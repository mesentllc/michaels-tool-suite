package com.fedex.smartpost.analysis;

import com.fedex.smartpost.common.business.FxspPackage;
import com.fedex.smartpost.common.business.FxspPackageFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GetMailerId {
	private static final Log log = LogFactory.getLog(GetMailerId.class);

	public static void main(String[] args) {
		FxspPackage fxspPackage = FxspPackageFactory.createFromUnknown("02390100101420000035");
		log.info("Mailer Id: " + fxspPackage.getMailerId());
	}
}
