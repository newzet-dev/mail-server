package com.newzet.api.common.batch;

public interface BatchProducer<T> {
	void addToBatch(T item);
}
