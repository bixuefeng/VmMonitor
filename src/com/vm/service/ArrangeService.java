package com.vm.service;

import org.springframework.stereotype.Service;

/**
 * 该类用于定时服务器整合或开发
 * @author Administrator
 *
 */
@Service
public class ArrangeService {

	/**
	 * 每天凌晨12点整合服务器
	 */
	public void combine12Clock(){
		System.out.println("12clok");
	}
	
	public void start8Clock(){
		System.out.println("8cloce");
	}
}
