package com.vm.domain;

import java.util.Map;

public class PoolInfo {

	private String name;
	private Map<java.lang.String,java.lang.String> otherConfig;
	private String uuid;
	private boolean	wlbEnabled;
	private String	wlbUrl;
	private String wlbUsername;
	private Map<java.lang.String,java.lang.String>	LicenseState;
	private String	nameDescription;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<java.lang.String, java.lang.String> getOtherConfig() {
		return otherConfig;
	}
	public void setOtherConfig(Map<java.lang.String, java.lang.String> otherConfig) {
		this.otherConfig = otherConfig;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public boolean isWlbEnabled() {
		return wlbEnabled;
	}
	public void setWlbEnabled(boolean wlbEnabled) {
		this.wlbEnabled = wlbEnabled;
	}
	public String getWlbUrl() {
		return wlbUrl;
	}
	public void setWlbUrl(String wlbUrl) {
		this.wlbUrl = wlbUrl;
	}
	public String getWlbUsername() {
		return wlbUsername;
	}
	public void setWlbUsername(String wlbUsername) {
		this.wlbUsername = wlbUsername;
	}
	public Map<java.lang.String, java.lang.String> getLicenseState() {
		return LicenseState;
	}
	public void setLicenseState(Map<java.lang.String, java.lang.String> licenseState) {
		LicenseState = licenseState;
	}
	public String getNameDescription() {
		return nameDescription;
	}
	public void setNameDescription(String nameDescription) {
		this.nameDescription = nameDescription;
	}
	@Override
	public String toString() {
		return "PoolInfo [name=" + name + ", otherConfig=" + otherConfig + ", uuid=" + uuid + ", wlbEnabled="
				+ wlbEnabled + ", wlbUrl=" + wlbUrl + ", wlbUsername=" + wlbUsername + ", LicenseState=" + LicenseState
				+ ", nameDescription=" + nameDescription + "]";
	}
	
}
