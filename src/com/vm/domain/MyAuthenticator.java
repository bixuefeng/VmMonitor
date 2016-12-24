package com.vm.domain;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
public class MyAuthenticator extends Authenticator{

	private String name;
	private String passWord;
	public MyAuthenticator(String name,String passWord){
		this.name = name;
		this.passWord = passWord;
	}
	protected PasswordAuthentication getPasswordAuthentication(){
		return new PasswordAuthentication(name, passWord);
	}
}
