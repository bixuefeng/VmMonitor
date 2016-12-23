package com.vm.domain;

import java.util.Map;

public class ServerInfo {

	private String name;
	private String uuid;
	private String totalMemory;
	private Map<String, String> other;
	private Map<VmInfo, String> everyMemory;
	private Map<String, String> cpu;
	private String ip;
	private String APIversion;
	private String edition;
	private String powerOnMode;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getTotalMemory() {
		return totalMemory;
	}

	public void setTotalMemory(String totalMemory) {
		this.totalMemory = totalMemory;
	}

	public Map<String, String> getOther() {
		return other;
	}

	public void setOther(Map<String, String> other) {
		this.other = other;
	}

	public Map<VmInfo, String> getEveryMemory() {
		return everyMemory;
	}

	public void setEveryMemory(Map<VmInfo, String> everyMemory) {
		this.everyMemory = everyMemory;
	}

	public Map<String, String> getCpu() {
		return cpu;
	}

	public void setCpu(Map<String, String> cpu) {
		this.cpu = cpu;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getAPIversion() {
		return APIversion;
	}

	public void setAPIversion(String aPIversion) {
		APIversion = aPIversion;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public String getPowerOnMode() {
		return powerOnMode;
	}

	public void setPowerOnMode(String powerOnMode) {
		this.powerOnMode = powerOnMode;
	}

	@Override
	public String toString() {
		return "ServerInfo [name=" + name + ", uuid=" + uuid + ", totalMemory=" + totalMemory + ", other=" + other
				+ ", everyMemory=" + everyMemory + ", cpu=" + cpu + ", ip=" + ip + ", APIversion=" + APIversion
				+ ", edition=" + edition + ", powerOnMode=" + powerOnMode + "]";
	}

}
