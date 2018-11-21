package com.fedex.smartpost.mts.services;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

public class MailSender {
    public void sendNotification(String recipients, String report) throws MessagingException {
		FileSystemResource file = new FileSystemResource(report);
		String[] recipientArray = recipients.split(",");

        synchronized(this) {
			JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
			javaMailSenderImpl.setHost("mapper.gslb.fedex.com");
			MimeMessage mimeMessage = javaMailSenderImpl.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			helper.setSubject("ITG 125563 - USPS Data Scan Dashboard Extract");
			helper.setFrom("DoNotReply@fedex.com");
			helper.addAttachment(file.getFilename(), file);
			helper.setText("Please find enclosed the Postal Scan Dashboard.", false);
			for (String recipient : recipientArray) {
				helper.setTo(recipient);
				javaMailSenderImpl.send(mimeMessage);
			}
        }
    }
}
