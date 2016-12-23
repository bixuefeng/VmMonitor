package com.vm.service;

import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vm.domain.PoolInfo;
import com.vm.domain.VmInfo;
import com.vm.resource.ServerResource;
import com.vm.xen.XenTools;

@Service
public class XenService {

	private final String ERROR_MSG = "��������";
	
	@Autowired
	private CalculateService calculateService;
	
	

	public PoolInfo getPoolInfo(){
		return XenTools.getPoolInfo();
	}
	/**
	 * ��ȡ������
	 * @return
	 */
	public String getPoolName() {
		String poolName = null;
		poolName = XenTools.getPoolName();
		if (StringUtils.isEmpty(poolName)) {
			return ERROR_MSG;
		}
		return poolName;
	}
	
	/**
	 * ��ȡ���з���������������
	 * @return
	 */
	public Set<String> getHostsNames(){
		return XenTools.getHostsName();
	}
	
	/**
	 * �����������ƣ���ȡ���������������������
	 * @param hostName
	 * @return
	 */
	public Set<String> getVmsNamesFromHost(String hostName){
		System.out.println("XenService.getVmsNamesFromHost���ò���hostName=" + hostName);
		if(StringUtils.isEmpty(hostName)){
			return null;
		}
		return XenTools.getVmsNamesFromHost(hostName);
	}
	
	/**
	 * �����������ƣ���ȡ������������SR����
	 * @param hostName
	 * @return
	 */
	public Set<String> getSrsNamesFromHost(String hostName){
		System.out.println("XenService.getVmsNamesFromHost���ò���hostName=" + hostName);
		if(StringUtils.isEmpty(hostName)){
			return null;
		}
		return XenTools.getSrsNamesFromHost(hostName);
	}
	
	/**
	 * ��ȡ���й���SR������
	 * @return
	 */
	public Set<String> getShareSrNames(){
		return XenTools.getShareSrNames();
	}
	/**
	 * ����vm�����жϸ�������Ƿ��ڿ���״̬
	 * @param vmName
	 * @return
	 */
	public boolean isRunningByName(String vmName){
		System.out.println("XenService.isRunningByName���ò���vmName=" + vmName);
		if(StringUtils.isEmpty(vmName)){
			return false;
		}
		return XenTools.isRunningByName(vmName);
	}
	
	public boolean isRunningByUuid(String uuid){
		System.out.println("XenService.isRunningByUuid���ò���uuid=" + uuid);
		if(StringUtils.isEmpty(uuid)){
			return false;
		}
		return XenTools.isRunning(uuid);
	}
	
	/**
	 * �����������uuid��ȡ�������ip��ַ������ػ�״̬���򿪻���
	 * @param vmUuid
	 * @return
	 */
	public String getIpByUuid(String vmUuid) {
		// TODO Auto-generated method stub
		System.out.println("XenService.getIpByUuid���ò���vmUuid=" + vmUuid);
		if(StringUtils.isEmpty(vmUuid)){
			return ERROR_MSG;
		}
		return XenTools.getIpByUuid(vmUuid);
	}
	
	/**
	 * ���������ͨ��ģ��
	 * @param vmName
	 * @return
	 */
	public VmInfo createWin10ByTemple(String vmName) {
		// TODO Auto-generated method stub
		if(StringUtils.isEmpty(vmName)){
			return null;
		}
		return XenTools.createWin10ByTemple(vmName);
	}
	
	/**
	 * ���������������uuid����ȡ����������Դ,֮������侲̬�Ͷ�̬�ڴ�������
	 * @param uuid
	 * @return
	 */
	public ServerResource getServerResourceByUuid(String uuid) {
		// TODO Auto-generated method stub
		if(StringUtils.isEmpty(uuid)){
			System.err.println("XenService.getServerResourceByUuid����,uuid="+uuid);
			return null;
		}
		ServerResource serverResource =  XenTools.getServerResourceByUuid(uuid);
		serverResource.setDynamicLoadOfServer(calculateService.calculateDynamicLoadOfServer(serverResource));
		serverResource.setStaticLoadOfServer(calculateService.calculateStaticLoadOfServer(serverResource));
		serverResource.setDynamicCPULoadOfServer(calculateService.calculateDynamicCPULoadOfServer(serverResource));
		return serverResource;
	}
	public Set<String> getHostsUUid() {
		return XenTools.getHostsUuid();
	}
	public VmInfo getVmInfoByNameOrUuid(String name,String uuid){
		if(StringUtils.isEmpty(name) && StringUtils.isEmpty(uuid)){
			System.err.println("XenService.getVmInfoByNameOrUuid����,name="+name+"uuid" + uuid);
			return null;
		}
		return XenTools.getVmInfoByNameOrUuid(name, uuid);
	}
}
