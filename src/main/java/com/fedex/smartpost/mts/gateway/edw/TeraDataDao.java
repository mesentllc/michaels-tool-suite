package com.fedex.smartpost.mts.gateway.edw;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public interface TeraDataDao {
	List<String[]> retrieveDashboardResults() throws SQLException;

	List<String> getReleasedPackages(Set<String> packageList) throws SQLException;
}
