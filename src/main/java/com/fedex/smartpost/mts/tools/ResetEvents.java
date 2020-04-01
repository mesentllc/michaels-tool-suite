package com.fedex.smartpost.mts.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fedex.smartpost.common.io.classpath.ClassPathResourceUtil;
import com.fedex.smartpost.mts.factory.RODeSGatewayFactory;
import com.fedex.smartpost.mts.gateway.rodes.RodesGateway;
import com.fedex.smartpost.mts.services.WindowsRegistryService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ResetEvents {
	private static final Log logger = LogFactory.getLog(ResetEvents.class);
	private static final String PDE_WS = "http://pje03534.ground.fedex.com:14150/rodes-pkg-aggregator/service/resetDomesticEvents?state=NEW&reason=RESET&eventSeq=";
	private static final String PRE_WS = "http://pje03534.ground.fedex.com:14150/rodes-pkg-aggregator/service/resetReturnsEvents?state=NEW&reason=RESET&eventSeq=";
	private static final String DOM_PKG_ID_TO_SEQ_SQL = ClassPathResourceUtil.getString("sql/packageIdToDomEventSeq.sql");
	private static final String RTN_PKG_ID_TO_SEQ_SQL = ClassPathResourceUtil.getString("sql/packageIdToRtnEventSeq.sql");
	private RodesGateway rodesGateway;

	public ResetEvents() throws Exception {
		if (WindowsRegistryService.credentialsSet()) {
			logger.info("Acquiring the RODeS Gateway.");
			rodesGateway = new RODeSGatewayFactory().createGateway();
		}
		else {
			logger.error("Attempting to reset events without first setting database credentials.");
			throw new Exception("Must set credentials first");
		}
	}

	private Map<Boolean, List<Long>> convertPackageIdsToEventSequences(List<String> packageIds) {
		Map<Boolean, List<Long>> processMap = new HashMap<>();
		logger.info("Adding the Domestic Event Sequences to the processing map.");
		processMap.put(false, rodesGateway.retrieveEventSeqFromPkgIds(DOM_PKG_ID_TO_SEQ_SQL, packageIds));
		logger.info("Adding the Returns Event Sequences to the processing map.");
		processMap.put(true, rodesGateway.retrieveEventSeqFromPkgIds(RTN_PKG_ID_TO_SEQ_SQL, packageIds));
		return processMap;
	}

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

	private String convertListToString(List<Long> longList) {
		logger.info("Converting the list of BG_SEQs to a CSV.");
		StringBuilder sb = new StringBuilder();
		for (Long value : longList) {
			sb.append(value).append(',');
		}
		if (sb.length() > 0) {
			return sb.substring(0, sb.length() - 1);
		}
		return null;
	}

	public void resetEventStatus(String filename) {
		List<String> packageIdList = new ArrayList<>();
		logger.info("Reading " + filename + " to aquire BG_SEQs to reset.");
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			while (br.ready()) {
				packageIdList.add(br.readLine().trim());
			}
			logger.info(packageIdList.size() + " package ids read from " + filename);
			br.close();
		}
		catch (Exception e) {
			logger.error("Error: ", e);
		}
		logger.info("Convert package ids to BG_SEQs.");
		Map<Boolean, List<Long>> processMap = convertPackageIdsToEventSequences(packageIdList);
		logger.info("Processing the DOMESTIC BG_SEQs.");
		sendToWS(PDE_WS, convertListToString(processMap.get(false)));
		logger.info("Processing the RETURN BG_SEQs.");
		sendToWS(PRE_WS, convertListToString(processMap.get(true)));
	}
}
