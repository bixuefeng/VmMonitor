package com.vm.resource;

import java.util.HashMap;
import java.util.Map;

/**
 * 存储单个虚拟机的资源信息
 * @author Administrator
 *
 */
public class VmResource {
	private String uuid;
	private double totalMemory; 
	private int cpuNum;
	private Map<String,Map<Integer, double[]>> state = new HashMap<String,Map<Integer, double[]>>();
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public double getTotalMemory() {
		return totalMemory;
	}
	public void setTotalMemory(double totalMemory) {
		this.totalMemory = totalMemory;
	}
	public Map<String, Map<Integer, double[]>> getState() {
		return state;
	}
	public void setState(Map<String, Map<Integer, double[]>> state) {
		this.state = state;
	}
	
	public int getCpuNum() {
		return cpuNum;
	}
	public void setCpuNum(int cpuNum) {
		this.cpuNum = cpuNum;
	}
	@Override
	public String toString() {
		return "VmResource [uuid=" + uuid + ", totalMemory=" + totalMemory + ", state=" + state + "]";
	}
	
}
