package com.vm.service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vm.constant.Constant;
import com.vm.resource.ServerResource;
import com.vm.resource.VmResource;
import com.vm.xen.XenTools;
import com.xensource.xenapi.VM;

/**
 * 提供负载均衡计算服务
 * @author 123456
 *
 */
@Service
public class CalculateService {
	
	@Autowired
	private XenService xenService;
	
	/**
	 * 计算服务器的静态内存负载率，直接按照已分配的内存计算
	 * @param serverResource
	 * @return
	 */
	public double calculateStaticLoadOfServer(ServerResource serverResource){
		Long total = serverResource.getTotalMemory();
		//Long usage = serverResource.getUsageMemory();
		Long free = serverResource.getRestMemory();
		double load = (double)(total - free)/total;
		return load;
	}

	/**
	 * 根据服务器资源探测情况，计算出服务器中的动态CPU负载率
	 * @param serverResource
	 * @return
	 */
	public double calculateDynamicCPULoadOfServer(ServerResource serverResource){
		
		Map<String, VmResource> map = serverResource.getAllVmResource();
		if(MapUtils.isEmpty(map)){
			return 0.0;
		}
		int cpuNum = serverResource.getCpuNum();
		double result = 0.0;
		for(Map.Entry<String,VmResource> entry : map.entrySet()){
			VmResource vmResource = entry.getValue();
			//int cpuNumOfVm = vmResource.getCpuNum();
			Map<String,Map<Integer, double[]>> state  = vmResource.getState();
			Map<Integer, double[]> cpuMap = state.get("cpuMap");
			for(Map.Entry<Integer, double[]> entry2 : cpuMap.entrySet()){
				double[] ds = entry2.getValue();
				if(ds != null){
					double d = ds[0];
					result += d;
				}
			}
		}
		return result/cpuNum;
	}
	/**
	 * 计算服务器的动态内存负载率，计算方式根据每一个VM的使用率来计算
	 * @param serverResource
	 * @return
	 */
	public double calculateDynamicLoadOfServer(ServerResource serverResource){
		Map<String, VmResource> map = serverResource.getAllVmResource();
		if(MapUtils.isEmpty(map)){
			return this.calculateStaticLoadOfServer(serverResource);
		}
		//计算服务器中每一个VM的内存使用率，累计为这个服务器的内存使用率
		double result = 0.0;
		for(Map.Entry<String, VmResource> entry : map.entrySet()){
			VmResource vmResource = entry.getValue();
			Map<String, Map<Integer, double[]>> map2 = vmResource.getState();
			Map<Integer, double[]> map3 = map2.get("memoryMap");
			double[] doub = map3.get(0);
			if(doub == null){
				continue;
			}
			result += (vmResource.getTotalMemory() - doub[0]/1024);
		}
		double total = (double)(serverResource.getTotalMemory()/1024/1024);
		return result/total;
	}
	
	/**
	 * 选择策略，寻找一个合适的VM进行迁移
	 * @param uuid host的uuid
	 * @return 虚拟机名称
	 */
	public String vmSelectStrategy(String uuid){
		if(StringUtils.isEmpty(uuid)){
			return null;
		}
		//默认使用策略，cpu/memory，其中cpu代表cpu的使用量：求和(cpu[i]*num)，memory为内存容量。内存容量越大
		//迁移因子越小。
		return XenTools.getVmBySelectStrategy(uuid);
	}

	/**
	 * 找到目的主机
	 * @return
	 */
	public String hostSelectStrategy() {
		// TODO Auto-generated method stub
		return XenTools.hostSelectStrategy();
	}

	/**
	 * 预测指定的主机是否负载过高
	 * @param uuid
	 * @return
	 */
	public boolean foreCast(String uuid) {
		if(StringUtils.isEmpty(uuid)){
			return false;
		}
		ServerResource serverResource = xenService.getServerResourceByUuid(uuid);
		Map<String, VmResource> vmMap = serverResource.getAllVmResource();
		if (MapUtils.isNotEmpty(vmMap)) {
			double[] history = new double[Constant.MAX_CPU_HISTORY];
			for(Map.Entry<String, VmResource> entry : vmMap.entrySet()){
				Map<Integer, double[]> map = entry.getValue().getState().get("cpuMap");
				for(Map.Entry<Integer, double[]> entry2 : map.entrySet()){
					double[] doub = entry2.getValue();
					for(int i=0;i<Constant.MAX_CPU_HISTORY;i++){
						history[i] += doub[i];
					}
				}
			}
			//获取了cpu使用率的历史数据。进行预测
			
		}
		return false;
	}
	
	
}
