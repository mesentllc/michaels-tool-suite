package com.fedex.smartpost.mts.converter;

import com.fedex.smartpost.mts.exceptions.EvccRuntimeException;
import com.fedex.smartpost.postal.types.UspsPostage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

public class UspsPostageTransactionMessageConverterImpl implements UspsPostageTransactionMessageConverter {
	private static final JAXBContext jaxbContext = initContext();

	private static JAXBContext initContext() {
		try {
			return JAXBContext.newInstance("com.fedex.smartpost.postal.types");
		}
		catch (JAXBException jaxbe) {
			throw new EvccRuntimeException("Error creating JAXBContext object", jaxbe);
		}
	}

	@Override
	public String createPostageTransactionMessage(UspsPostage transDto) throws EvccRuntimeException {
		try {
			Marshaller marshaller = jaxbContext.createMarshaller();
			StringWriter writer = new StringWriter();
			marshaller.marshal(transDto, writer);
			writer.flush();
			String result = writer.toString();
			return result;
		}
		catch (JAXBException e) {
			throw new EvccRuntimeException("Error marshalling UspsPostage object", e);
		}
	}
}
