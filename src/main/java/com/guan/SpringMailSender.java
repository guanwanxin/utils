package com.guan;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;
import org.springframework.ui.velocity.VelocityEngineUtils;

@SuppressWarnings("deprecation")
public class SpringMailSender {
	private JavaMailSenderImpl mailSender;
	private VelocityEngine velocityEngine;

	public SpringMailSender() {
		this.mailSender = new JavaMailSenderImpl();

		this.mailSender.setHost("smtp.sina.cn");
		this.mailSender.setUsername("13418548539@sina.cn");
		this.mailSender.setPassword("gwx199212247656");

		Properties props = System.getProperties();
		props.put("resource.loader", "class");
		props.put("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

		VelocityEngineFactoryBean v = new VelocityEngineFactoryBean();
		v.setVelocityProperties(props);
		try {
			this.velocityEngine = v.createVelocityEngine();
		} catch (VelocityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void simpleSend() throws Exception {
		SimpleMailMessage smm = new SimpleMailMessage();

		smm.setFrom(this.mailSender.getUsername());
		smm.setTo("951868677@qq.com");
		smm.setSubject("Hello world");
		smm.setText("nice !");

		this.mailSender.send(smm);
	}

	public void attachedSend() throws MessagingException {
		MimeMessage msg = this.mailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(msg, true);

		helper.setFrom(this.mailSender.getUsername());
		helper.setTo("951868677@qq.com");
		helper.setSubject("Hello Attachment");
		helper.setText("This is a mail with attachment");

		FileSystemResource file = new FileSystemResource(
				"H:\\10023474525n_20181121.csv");

		helper.addAttachment("10023474525n_20181121.csv", file);

		this.mailSender.send(msg);
	}

	public void richContentSend() throws MessagingException {
		MimeMessage msg = this.mailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(msg, true);

		helper.setFrom(this.mailSender.getUsername());
		helper.setTo("951868677@qq.com");
		helper.setSubject("這是一個測試");

		helper.setText(
				"<body><p style='color:red;'>Hello Html Email</p><img src='cid:file'/></body>",
				true);

		FileSystemResource file = new FileSystemResource("H:\\pic\\110.jpg");

		helper.addInline("file", file);

		this.mailSender.send(msg);
	}

	public void templateSend() throws MessagingException {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("user", "hehaiyang");
		model.put("content", "good evening !");

		String emailText = VelocityEngineUtils.mergeTemplateIntoString(
				this.velocityEngine, "/velocity/mail.vm", model);

		MimeMessage msg = this.mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(msg, true);
		helper.setFrom(this.mailSender.getUsername());
		helper.setTo("951868677@qq.com");
		helper.setSubject("Rich content mail");
		helper.setText(emailText, true);

		this.mailSender.send(msg);
	}
}
