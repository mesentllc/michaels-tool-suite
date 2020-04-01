package com.fedex.smartpost.mts.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ResetPackageStatus {
	private static final Log logger = LogFactory.getLog(ResetPackageStatus.class);
	private static final String AGG_WS = "http://pje03534.ground.fedex.com:14150/rodes-pkg-aggregator/service/updatePackageStatus?status=5&password=ChangeIsG00d&packageIds=";

	private void sendToWS(String urlRoot, String eventSeq) {
		if (eventSeq == null) {
			logger.info("No event sequences to be processed - exiting.");
			return;
		}
		String urlString = urlRoot + eventSeq;
		logger.info("Calling " + urlString);
		try {
			URL url = new URL(urlString);
			URLConnection urlConnection = url.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			reader.readLine();
			reader.close();
		}
		catch (Exception e) {
			logger.error("Exception: ", e);
		}
	}

	public void resetEventStatus(String filename) {
		StringBuilder packageIdList = new StringBuilder();
		int counter = 0;
		logger.info("Reading " + filename + " to read package ids to reset.");
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			while (br.ready()) {
				String packageId = br.readLine().trim();
				if (packageId.length() > 0) {
					packageIdList.append(packageId).append(",");
					counter++;
				}
			}
			logger.info(counter + " package ids read from " + filename);
			br.close();
		}
		catch (Exception e) {
			logger.error("Error: ", e);
		}
		String packageString = packageIdList.toString();
		if (packageString.endsWith(",")) {
			packageString = packageString.substring(0,packageString.length() - 1);
		}
		logger.info("Processing...");
		sendToWS(AGG_WS, packageString);
		logger.info("Completed...");
	}

	public static void main(String[] args) {
		ResetPackageStatus resetPackageStatus = new ResetPackageStatus();
		resetPackageStatus.resetEventStatus("D:/Support/2018-02-15/errorPackages.txt");
	}
}