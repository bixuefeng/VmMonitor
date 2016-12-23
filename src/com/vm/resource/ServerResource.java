package com.vm.resource;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于存储单个服务器的资源信息
 * @author Administrator
 *
 */
public class ServerResource {

	private String uuid;
	private Long totalMemory;
	private Long usageMemory;
	private Long restMemory;
	private int cpuNum;
	private int speed;
	private double dynamicLoadOfServer;
	private double staticLoadOfServer;
	private double dynamicCPULoadOfServer;
	private Map<String,VmResource> allVmResource = new HashMap<String,VmResource>();
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public Long getTotalMemory() {
		return totalMemory;
	}
	public void setTotalMemory(Long totalMemory) {
		this.totalMemory = totalMemory;
	}
	public Long getUsageMemory() {
		return usageMemory;
	}
	public void setUsageMemory(Long usageMemory) {
		this.usageMemory = usageMemory;
	}
	public Long getRestMemory() {
		return restMemory;
	}
	public void setRestMemory(Long restMemory) {
		this.restMemory = restMemory;
	}
	public Map<String, VmResource> getAllVmResource() {
		return allVmResource;
	}
	public void setAllVmResource(Map<String, VmResource> allVmResource) {
		this.allVmResource = allVmResource;
	}
	
	public double getDynamicLoadOfServer() {
		return dynamicLoadOfServer;
	}
	public void setDynamicLoadOfServer(double dynamicLoadOfServer) {
		this.dynamicLoadOfServer = dynamicLoadOfServer;
	}
	public double getStaticLoadOfServer() {
		return staticLoadOfServer;
	}
	public void setStaticLoadOfServer(double staticLoadOfServer) {
		this.staticLoadOfServer = staticLoadOfServer;
	}
	
	public double getDynamicCPULoadOfServer() {
		return dynamicCPULoadOfServer;
	}
	public void setDynamicCPULoadOfServer(double dynamicCPULoadOfServer) {
		this.dynamicCPULoadOfServer = dynamicCPULoadOfServer;
	}
	
	public int getCpuNum() {
		return cpuNum;
	}
	public void setCpuNum(int cpuNum) {
		this.cpuNum = cpuNum;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	@Override
	public String toString() {
		return "ServerResource [uuid=" + uuid + ", totalMemory=" + totalMemory + ", usageMemory=" + usageMemory
				+ ", restMemory=" + restMemory + ", allVmResource=" + allVmResource + "]";
	}
	
}
