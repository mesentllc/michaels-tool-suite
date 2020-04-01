package com.fedex.smartpost.mts.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.fedex.smartpost.mts.services.AdminService;
import com.fedex.smartpost.mts.thread.UnreleaseThread;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public final class UnreleaseBgs {
	private static final Log logger = LogFactory.getLog(UnreleaseBgs.class);
//	private static final String urlRoot = "http://sje00848.ground.fedex.com:14130/rodes-scheduler/serviceInitiator/resetBillingGroup?bg_seq=";
	private static final String urlRoot = "http://pje03534.ground.fedex.com:14150/rodes-pkg-aggregator/service/resetBillingGroup?bg_seq=";
	private AdminService adminService;

	public UnreleaseBgs() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-unrelease.xml");
		adminService = (AdminService)context.getBean("adminService");
	}

	private static void resetBgStatus(String filename) {
		try {
			try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
				while (br.ready()) {
					String urlString = urlRoot + br.readLine().trim();
					logger.info("Calling " + urlString);
					URL url = new URL(urlString);
					URLConnection urlConnection = url.openConnection();
					BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
					reader.readLine();
					reader.close();
				}
			}
		}
		catch (Exception e) {
			logger.error("Error: ", e);
		}
	}

	public void process(String filename, int threadCount) {
		BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
		List<UnreleaseThread> threadList = new ArrayList<>(threadCount);
		int totalCount = 0;

		try {
			logger.info("Reading " + filename + " for BGs to release.");
			try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
				while (br.ready()) {
					messageQueue.add(br.readLine().trim());
					totalCount++;
				}
			}
			logger.info(totalCount + " records read.");
			for (int ptr = 0; ptr < threadCount; ptr++) {
				UnreleaseThread unreleaseThread = new UnreleaseThread(adminService, ptr, messageQueue);
				threadList.add(unreleaseThread);
				unreleaseThread.start();
			}
			for (UnreleaseThread ut : threadList) {
				messageQueue.add("-1");
			}
			for (UnreleaseThread ut : threadList) {
				ut.join();
			}
			logger.info("Completed the unreleasing of the BGs. - Now setting BG status to 1.");
			resetBgStatus(filename);
		}
		catch (Exception e) {
			logger.error("Error: ", e);
		}
	}
}
