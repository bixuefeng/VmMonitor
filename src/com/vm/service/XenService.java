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

	private final String ERROR_MSG = "错误名称";
	
	@Autowired
	private CalculateService calculateService;
	
	

	public PoolInfo getPoolInfo(){
		return XenTools.getPoolInfo();
	}
	/**
	 * 获取池名称
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
	 * 获取所有服务器主机的名称
	 * @return
	 */
	public Set<String> getHostsNames(){
		return XenTools.getHostsName();
	}
	
	/**
	 * 根据主机名称，获取该主机上所有虚拟机名称
	 * @param hostName
	 * @return
	 */
	public Set<String> getVmsNamesFromHost(String hostName){
		System.out.println("XenService.getVmsNamesFromHost调用参数hostName=" + hostName);
		if(StringUtils.isEmpty(hostName)){
			return null;
		}
		return XenTools.getVmsNamesFromHost(hostName);
	}
	
	/**
	 * 根据主机名称，获取该主机上所有SR名称
	 * @param hostName
	 * @return
	 */
	public Set<String> getSrsNamesFromHost(String hostName){
		System.out.println("XenService.getVmsNamesFromHost调用参数hostName=" + hostName);
		if(StringUtils.isEmpty(hostName)){
			return null;
		}
		return XenTools.getSrsNamesFromHost(hostName);
	}
	
	/**
	 * 获取池中共享SR的名称
	 * @return
	 */
	public Set<String> getShareSrNames(){
		return XenTools.getShareSrNames();
	}
	/**
	 * 根据vm名称判断该虚拟机是否处于开机状态
	 * @param vmName
	 * @return
	 */
	public boolean isRunningByName(String vmName){
		System.out.println("XenService.isRunningByName调用参数vmName=" + vmName);
		if(StringUtils.isEmpty(vmName)){
			return false;
		}
		return XenTools.isRunningByName(vmName);
	}
	
	public boolean isRunningByUuid(String uuid){
		System.out.println("XenService.isRunningByUuid调用参数uuid=" + uuid);
		if(StringUtils.isEmpty(uuid)){
			return false;
		}
		return XenTools.isRunning(uuid);
	}
	
	/**
	 * 根据虚拟机的uuid获取虚拟机的ip地址，如果关机状态，则开机。
	 * @param vmUuid
	 * @return
	 */
	public String getIpByUuid(String vmUuid) {
		// TODO Auto-generated method stub
		System.out.println("XenService.getIpByUuid调用参数vmUuid=" + vmUuid);
		if(StringUtils.isEmpty(vmUuid)){
			return ERROR_MSG;
		}
		return XenTools.getIpByUuid(vmUuid);
	}
	
	/**
	 * 创建虚拟机通过模板
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
	 * 根据虚拟机服务器uuid，获取服务器的资源,之后计算其静态和动态内存利用率
	 * @param uuid
	 * @return
	 */
	public ServerResource getServerResourceByUuid(String uuid) {
		// TODO Auto-generated method stub
		if(StringUtils.isEmpty(uuid)){
			System.err.println("XenService.getServerResourceByUuid出错,uuid="+uuid);
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
			System.err.println("XenService.getVmInfoByNameOrUuid出错,name="+name+"uuid" + uuid);
			return null;
		}
		return XenTools.getVmInfoByNameOrUuid(name, uuid);
	}
}
