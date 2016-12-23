package com.vm.thread;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vm.resource.AllServerResource;

@Component("balanceThred")
public class BalanceThred extends Thread{

	@Autowired
	private AllServerResource allServerResource;
	@Override
	public void run(){
		while(true){
			allServerResource.balance();
			try {
				Thread.sleep(40000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
