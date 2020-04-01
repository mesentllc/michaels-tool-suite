package com.fedex.smartpost.mts.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GroupThread extends Thread {
	private static final Log logger = LogFactory.getLog(UnreleaseThread.class);
	private final BlockingQueue<String> messageStringQueue;
	private final int threadNumber;
	private AtomicLong totalCount = new AtomicLong();

	public GroupThread(int threadNumber, final BlockingQueue<String> messageStringQueue) {
		this.messageStringQueue = messageStringQueue;
		this.threadNumber = threadNumber;
	}

	private void group(String packageId) throws IOException {
		int base = 3534;
		URL url;
		URLConnection urlConnection;
		InputStream result;
		BufferedReader reader;
		boolean returnVal = false;

		url = new URL(String.format("http://pje%05d.ground.fedex.com:14150/rodes-pkg-aggregator/service/autoGroupPackage?packageId=%s",
		                            base + (threadNumber % 4), packageId));
		urlConnection = url.openConnection();
		result = urlConnection.getInputStream();
		reader = new BufferedReader(new InputStreamReader(result));
		boolean waiting = true;
		while (reader.readLine() != null) {
			if (waiting) {
				waiting = false;
				logger.info("Waiting...");
			}
		}
		reader.close();
		result.close();
	}

	@Override
	public void run() {
		int counter = 0;
		while (true) {
			String packageId = null;

			try {
				packageId = messageStringQueue.take();
			}
			catch (InterruptedException e) {
				logger.info("Thread " + threadNumber + " has been interrupted.", e);
			}
			if ("-1".equals(packageId)) {
				logger.info(String.format("Total grouped from Thread %d: %d", threadNumber, totalCount.get()));
				logger.info(String.format("Thread %d shutting down.", threadNumber));
				return;
			}
			logger.debug("Grouping: " + packageId);
			try {
				if (++counter % 1000 == 0) {
					logger.info("Thread #:" + threadNumber + " - processed " + counter + " records so far.");
				}
				group(packageId);
			}
			catch (IOException e) {
				logger.error("Error processing:" + packageId);
			}
			totalCount.addAndGet(1);
		}
	}
}