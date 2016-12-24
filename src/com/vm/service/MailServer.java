package com.vm.service;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.vm.constant.Constant;
import com.vm.domain.MyAuthenticator;

public class MailServer {
	/**
	 * ��Ŀ�����䷢���ļ���
	 * @param to Ŀ������
	 * @param msg �ʼ���Ϣ
	 * @return �ɹ����
	 */
	public boolean sendMail(String to,String msg){
		try {
			Properties prop = new Properties();
			prop.setProperty("mail.transport.protocol", Constant.mailProtocol);
			prop.setProperty("mail.host", Constant.mailSmtpServer);
			prop.put("mail.smtp.auth", "true");
			MyAuthenticator myAuthenticator = new MyAuthenticator(Constant.mailName, Constant.mailPassWord);
			Session session = Session.getDefaultInstance(prop,myAuthenticator);
			session.setDebug(true);
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(Constant.mailFrom));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSentDate(new Date());
			message.setSubject(Constant.mailSubject);
			message.setText(msg);
			message.saveChanges();
			Transport.send(message);
		} catch (Exception e) {
			System.err.println("�ʼ�����ʧ�ܣ�to="+to+",msg="+msg);
		}
		return false;
	}
}
