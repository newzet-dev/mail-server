package com.newzet.api.common.batch;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamReceiver;

import com.newzet.api.common.batch.config.BatchConfig;
import com.newzet.api.common.objectMapper.OptionalObjectMapper;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractBatchConsumer<T> implements BatchConsumer {

	protected final RedisTemplate<String, String> redisTemplate;
	protected final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
	protected final BatchConfig batchConfig;
	protected final OptionalObjectMapper optionalObjectMapper;
	protected final AtomicBoolean isProcessing = new AtomicBoolean(false);
	protected ExecutorService executorService;
	protected ExecutorService ackExecutorService;

	@PostConstruct
	public void init() {
		try {
			Boolean exists = redisTemplate.hasKey(getStreamKey());
			if (Boolean.FALSE.equals(exists)) {
				Map<String, String> dummy = new HashMap<>();
				dummy.put("init", "init");
				redisTemplate.opsForStream().add(getStreamKey(), dummy);
				log.info("Created new Redis Stream: {}", getStreamKey());
			}

			try {
				redisTemplate.opsForStream().createGroup(getStreamKey(), getConsumerGroup());
				log.info("Created new consumer group: {} for stream: {}", getConsumerGroup(),
					getStreamKey());
			} catch (Exception e) {
				log.debug("Consumer group may already exist: {}", e.getMessage());
			}
		} catch (Exception e) {
			log.error("Failed to initialize Redis Stream: {}", e.getMessage(), e);
		}
	}

	@Override
	public Map<String, Object> getBatchStatus() {
		Map<String, Object> status = new HashMap<>();
		status.put("isProcessing", isProcessing.get());
		status.put("batchSize", batchConfig.getBatchSize());

		Long pendingCount = redisTemplate.opsForStream().size(getStreamKey());
		pendingCount = (pendingCount != null && pendingCount > 0) ? pendingCount - 1 : 0;
		status.put("pendingMessages", pendingCount != null ? pendingCount : 0);

		return status;
	}

	@Override
	public void startProcessing() {
		if (isProcessing.compareAndSet(false, true)) {
			executorService = Executors.newSingleThreadExecutor();
			ackExecutorService = Executors.newSingleThreadExecutor();
			executorService.submit(this::processBatchesAsync);
			log.info("{} batch processor started with configuration: size={}, timeout={}s",
				getProcessorTypeName(), batchConfig.getBatchSize(),
				batchConfig.getTimeoutSeconds());
		} else {
			log.warn("{} batch processor is already running", getProcessorTypeName());
		}
	}

	@Override
	public void stopProcessing() {
		if (isProcessing.compareAndSet(true, false)) {
			if (executorService != null) {
				executorService.shutdownNow();
				log.info("{} batch processor stopped", getProcessorTypeName());
			}
			if (ackExecutorService != null) {
				ackExecutorService.shutdownNow();
			}
		}
	}

	protected void processBatchesAsync() {
		log.info("{} batch processing thread initialized", getProcessorTypeName());

		try {
			StreamReceiver.StreamReceiverOptions<String, MapRecord<String, String, String>> options =
				StreamReceiver.StreamReceiverOptions.builder()
					.pollTimeout(Duration.ofSeconds(1))
					.build();

			StreamReceiver<String, MapRecord<String, String, String>> receiver =
				StreamReceiver.create(reactiveRedisTemplate.getConnectionFactory(), options);

			receiver.receive(
					Consumer.from(getConsumerGroup(), getConsumerName()),
					StreamOffset.create(getStreamKey(), ReadOffset.lastConsumed())
				)
				.bufferTimeout(batchConfig.getBatchSize(),
					Duration.ofSeconds(batchConfig.getTimeoutSeconds()))
				.doOnNext(records -> {
					if (!records.isEmpty()) {
						log.info("Processing {} batch: count={}", getProcessorTypeName(),
							records.size());
						processBatchWithAck(records);
					}
				})
				.doOnError(
					error -> log.error("{} stream processing error: {}", getProcessorTypeName(),
						error.getMessage(),
						error))
				.subscribe();

			log.info("Subscribed to {} Redis Stream with consumer group: {}, consumer: {}",
				getProcessorTypeName(), getConsumerGroup(), getConsumerName());
		} catch (Exception e) {
			log.error("Failed to initialize {} batch processor: {}", getProcessorTypeName(),
				e.getMessage(), e);
			isProcessing.set(false);
		}
	}

	protected void processBatchWithAck(List<MapRecord<String, String, String>> records) {
		List<T> items = new ArrayList<>();

		for (MapRecord<String, String, String> record : records) {
			try {
				String data = record.getValue().get("data");
				if (data != null) {
					Optional<T> itemOpt = optionalObjectMapper.deserialize(data, getItemClass());
					if (itemOpt.isPresent()) {
						items.add(itemOpt.get());
					} else {
						log.warn("Failed to deserialize {} from record: {}", getProcessorTypeName(),
							record.getId());
					}
				}
			} catch (Exception e) {
				log.error("Error processing {} record {}: {}", getProcessorTypeName(),
					record.getId(), e.getMessage(), e);
			}
		}

		processBatchItems(items);

		CompletableFuture.runAsync(() -> {
			List<String> ackedMessageIds = new ArrayList<>();

			for (MapRecord<String, String, String> record : records) {
				String messageId = record.getId().getValue();
				try {
					redisTemplate.opsForStream()
						.acknowledge(getStreamKey(), getConsumerGroup(), messageId);
					ackedMessageIds.add(messageId);
				} catch (Exception e) {
					log.warn("Ack failed for {} message {}, skipping for now: {}",
						getProcessorTypeName(), messageId, e.getMessage());
				}
			}

			if (!ackedMessageIds.isEmpty()) {
				try {
					redisTemplate.opsForStream()
						.delete(getStreamKey(), ackedMessageIds.toArray(new String[0]));
				} catch (Exception e) {
					log.error("Failed to delete acked {} messages: {}", getProcessorTypeName(),
						e.getMessage(), e);
				}
			}
		}, ackExecutorService);
	}

	@PreDestroy
	public void onShutdown() {
		stopProcessing();
		if (executorService != null && !executorService.isShutdown()) {
			executorService.shutdown();
		}
		if (ackExecutorService != null && !ackExecutorService.isShutdown()) {
			ackExecutorService.shutdown();
		}
		log.info("{} batch processor shutdown completed", getProcessorTypeName());
	}

	protected abstract String getStreamKey();

	protected abstract String getConsumerGroup();

	protected abstract String getConsumerName();

	protected abstract String getProcessorTypeName();

	protected abstract Class<T> getItemClass();

	protected abstract void processBatchItems(List<T> items);
}
