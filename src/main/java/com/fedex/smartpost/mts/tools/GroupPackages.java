package com.fedex.smartpost.mts.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.fedex.smartpost.mts.thread.GroupThread;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GroupPackages {
	private static final Log logger = LogFactory.getLog(GroupPackages.class);

	public static void main(String[] args) {
		GroupPackages groupPackages = new GroupPackages();
		groupPackages.process();
	}

	private void process() {
		int threadCount = 10;
		BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
		List<GroupThread> threadList = new ArrayList<>(threadCount);
		int totalCount = 0;
		String filename = "D:\\Support\\2018-02-15\\errorPackages.txt";

		try {
			logger.info("Reading " + filename + " for package ids to group.");
			BufferedReader br = new BufferedReader(new FileReader(filename));
			while (br.ready()) {
				messageQueue.add(br.readLine().trim());
				totalCount++;
			}
			br.close();
			logger.info(totalCount + " records read.");
			for (int ptr = 0; ptr < threadCount; ptr++) {
				GroupThread groupThread = new GroupThread(ptr, messageQueue);
				threadList.add(groupThread);
				groupThread.start();
			}
			for (GroupThread ut : threadList) {
				messageQueue.add("-1");
			}
			for (GroupThread ut : threadList) {
				ut.join();
			}
			logger.info("Completed the grouping the packages.");
		}
		catch (Exception e) {
			logger.error("Error: ", e);
		}
	}
}
