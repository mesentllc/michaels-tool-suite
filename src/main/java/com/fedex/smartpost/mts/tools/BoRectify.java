package com.fedex.smartpost.mts.tools;

import com.fedex.smartpost.mts.factory.EVSGatewayFactory;
import com.fedex.smartpost.mts.gateway.evs.EvsManifest;
import com.fedex.smartpost.mts.services.WindowsRegistryService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public class BoRectify {
	private static final Log log = LogFactory.getLog(BoRectify.class);
	private EvsManifest evsManifest;

	private BoRectify() throws Exception {
		if (WindowsRegistryService.credentialsSet()) {
			log.info("Acquiring the EVS Manifest DAO.");
			evsManifest = new EVSGatewayFactory().createEvsManifest();
		}
		else {
			log.error("Attempting to reset events without first setting database credentials.");
			throw new Exception("Must set credentials first");
		}
	}

	private void process(String tran_id) {
		List<String> orphans = evsManifest.retrieveOrphans(tran_id);
		if (orphans.size() > 0) {
			evsManifest.logOrphanSummary(orphans);
		}
	}

	public static void main(String[] args) throws Exception {
		BoRectify rectify = new BoRectify();
		rectify.process("201906030001");
	}
}
