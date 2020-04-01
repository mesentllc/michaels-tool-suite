package com.fedex.smartpost.mts.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import com.fedex.smartpost.mts.services.AdminService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UnreleaseThread extends Thread {
	private static final Log logger = LogFactory.getLog(UnreleaseThread.class);
	private final BlockingQueue<String> messageStringQueue;
	private final int threadNumber;
	private AtomicLong totalCount = new AtomicLong();
	private AdminService adminService;

	public UnreleaseThread(AdminService adminService, int threadNumber, final BlockingQueue<String> messageStringQueue) {
		this.messageStringQueue = messageStringQueue;
		this.threadNumber = threadNumber;
		this.adminService = adminService;
	}

	@Override
	public void run() {
		while (true) {
			String billingGroup = null;

			try {
				billingGroup = messageStringQueue.take();
			}
			catch (InterruptedException e) {
				logger.info("Thread " + threadNumber + " has been interrupted.", e);
			}
			if ("-1".equals(billingGroup)) {
				logger.info(String.format("Total unreleased from Thread %d: %d", threadNumber, totalCount.get()));
				logger.info(String.format("Thread %d shutting down.", threadNumber));
				return;
			}
			logger.info("Unreleasing BG: " + billingGroup);
			adminService.manualUnrelease(Long.parseLong(billingGroup));
			totalCount.addAndGet(1);
		}
	}
}