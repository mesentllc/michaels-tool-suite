package com.fedex.smartpost.mts.gateway.edw;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.fedex.smartpost.common.io.classpath.ClassPathResourceUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

public class TeraDataDaoImpl extends JdbcTemplate implements TeraDataDao {
	private static final String V_CONTAINER_MG = ClassPathResourceUtil.getString("sql/dashboard/CreateVolatileContainerMGTable.sql");
	private static final String V_CONTAINER_MV = ClassPathResourceUtil.getString("sql/dashboard/CreateVolatileContainerMVTable.sql");
	private static final String V_PKG_TRAN_HUB = ClassPathResourceUtil.getString("sql/dashboard/CreateVolatilePackageTranHubTable.sql");
	private static final String V_PKG_TRAN_HUB_UPN = ClassPathResourceUtil.getString("sql/dashboard/CreateVolatilePackageTranHubUPNTable.sql");
	private static final String DASHBOARD_SQL = ClassPathResourceUtil.getString("sql/dashboard/Dashboard.sql");
	private static final String CREATE_VOLATILE_TABLE = "create volatile table packages (pkg_barcd_nbr varchar(30)) on commit preserve rows";
	private static final String DROP_VOLATILE_TABLE = "drop table packages";
	private static final String GET_RELEASED_PACKAGES_FROM_RRR = ClassPathResourceUtil.getString("sql/retrieveRoRelPackagesFromRRR.sql");
	private static final Log logger = LogFactory.getLog(TeraDataDao.class);

	private String[] retrieveColumnNames(ResultSet rs) throws SQLException {
		String[] row;
		int columnCount;
		logger.info("Executing retrieveColumnNames");
		if (rs == null) {
			return null;
		}
		ResultSetMetaData rsmd = rs.getMetaData();
		columnCount = rsmd.getColumnCount();
		row = new String[columnCount];
		for (int i = 1; i <= columnCount; i++) {
			logger.info("Reading name of column " + i + " of " + columnCount);
			row[i - 1] = rsmd.getColumnName(i);
		}
		logger.info("Exiting retrieveColumnNames");
		return row;
	}

	private List<String[]> retrieveData(ResultSet rs) throws SQLException {
		List<String[]> rows = new ArrayList<String[]>();
		String[] row;
		int columnCount;
		int rowCount = 0;
		logger.info("Executing retrieveData");
		if (rs == null) {
			return null;
		}
		ResultSetMetaData rsmd = rs.getMetaData();
		columnCount = rsmd.getColumnCount();
		while (rs.next()) {
			logger.info("Reading row " + ++rowCount);
			row = new String[columnCount];
			for (int i = 1; i <= columnCount; i++) {
				logger.info("Reading column " + i + " of " + columnCount);
				row[i - 1] = rs.getString(i);
			}
			logger.info("Adding row to list.");
			rows.add(row);
		}
		logger.info("Exiting retrieveData");
		return rows;
	}

	@Override
	public List<String[]> retrieveDashboardResults() throws SQLException {
		List<String[]> result = new ArrayList<String[]>();
		String[] row;
		logger.info("Executing retrieveDashboardResults");
		logger.info("Setting Connection variable.");
		Connection con = DataSourceUtils.getConnection(getDataSource());
		logger.info("Setting Statement variable.");
		Statement s = con.createStatement();
		logger.info("Retrieving MV Table.");
		s.executeQuery(V_CONTAINER_MV);
		logger.info("Retrieving MG Table.");
		s.executeQuery(V_CONTAINER_MG);
		logger.info("Retrieving Tran Hub Set.");
		s.executeQuery(V_PKG_TRAN_HUB);
		logger.info("Retrieving Tran Hub UPN Table.");
		s.executeQuery(V_PKG_TRAN_HUB_UPN);
		logger.info("Retrieving Dashboard Results.");
		ResultSet rs = s.executeQuery(DASHBOARD_SQL);
		row = retrieveColumnNames(rs);
		if (row == null) {
			logger.info("No results from retrieveColumnNames.");
			return result;
		}
		result.add(row);
		result.addAll(retrieveData(rs));
		logger.info("Exiting retrieveDashboardResults");
		return result;
	}

	@Override
	public List<String> getReleasedPackages(Set<String> packageList) throws SQLException {
		List<String> released = new ArrayList<String>();
		int packageCount = 0;
		int batchCount = 0;
		Connection conn = DataSourceUtils.getConnection(getDataSource());
		Statement stmt = conn.createStatement();
		stmt.execute(CREATE_VOLATILE_TABLE);
		PreparedStatement ps = conn.prepareStatement("insert into packages (?)");
		for (String item : packageList) {
			if ((++batchCount % 5000) == 0) {
				ps.executeBatch();
			}
			ps.setString(1, item);
			ps.addBatch();
		}
		ps.executeBatch();
		ps.close();
		ResultSet rs = stmt.executeQuery(GET_RELEASED_PACKAGES_FROM_RRR);
		while (rs.next()) {
			released.add(rs.getString("pkg_barcd_nbr"));
			packageCount++;
		}
		logger.info("Number of UNRELEASED package ids using RODES_RATING_RELEASE: " + packageCount);
		rs.close();
		stmt.execute(DROP_VOLATILE_TABLE);
		stmt.close();
		return released;
	}
}
