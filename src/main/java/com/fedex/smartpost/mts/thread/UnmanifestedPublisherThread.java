package com.fedex.smartpost.mts.thread;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import com.fedex.smartpost.mts.model.Message;
import com.fedex.smartpost.mts.services.JmsMessageCreator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.springframework.jms.core.JmsTemplate;

public class UnmanifestedPublisherThread extends Thread {
	private static final Log logger = LogFactory.getLog(UnmanifestedPublisherThread.class);
	private int threadNumber;
	private final BlockingQueue<List<Message>> messageStringQueue;
	private JmsTemplate publisher;
	private AtomicLong packageCount = new AtomicLong();

	public UnmanifestedPublisherThread(int threadNumber, final BlockingQueue<List<Message>> messageStringQueue, JmsTemplate publisher) {
		this.publisher = publisher;
		this.threadNumber = threadNumber;
		this.messageStringQueue = messageStringQueue;
	}

	@Override
	public void run() {
		while (true) {
			List<Message> list = null;
			try {
				list = messageStringQueue.take();
			}
			catch (InterruptedException ex) {
				logger.info("Thread " + threadNumber + " has been interrupted.", ex);
			}
			if (list == null || list.isEmpty()) {
				logger.info(String.format("Total messages published from Thread %d: %d", threadNumber, packageCount.get()));
				logger.info(String.format("Thread %d shutting down.", threadNumber));
				return;
			}
			for (Message message : list) {
				Properties properties = new Properties();
				properties.put("MsgCreateTmstp", new DateTime().toString());
				properties.put("MsgSource", "fxsp-evs-process-postage");
				properties.put("MsgVsn", "1.0");
				properties.put("EventType", "PROCESSPOSTAGE");
				properties.put("ParcelId", message.getPackageId());
				properties.put("ReleaseTypeCode", "U");
				publisher.send(new JmsMessageCreator(message.getPayload(), properties));
				long printedValue = packageCount.incrementAndGet();
				if (printedValue % 1000 == 0) {
					logger.info(String.format("Messages published from Thread %d: %d", threadNumber, printedValue));
				}
			}
		}
	}
}
