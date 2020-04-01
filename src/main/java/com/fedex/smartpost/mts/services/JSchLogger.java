package com.fedex.smartpost.mts.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jcraft.jsch.Logger;

public class JSchLogger implements Logger {
	private static final Log log = LogFactory.getLog(JSchLogger.class);

	@Override
	public boolean isEnabled(int i) {
		return true;
	}

	@Override
	public void log(int i, String s) {
		switch (i) {
			case Logger.DEBUG:
				log.debug(s);
				break;
			case Logger.INFO:
				log.info(s);
				break;
			case Logger.WARN:
				log.warn(s);
				break;
			case Logger.ERROR:
				log.error(s);
				break;
			case Logger.FATAL:
				log.fatal(s);
		}
	}
}
