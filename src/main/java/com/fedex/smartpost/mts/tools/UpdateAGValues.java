package com.fedex.smartpost.mts.tools;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;
import java.util.TreeSet;

public final class UpdateAGValues {
	private static final Log logger = LogFactory.getLog(UpdateAGValues.class);
//	private static final String urlRoot = "http://sje00848.ground.fedex.com:14130/rodes-scheduler/serviceInitiator/setAutogrouping?fedexCustAcct=";
	private static final String urlRoot = "http://pje03534.ground.fedex.com:14150/rodes-pkg-aggregator/service/setAutogrouping?fedexCustAcct=";

	private void resetAGValue(String fedexId, boolean b) throws IOException {
		String urlString = urlRoot + fedexId + "&turnOn=" + b;
		logger.info("Calling " + urlString);
		URL url = new URL(urlString);
		URLConnection urlConnection = url.openConnection();
		BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		while (reader.ready()) {
			logger.info(reader.readLine());
		}
		reader.close();
	}

	public void process(String filename, int offOnValue) {
		Set<String> fedexCustomerAccounts = new TreeSet<>();
		int totalCount = 0;

		try {
			logger.info("Reading " + filename + " for FedEx Customer Ids to set Autogrouping values.");
			try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
				while (br.ready()) {
					fedexCustomerAccounts.add(br.readLine().trim());
					totalCount++;
				}
			}
			logger.info(totalCount + " records read, " + fedexCustomerAccounts.size() + " unique FedEx Ids.");
			for (String fedexId : fedexCustomerAccounts) {
				resetAGValue(fedexId, offOnValue == 1);
			}
		}
		catch (Exception e) {
			logger.error("Error: ", e);
		}
	}
}
