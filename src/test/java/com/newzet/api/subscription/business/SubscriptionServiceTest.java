package com.newzet.api.subscription.business;

import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.newzet.api.subscription.business.service.SubscriptionQueryRepository;
import com.newzet.api.subscription.business.service.SubscriptionRepository;
import com.newzet.api.subscription.business.service.SubscriptionService;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

	@Mock
	private SubscriptionRepository subscriptionRepository;
	@Mock
	private SubscriptionQueryRepository subscriptionQueryRepository;
	@InjectMocks
	private SubscriptionService subscriptionService;

	@Test
	public void addSubscriptionIfUnsubscribed_whenSubscribed_saveSubscription() {
	    //Given
		UUID userId = UUID.randomUUID();
		String fromName = "name";
		String fromDomain = "domain";
		String mailingList = "mailingList";
		when(subscriptionQueryRepository.isSubscribe(any(), any(), any())).thenReturn(false);

	    //When
		subscriptionService.addSubscriptionIfUnsubscribed(userId, fromName, fromDomain, mailingList);

	    //Then
		verify(subscriptionRepository, times(1)).save(userId, fromName, fromDomain, mailingList);
	}

	@Test
	public void addSubscriptionIfSubscribed_whenUnSubscribed_doNothing() {
		//Given
		when(subscriptionQueryRepository.isSubscribe(any(), any(), any())).thenReturn(true);

		//When
		subscriptionService.addSubscriptionIfUnsubscribed(UUID.randomUUID(), "testName", "testDomain", "testMailingList");

		//Then
		verify(subscriptionRepository, never()).save(any(), any(), any(), any());
	}
}
