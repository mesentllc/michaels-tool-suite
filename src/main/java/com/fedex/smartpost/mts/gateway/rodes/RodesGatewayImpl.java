package com.fedex.smartpost.mts.gateway.rodes;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class RodesGatewayImpl extends NamedParameterJdbcTemplate implements RodesGateway {
	private static final Log logger = LogFactory.getLog(RodesGateway.class);

	public RodesGatewayImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public List<Long> retrieveEventSeqFromPkgIds(String sql, List<String> packageIds) {
		int maxPointer;
		int curPointer = 0;
		List<Long> eventSeqList = new ArrayList<>();

		while (packageIds.size() > curPointer) {
			maxPointer = Math.min(1000, packageIds.size() - curPointer);
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("packageIds", packageIds.subList(curPointer, curPointer + maxPointer));
			eventSeqList.addAll(queryForList(sql, parameters, Long.class));
			curPointer += maxPointer;
		}
		return eventSeqList;
	}
}
