package com.fedex.smartpost.mts.factory;

import com.fedex.smartpost.mts.gateway.edw.TeraDataDao;
import com.fedex.smartpost.mts.gateway.edw.TeraDataDaoImpl;
import com.fedex.smartpost.mts.services.WindowsRegistryService;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;

public class TeraDataDaoFactory {
	private DataSource getDataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("com.teradata.jdbc.TeraDriver");
		dataSource.setUrl("jdbc:teradata://edwadhoccop1.prod.fedex.com/smartpost_eds_prod_view_db");
		dataSource.setUsername(WindowsRegistryService.getTeraDataUserFromRegistry());
		dataSource.setPassword(WindowsRegistryService.getPasswordFromRegistry());
		dataSource.setMaxActive(5);
		dataSource.setMaxIdle(1);
		dataSource.setDefaultReadOnly(true);
		return dataSource;
	}

	public TeraDataDao getTeraDataDao() {
		TeraDataDaoImpl teraDataDao = new TeraDataDaoImpl();
		teraDataDao.setDataSource(getDataSource());
		return teraDataDao;
	}
}
