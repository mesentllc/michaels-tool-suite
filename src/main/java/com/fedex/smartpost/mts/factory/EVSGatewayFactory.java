package com.fedex.smartpost.mts.factory;

import com.fedex.smartpost.mts.gateway.evs.EvsManifestImpl;
import com.fedex.smartpost.mts.gateway.rodes.RodesGateway;
import com.fedex.smartpost.mts.gateway.rodes.RodesGatewayImpl;
import com.fedex.smartpost.mts.services.WindowsRegistryService;
import org.apache.commons.dbcp.BasicDataSource;

import javax.sql.DataSource;

public class EVSGatewayFactory {
	private DataSource createDataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
		dataSource.setUrl("jdbc:oracle:thin:@ldap://oidprd.gss.ground.fedex.com:389/SPEVS_USR_SVC1_PRD,cn=OracleContext,dc=ground,dc=fedex,dc=com");
		dataSource.setUsername(WindowsRegistryService.getOracleUserFromRegistry());
		dataSource.setPassword(WindowsRegistryService.getPasswordFromRegistry());
		dataSource.setPoolPreparedStatements(true);
		dataSource.setMaxActive(10);
		dataSource.setMaxIdle(1);
		dataSource.setDefaultAutoCommit(false);
		return dataSource;
	}

	public EvsManifestImpl createEvsManifest() {
		return new EvsManifestImpl(createDataSource());
	}
}
