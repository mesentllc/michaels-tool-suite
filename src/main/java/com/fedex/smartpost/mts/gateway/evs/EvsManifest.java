package com.fedex.smartpost.mts.gateway.evs;

import java.util.List;

public interface EvsManifest {
	List<String> retrieveOrphans(String tran_id);
	void logOrphanSummary(List<String> orphanedManifests);
}
