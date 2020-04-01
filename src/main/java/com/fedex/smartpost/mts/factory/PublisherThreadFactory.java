package com.fedex.smartpost.mts.factory;

import com.fedex.smartpost.mts.model.Message;
import com.fedex.smartpost.mts.thread.UnmanifestedPublisherThread;
import org.springframework.context.ApplicationContextAware;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public interface PublisherThreadFactory extends ApplicationContextAware {
	UnmanifestedPublisherThread createBean(int threadNumber, final BlockingQueue<List<Message>> messageStringQueue);
}
