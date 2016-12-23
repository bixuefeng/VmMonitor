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
 * �ṩ���ؾ���������
 * @author 123456
 *
 */
@Service
public class CalculateService {
	
	@Autowired
	private XenService xenService;
	
	/**
	 * ����������ľ�̬�ڴ渺���ʣ�ֱ�Ӱ����ѷ�����ڴ����
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
	 * ���ݷ�������Դ̽�������������������еĶ�̬CPU������
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
	 * ����������Ķ�̬�ڴ渺���ʣ����㷽ʽ����ÿһ��VM��ʹ����������
	 * @param serverResource
	 * @return
	 */
	public double calculateDynamicLoadOfServer(ServerResource serverResource){
		Map<String, VmResource> map = serverResource.getAllVmResource();
		if(MapUtils.isEmpty(map)){
			return this.calculateStaticLoadOfServer(serverResource);
		}
		//�����������ÿһ��VM���ڴ�ʹ���ʣ��ۼ�Ϊ������������ڴ�ʹ����
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
	 * ѡ����ԣ�Ѱ��һ�����ʵ�VM����Ǩ��
	 * @param uuid host��uuid
	 * @return ���������
	 */
	public String vmSelectStrategy(String uuid){
		if(StringUtils.isEmpty(uuid)){
			return null;
		}
		//Ĭ��ʹ�ò��ԣ�cpu/memory������cpu����cpu��ʹ���������(cpu[i]*num)��memoryΪ�ڴ��������ڴ�����Խ��
		//Ǩ������ԽС��
		return XenTools.getVmBySelectStrategy(uuid);
	}

	/**
	 * �ҵ�Ŀ������
	 * @return
	 */
	public String hostSelectStrategy() {
		// TODO Auto-generated method stub
		return XenTools.hostSelectStrategy();
	}

	/**
	 * Ԥ��ָ���������Ƿ��ع���
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
			//��ȡ��cpuʹ���ʵ���ʷ���ݡ�����Ԥ��
			
		}
		return false;
	}
	
	
}
