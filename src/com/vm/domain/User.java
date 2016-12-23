package com.vm.domain;

/**
 * 保存用户信息
 * @author Administrator
 *
 */
public class User {

	private int id;
	private String userName;
	private String password;
	private String phone;
	private String email;
	private String vmUuid;
	private String state;
	private int ctime;
	private int utime;
	private int valid;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getVmUuid() {
		return vmUuid;
	}
	public void setVmUuid(String vmUuid) {
		this.vmUuid = vmUuid;
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
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	@Override
	public String toString() {
		return "User [id=" + id + ", userName=" + userName + ", password=" + password + ", phone=" + phone + ", email="
				+ email + ", vmUuid=" + vmUuid + ", state=" + state + ", ctime=" + ctime + ", utime=" + utime
				+ ", valid=" + valid + "]";
	}
	
	
}
