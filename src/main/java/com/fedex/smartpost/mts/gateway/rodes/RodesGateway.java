package com.fedex.smartpost.mts.gateway.rodes;

import java.util.List;

public interface RodesGateway {
	List<Long> retrieveEventSeqFromPkgIds(String sql, List<String> packageIds);
}
