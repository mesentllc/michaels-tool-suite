package com.fedex.smartpost.mts.services;

import com.fedex.smartpost.rating.types.ManualUnreleaseRequest;
import com.fedex.smartpost.rating.types.ManualUnreleaseResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.ws.client.core.WebServiceTemplate;

public class AdminServiceImpl implements AdminService {
	private WebServiceTemplate ratingAdminWebService;
	private static final Log logger = LogFactory.getLog(AdminServiceImpl.class);

	@Required
	public void setRatingAdminWebService(WebServiceTemplate ratingAdminWebService) {
		this.ratingAdminWebService = ratingAdminWebService;
	}

	public String manualUnrelease(Long billingGroupId) {
		ManualUnreleaseRequest request = new ManualUnreleaseRequest();
		StringBuilder sb = new StringBuilder();
		if (logger.isDebugEnabled()) {
			logger.info("Requesting Manual Unrelease Service");
		}
		request.setBillingGroup(billingGroupId);
		ManualUnreleaseResponse response = (ManualUnreleaseResponse)ratingAdminWebService.marshalSendAndReceive(request);
		if (logger.isDebugEnabled()) {
			logger.info(response.getUpdated());
		}
		sb.append("Billing Group: ").append(billingGroupId).append(" has been Unreleased");
		return sb.toString();
	}
}
