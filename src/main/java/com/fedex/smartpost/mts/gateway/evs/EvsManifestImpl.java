package com.fedex.smartpost.mts.gateway.evs;

import com.fedex.smartpost.common.io.classpath.ClassPathResourceUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class EvsManifestImpl extends NamedParameterJdbcTemplate implements EvsManifest {
	private static final Log log = LogFactory.getLog(EvsManifest.class);
	private static final String RETRIEVE_ORPHANS_SQL = ClassPathResourceUtil.getString("sql/evs/RetrieveOrphanedManifests.sql");
	private static final String RETRIEVE_ORPHAN_SUMMARY_SQL = ClassPathResourceUtil.getString("sql/evs/RetrieveOrphanedManifestSummary.sql");

	public EvsManifestImpl(DataSource dataSource) {
		super(dataSource);
	}

	private class ManifestSummary {
		int pkgCount;
		float postage;
	}

	private RowMapper<ManifestSummary> SUMMARY_RM = (rs, i) -> {
		ManifestSummary summary = new ManifestSummary();
		summary.pkgCount = rs.getInt("count");
		summary.postage = rs.getFloat("postage");
		return summary;
	};

	@Override
	public List<String> retrieveOrphans(String tran_id) {
		log.info("Attempting to retrieve orphaned manifests for tran_id " + tran_id);
		MapSqlParameterSource parameters = new MapSqlParameterSource("tran_id", tran_id);
		List<String> manifests = queryForList(RETRIEVE_ORPHANS_SQL, parameters, String.class);
		log.info("Found " + manifests.size() + " orphaned ids.");
		return manifests;
	}

	@Override
	public void logOrphanSummary(List<String> orphanedManifests) {
		log.info("Attempting to get summary for " + orphanedManifests.size() + " manifests.");
		MapSqlParameterSource parameters = new MapSqlParameterSource("orphans", orphanedManifests);
		ManifestSummary summary = query(RETRIEVE_ORPHAN_SUMMARY_SQL, parameters, SUMMARY_RM).get(0);
		log.info("Summary: " + summary.pkgCount + " packages for postage amount: " + summary.postage);
	}
}
