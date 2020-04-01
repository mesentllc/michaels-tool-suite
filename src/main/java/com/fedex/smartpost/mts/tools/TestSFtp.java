package com.fedex.smartpost.mts.tools;

import com.fedex.smartpost.mts.services.FtpFileService;
import com.fedex.smartpost.mts.services.FtpFileServiceImpl;
import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
public class TestSFtp {
	private static final String KEY_FILE = "/ssh/7575/vje56120.txt";

	public static void main(String[] args) {
		log.info("Attempting to connect to SFTP client.");
		FtpFileService service = new FtpFileServiceImpl(null, KEY_FILE, null);
		service.sendFile();
		log.info("Success!");
	}
}
