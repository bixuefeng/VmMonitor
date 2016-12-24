package com.vm.domain;

/**
 * ´æ´¢ÐéÄâ»úÐÅÏ¢
 * @author Administrator
 *
 */
public class VmInfo {

	private int id;
	private String uuid;
	private String name;
	private String os;
	private String lastTime;
	private String memory;
	private String cpu;
	private String vmIp;
	private String state;
	private int vifs;
	private int vbds;
	private int location;
	private int ctime;
	private int utime;
	private int valid;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getLocation() {
		return location;
	}
	public void setLocation(int location) {
		this.location = location;
	}
	
	public int getCtime() {
		return ctime;
	}
	public void setCtime(int ctime) {
		this.ctime = ctime;
	}
	public int getUtime() {
		return utime;
	}
	public void setUtime(int utime) {
		this.utime = utime;
	}
	public int getValid() {
		return valid;
	}
	public void setValid(int valid) {
		this.valid = valid;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	
	
	public String getLastTime() {
		return lastTime;
	}
	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}
	public String getMemory() {
		return memory;
	}
	public void setMemory(String memory) {
		this.memory = memory;
	}
	public String getCpu() {
		return cpu;
	}
	public void setCpu(String cpu) {
		this.cpu = cpu;
	}
	public String getVmIp() {
		return vmIp;
	}
	public void setVmIp(String vmIp) {
		this.vmIp = vmIp;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public int getVifs() {
		return vifs;
	}
	public void setVifs(int vifs) {
		this.vifs = vifs;
	}
	public int getVbds() {
		return vbds;
	}
	public void setVbds(int vbds) {
		this.vbds = vbds;
	}
	@Override
	public String toString() {
		return "VmInfo [id=" + id + ", uuid=" + uuid + ", name=" + name + ", os=" + os + ", lastTime=" + lastTime
				+ ", memory=" + memory + ", cpu=" + cpu + ", vmIp=" + vmIp + ", state=" + state + ", vifs=" + vifs
				+ ", vbds=" + vbds + ", location=" + location + ", ctime=" + ctime
				+ ", utime=" + utime + ", valid=" + valid + "]";
	}
	
}
