package com.fedex.smartpost.mts.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fedex.smartpost.common.exception.UnrecoverableException;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class FtpFileServiceImpl implements FtpFileService {
	private static final Log log = LogFactory.getLog(FtpFileService.class);
	private JSch jSch;
	private String username;
	private String passphrase;
	private String keyFile;

	public FtpFileServiceImpl(String username, String keyFile, String passphrase) {
		this.username = username;
		this.passphrase = passphrase;
		this.keyFile = keyFile;
	}

	public void setjSch(JSch jSch) {
		this.jSch = jSch;
	}

	private static String getHost() {
		try {
			String hostName = InetAddress.getLocalHost().getHostName();
			if (hostName != null) {
				int firstPeriod = hostName.indexOf(".");
				if (firstPeriod > 0) {
					hostName = hostName.substring(0, firstPeriod);
				}
				return hostName;
			}
		}
		catch (UnknownHostException e) {
			return "Unknown";
		}
		return "Unknown";
	}

	private JSch getJsch() {
		if (jSch == null) {
			jSch = new JSch();
		}
		return jSch;
	}

	private void identitySetup() {
		try {
			// /opt/fedex/sptl/.ssh/id_dsa
			if (username != null && StringUtils.isNotEmpty(passphrase)) {
				jSch.addIdentity(username, IOUtils.toByteArray(new FileInputStream(keyFile)), null, passphrase.getBytes());
			}
			else {
				jSch.addIdentity(keyFile);
			}
		}
		catch (IOException e) {
			throw new UnrecoverableException("Could not open private key file", e, false);
		}
		catch (JSchException e) {
			throw new UnrecoverableException("Could not create SSH identity", e, false);
		}
	}

	@Override
	public void sendFile() {
		String host = "sftpgixmetadataqa.fxg.ds.fedex.com";
		log.debug("Sending SFTP.");
		JSch jsch = getJsch();
		JSch.setLogger(new JSchLogger());
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		identitySetup();
		try {
			Session session = jsch.getSession(username, host);
			session.setConfig(config);
			session.connect();
			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp sftp = (ChannelSftp)channel;
//			sftp.put(rootDir + "/rolling.txt", target);
			sftp.exit();
			session.disconnect();
		}
		catch (JSchException e) {
//			throw new UnrecoverableException("Could not send file " + rootDir + "/rolling.txt to " + target, e, false);
		}
	}
}
