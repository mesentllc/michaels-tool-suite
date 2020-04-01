package com.fedex.smartpost.mts.factory;

import com.fedex.smartpost.mts.model.Message;
import com.fedex.smartpost.mts.thread.UnmanifestedPublisherThread;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.core.JmsTemplate;

public class PublisherThreadFactoryImpl implements PublisherThreadFactory {
	private JmsTemplate publisher;

	@Override
	public UnmanifestedPublisherThread createBean(int threadNumber, BlockingQueue<List<Message>> messageStringQueue) {
		return new UnmanifestedPublisherThread(threadNumber, messageStringQueue, publisher);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		publisher = (JmsTemplate)applicationContext.getBean("jmsTemplate");
	}
}
