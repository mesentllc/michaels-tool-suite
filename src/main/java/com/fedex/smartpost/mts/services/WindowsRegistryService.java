/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fedex.smartpost.mts.services;

import static com.sun.jna.platform.win32.WinReg.HKEY_CURRENT_USER;

import java.awt.Point;

import com.sun.jna.platform.win32.Advapi32Util;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.util.StringUtils;

public class WindowsRegistryService {
	private static final Log logger = LogFactory.getLog(WindowsRegistryService.class);
	private static final String REGISTRY_KEY_STRING = "Software\\FedExSmartPost\\MTS\\Database";
	private static final String MTS_SALT_PASSWORD = "MTS_IS_KEWL_OR_IS_IT_MES";
	private static final String ORACLE_USER_KEY = "Oracle User";
	private static final String TERADATA_USER_KEY = "TeraData User";
	private static final String PASSWORD_KEY = "Password";
	private static final String X_POS_KEY = "Windows X";
	private static final String Y_POS_KEY = "Windows Y";

	public static void setRegistryValues(String oracleUser, String teraDataUser, char[] password) {
		StandardPBEStringEncryptor textEncryptor = new StandardPBEStringEncryptor();
		textEncryptor.setPassword(MTS_SALT_PASSWORD);
		logger.info("Checking to see if the registry key exists.");
		if (!Advapi32Util.registryKeyExists(HKEY_CURRENT_USER, REGISTRY_KEY_STRING)) {
			logger.info("It doesn't so create the registry key.");
			Advapi32Util.registryCreateKey(HKEY_CURRENT_USER, REGISTRY_KEY_STRING);
		}
		logger.info("Setting the Oracle User registry key.");
		Advapi32Util.registrySetStringValue(HKEY_CURRENT_USER, REGISTRY_KEY_STRING, ORACLE_USER_KEY, oracleUser);
		logger.info("Setting the TeraDate User registry key.");
		Advapi32Util.registrySetStringValue(HKEY_CURRENT_USER, REGISTRY_KEY_STRING, TERADATA_USER_KEY, teraDataUser);
		logger.info("Setting the Password registry key.");
		Advapi32Util.registrySetStringValue(HKEY_CURRENT_USER, REGISTRY_KEY_STRING, PASSWORD_KEY, textEncryptor.encrypt(new String(password)));
	}

	public static boolean credentialsSet() {
		return !(StringUtils.isEmpty(WindowsRegistryService.getOracleUserFromRegistry())
				|| StringUtils.isEmpty(WindowsRegistryService.getTeraDataUserFromRegistry())
				|| StringUtils.isEmpty(WindowsRegistryService.getPasswordFromRegistry()));
	}

	public static String getOracleUserFromRegistry() {
		logger.info("Retrieving Oracle username from registry.");
		if (!Advapi32Util.registryKeyExists(HKEY_CURRENT_USER, REGISTRY_KEY_STRING)) {
			logger.info("Registry Key not setup - can't get key.");
			return null;
		}
		logger.info("Oracle username returned.");
		return Advapi32Util.registryGetStringValue(HKEY_CURRENT_USER, REGISTRY_KEY_STRING, ORACLE_USER_KEY);
	}

	public static String getTeraDataUserFromRegistry() {
		logger.info("Retrieving TeraData username from registry.");
		if (!Advapi32Util.registryKeyExists(HKEY_CURRENT_USER, REGISTRY_KEY_STRING)) {
			logger.info("Registry Key not setup - can't get key.");
			return null;
		}
		logger.info("TeraData username returned.");
		return Advapi32Util.registryGetStringValue(HKEY_CURRENT_USER, REGISTRY_KEY_STRING, TERADATA_USER_KEY);
	}

	public static String getPasswordFromRegistry() {
		StandardPBEStringEncryptor textEncryptor = new StandardPBEStringEncryptor();
		textEncryptor.setPassword(MTS_SALT_PASSWORD);
		logger.info("Retrieving Password from registry.");
		if (!Advapi32Util.registryKeyExists(HKEY_CURRENT_USER, REGISTRY_KEY_STRING)) {
			logger.info("Registry Key not setup - can't get key.");
			return null;
		}
		logger.info("Password returned.");
		return textEncryptor.decrypt(Advapi32Util.registryGetStringValue(HKEY_CURRENT_USER, REGISTRY_KEY_STRING, PASSWORD_KEY));
	}

	public static void setWindowLocation(Point point) {
		logger.info("Checking to see if the registry key exists.");
		if (!Advapi32Util.registryKeyExists(HKEY_CURRENT_USER, REGISTRY_KEY_STRING)) {
			logger.info("It doesn't so create the registry key.");
			Advapi32Util.registryCreateKey(HKEY_CURRENT_USER, REGISTRY_KEY_STRING);
		}
		logger.info("Saving window X location from registry.");
		Advapi32Util.registrySetIntValue(HKEY_CURRENT_USER, REGISTRY_KEY_STRING, X_POS_KEY, point.x);
		logger.info("Saving window Y location from registry.");
		Advapi32Util.registrySetIntValue(HKEY_CURRENT_USER, REGISTRY_KEY_STRING, Y_POS_KEY, point.y);
	}

	public static Point getWindowLocation() {
		int xPos = 0;
		int yPos = 0;
		logger.info("Retrieving window location from registry.");
		if (!Advapi32Util.registryKeyExists(HKEY_CURRENT_USER, REGISTRY_KEY_STRING)) {
			logger.info("Registry Key not setup - can't get key.");
			return new Point(0, 0);
		}
		try {
			xPos = Advapi32Util.registryGetIntValue(HKEY_CURRENT_USER, REGISTRY_KEY_STRING, X_POS_KEY);
			yPos = Advapi32Util.registryGetIntValue(HKEY_CURRENT_USER, REGISTRY_KEY_STRING, Y_POS_KEY);
		}
		catch (Exception e) {
		}
		return new Point(xPos, yPos);
	}
}
