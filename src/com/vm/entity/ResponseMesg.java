package com.vm.entity;

import java.io.Serializable;

/**
 * 用于响应用户需求
 * @author Administrator
 *
 */
public class ResponseMesg implements Serializable{

	private static final long serialVersionUID = 1L;
	private String responserMesg;
	private int mesgType;
	/*
	 * 	0:表示虚拟机未开机，需继续等待
	 *  1：表示返回信息为虚拟机IP地址
	 */
	public ResponseMesg(String responserMesg,int mesgType)
	{
		this.responserMesg = responserMesg;
		this.mesgType = mesgType;
	}
	public String getResponserMesg() {
		return responserMesg;
	}
	public void setResponserMesg(String responserMesg) {
		this.responserMesg = responserMesg;
	}
	public int getMesgType() {
		return mesgType;
	}
	public void setMesgType(int mesgType) {
		this.mesgType = mesgType;
	}
}
