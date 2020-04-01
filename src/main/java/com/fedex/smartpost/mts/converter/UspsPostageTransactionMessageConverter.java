package com.fedex.smartpost.mts.converter;

import com.fedex.smartpost.mts.exceptions.EvccRuntimeException;
import com.fedex.smartpost.postal.types.UspsPostage;

public interface UspsPostageTransactionMessageConverter {
	String createPostageTransactionMessage(UspsPostage transDto) throws EvccRuntimeException;
}
