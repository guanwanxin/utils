package com.guan;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

public class EmailUtil {
	private static JavaMailSenderImpl sender;
	private static String mailsenderhost;
	private static String mailsenderaddress;
	private static String mailsenderpwd;
	private static String mailreceiveraddress;
	public static String domian;
	public static Log log = LogFactory.getLog(EmailUtil.class);

	static {
		try {
			Properties pro = getConfig();
			mailsenderhost = pro.getProperty("email.sender.host");
			mailsenderaddress = pro.getProperty("email.sender.address");
			mailsenderpwd = pro.getProperty("email.sender.password");
			mailreceiveraddress = pro.getProperty("email.receiver.address");
			domian = pro.getProperty("email.domian");
		} catch (IOException e) {
			e.printStackTrace();
		}
		Properties prop = new Properties();
		prop.setProperty("mail.smtp.auth", "true");

		sender = new JavaMailSenderImpl();
		sender.setHost(mailsenderhost);
		sender.setUsername(mailsenderaddress);
		sender.setPassword(mailsenderpwd);
		sender.setJavaMailProperties(prop);
	}

	public static void sendEmail(String subject, String content) {
		MimeMessage msg = sender.createMimeMessage();
		try {
			msg.setSubject(subject, "utf-8");
			msg.setText(content, "utf-8");
			msg.setFrom(new InternetAddress(mailsenderaddress));

			if (mailreceiveraddress.contains(",")) {
				String[] receiverAddress = mailreceiveraddress.split(",");
				for (String receiveradd : receiverAddress) {
					msg.addRecipient(Message.RecipientType.TO,
							new InternetAddress(receiveradd));
				}
			} else {
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
						mailreceiveraddress));
			}
			msg.addFrom(new InternetAddress[] { new InternetAddress(
					mailsenderaddress) });
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}

		sender.send(msg);

		log.info(content);
	}

	public static void sendEmailChangePw(String subject, String content,
			String mailAddr) {
		MimeMessage msg = sender.createMimeMessage();
		MimeMessageHelper messageHelper = new MimeMessageHelper(msg, "UTF-8");

		try {
			messageHelper.setTo(mailAddr);
		} catch (MessagingException e) {
			throw new RuntimeException("收件人邮箱地址出错！");
		}
		try {
			messageHelper.setFrom(mailsenderaddress);
		} catch (MessagingException e) {
			throw new RuntimeException("发件人邮箱地址出错！");
		}
		try {
			messageHelper.setSubject(subject);
		} catch (MessagingException e) {
			throw new RuntimeException("邮件主题出错！");
		}
		try {
			messageHelper.setText(content, true);
		} catch (MessagingException e) {
			throw new RuntimeException("邮件内容出错！");
		}

		sender.send(msg);

		log.info(content);
	}

	public static Properties getConfig() throws IOException {
		InputStream in = EmailUtil.class
				.getResourceAsStream("email.properties");
		Properties p = new Properties();
		p.load(in);
		return p;
	}

	public static void main(String[] arg) {
		String content = "111111\t111111#222222\tMESSAGE_FORMAT_ERROR【已邮件通知相关干系人】【已邮件通知相关干系人】【已邮件通知相关干系人】";
		content = content
				+ "111112\t111112#222223\tMESSAGE_FORMAT_ERROR【已邮件通知相关干系人】【已邮件通知相关干系人】【已邮件通知相关干系人】";
		content = content
				+ "111113\t111113#222224\tMESSAGE_FORMAT_ERROR【已邮件通知相关干系人】【已邮件通知相关干系人】【已邮件通知相关干系人】";
		content = content
				+ "111114\t111114#222225\tMESSAGE_FORMAT_ERROR【已邮件通知相关干系人】【已邮件通知相关干系人】【已邮件通知相关干系人】";
		content = content
				+ "111115\t111115#222226\tMESSAGE_FORMAT_ERROR【已邮件通知相关干系人】【已邮件通知相关干系人】【已邮件通知相关干系人】";
		content = content
				+ "111116\t111116#222227\tMESSAGE_FORMAT_ERROR【已邮件通知相关干系人】【已邮件通知相关干系人】【已邮件通知相关干系人】";
		content = content
				+ "111117\t111117#222228\tMESSAGE_FORMAT_ERROR【已邮件通知相关干系人】【已邮件通知相关干系人】【已邮件通知相关干系人】";
		content = content
				+ "111118\t111118#222229\tMESSAGE_FORMAT_ERROR【已邮件通知相关干系人】【已邮件通知相关干系人】【已邮件通知相关干系人】";
		content = content
				+ "111119\t111119#222230\tMESSAGE_FORMAT_ERROR【已邮件通知相关干系人】【已邮件通知相关干系人】【已邮件通知相关干系人】";
		content = content
				+ "111120\t111120#222231\tMESSAGE_FORMAT_ERROR【已邮件通知相关干系人】【已邮件通知相关干系人】【已邮件通知相关干系人】";

		content = "各位干系人，很遗憾通知你昨天记账信息有异常，记账异常消息如下：111111-111111#222222-MESSAGE_FORMAT_ERROR【已邮件通知相关干系人】【已邮件通知相关干系人】【已邮件通知相关干系人】111112-111112#222223-MESSAGE_FORMAT_ERROR【已邮件通知相关干系人】【已邮件通知相关干系人】【已邮件通知相关干系人】111113-111113#222224-MESSAGE_FORMAT_ERROR【已邮件通知相关干系人】【已邮件通知相关干系人】【已邮件通知相关干系人】111114-111114#222225-MESSAGE_FORMAT_ERROR【已邮件通知相关干系人】【已邮件通知相关干系人】【已邮件通知相关干系人】111115-111115#222226-MESSAGE_FORMAT_ERROR【已邮件通知相关干系人】【已邮件通知相关干系人】【已邮件通知相关干系人】111116-111116#222227-MESSAGE_FORMAT_ERROR【已邮件通知相关干系人】【已邮件通知相关干系人】【已邮件通知相关干系人】111117-111117#222228-MESSAGE_FORMAT_ERROR【已邮件通知相关干系人】【已邮件通知相关干系人】【已邮件通知相关干系人】111118-111118#222229-MESSAGE_FORMAT_ERROR【已邮件通知相关干系人】【已邮件通知相关干系人】【已邮件通知相关干系人】111119-111119#222230-MESSAGE_FORMAT_ERROR【已邮件通知相关干系人】【已邮件通知相关干系人】【已邮件通知相关干系人】111120-111120#222231-MESSAGE_FORMAT_ERROR【已邮件通知相关干系人】【已邮件通知相关干系人】【已邮件通知相关干系人】";
		sendEmail("集中支付-记账异常信息", content);
	}
}
