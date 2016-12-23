package com.vm.resource;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vm.domain.VmInfo;
import com.vm.service.CalculateService;
import com.vm.service.XenService;
import com.xensource.xenapi.VM;

@Component
public class AllServerResource {

	@Autowired
	private XenService xenService;
	@Autowired
	private CalculateService calculateService;
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private Map<String, ServerResource> allServerResource = new HashMap<String, ServerResource>();

	/**
	 * 资源探测线程更新某个服务器的资源
	 * 
	 * @param uuid
	 */
	public void updateResourceByUuid(String uuid) {
		lock.readLock().lock();
		try {
			System.out.println("===============探测线程" + Thread.currentThread() + "开始更新资源" + uuid + "==============");
			allServerResource.put(uuid, xenService.getServerResourceByUuid(uuid));
/*			for (Map.Entry<String, ServerResource> entry : allServerResource.entrySet()) {
				System.out.println("服务器" + entry.getKey() + "资源如下:");
				ServerResource serverResource = entry.getValue();
				System.out.println("总内存" + serverResource.getTotalMemory());
				System.out.println("已用内存(静态固定分配【不包含domain0】)" + serverResource.getUsageMemory());
				System.out.println("剩余可用内存" + serverResource.getRestMemory());
				System.out.println("服务器内正在运行的VM资源如下:");
				Map<String, VmResource> vmMap = serverResource.getAllVmResource();
				if (MapUtils.isEmpty(vmMap)) {
					System.out.println("服务器中没有运行的虚拟机");

				} else {
					for (Map.Entry<String, VmResource> ventry : vmMap.entrySet()) {
						System.out.println("虚拟机" + ventry.getKey());
						VmResource vmResource = ventry.getValue();
						System.out.println("已分配总内存：" + vmResource.getTotalMemory());
						Map<String, Map<Integer, double[]>> vMap = vmResource.getState();
						Map<Integer, double[]> cpu = vMap.get("cpuMap");
						Map<Integer, double[]> memory = vMap.get("memoryMap");
						Map<Integer, double[]> vifT = vMap.get("vifTMap");
						Map<Integer, double[]> vifR = vMap.get("vifRMap");
						Map<Integer, double[]> vbdW = vMap.get("vbdWMap");
						Map<Integer, double[]> vbdR = vMap.get("vbdRMap");
						if (MapUtils.isNotEmpty(cpu)) {
							for (int i = 0; i < cpu.size(); i++) {
								double[] data = cpu.get(i);
								if(data == null){
									System.out.println("没有cpu数据");
									continue;
								}
								System.out.print("cpu" + i + "在过去的" + data.length + "个监测点使用率:");
								for (int j = data.length - 1; j >= 0; j--) {
									System.out.print("( " + String.format("%.2f", data[j] * 100) + "% )");
								}
							}
						}
						if (MapUtils.isNotEmpty(memory)) {
							for (int i = 0; i < memory.size(); i++) {
								double[] data = memory.get(i);
								if(data == null){
									System.out.println("\n没有内存数据");
									continue;
								}
								int len = data.length;
								System.out.print("\n内存在过去的" + len + "个监测点使用率:");
								for (int j = 0; j < len; j++) {
									System.out.print("( "
											+ String.format("%.2f",
													(vmResource.getTotalMemory() - data[len - j - 1]/1024)/vmResource.getTotalMemory()*100)
											+ " %)");
								}
							}
						}

						if (MapUtils.isNotEmpty(vifT)) {
							for (int i = 0; i < vifT.size(); i++) {
								double[] data = vifT.get(i);
								if(data == null){
									System.out.println("\n没有上行速率数据");
									continue;
								}
								int len = data.length;
								System.out.print("\n网络" + i + "在过去的" + len + "个监测点上行速率:");
								for (int j = 0; j < len; j++) {
									System.out.print(String.format("("+"%.2f", data[len - j - 1] / 1024) + "KB)");
								}
							}
						}
						if (MapUtils.isNotEmpty(vifR)) {
							for (int i = 0; i < vifR.size(); i++) {
								double[] data = vifR.get(i);
								if(data == null){
									System.out.println("\n没有下行速率数据");
									continue;
								}
								int len = data.length;
								System.out.print("\n网络" + i + "在过去的" + len + "个监测点下行速率:");
								for (int j = 0; j < len; j++) {
									System.out.print(String.format("(" + "%.2f", data[len - j - 1] / 1024) + "KB)");
								}
							}
						}

						if (MapUtils.isNotEmpty(vbdW)) {
							for (int i = 0; i < vbdW.size(); i++) {
								double[] data = vbdW.get(i);
								if(data == null){
									System.out.println("\n没有写速率数据");
									continue;
								}
								int len = data.length;
								System.out.print("\n磁盘" + i + "在过去的" + len + "个监测点写速率:");
								for (int j = 0; j < len; j++) {
									System.out.print("(" + String.format("%.2f", data[len - j - 1] / 1024) + "KB)");
								}
							}
						}
						if (MapUtils.isNotEmpty(vbdR)) {
							for (int i = 0; i < vbdR.size(); i++) {
								double[] data = vbdR.get(i);
								if(data == null){
									System.out.println("\n没有读速率数据");
									continue;
								}
								int len = data.length;
								System.out.print("\n磁盘" + i + "在过去的" + len + "个监测点读速率:");
								for (int j = 0; j < len; j++) {
									System.out.print("(" + String.format("%.2f", data[len - j - 1] / 1024) + "KB)");
								}
							}
						}
						System.out.println("静态内存使用负载" + serverResource.getStaticLoadOfServer());
						System.out.println("动态内存使用负载" + serverResource.getDynamicLoadOfServer());
					}
				}
			}*/
			
			
			System.out.println("\n===============探测线程" + Thread.currentThread() + "更新资源完毕" + uuid + "==============");
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * 负载平衡线程执行内容
	 */
	public void balance() {
		lock.writeLock().lock();
		try {
			System.out.println("负载平衡线程" + Thread.currentThread() + "开始工作");
			System.out.println("集群中共有" + allServerResource.size() + "个服务器处于运行状态");
			for(Map.Entry<String, ServerResource> entry : allServerResource.entrySet()){
				String uuid = entry.getKey();
				System.out.println("服务器" + uuid +"负载状态");
				//对服务器静态负载计算
				ServerResource serverResource = entry.getValue();
				double memoryStaticUtilization = serverResource.getStaticLoadOfServer();
				System.out.println("静态内存使用负载" + memoryStaticUtilization);
				//计算动态负载率
				double memoryDynamicUtilization =  serverResource.getDynamicLoadOfServer();
				System.out.println("动态内存使用负载" + memoryDynamicUtilization);
				//计算主机的cpu动态使用率
				double cpuDynamicUtilization =  serverResource.getDynamicCPULoadOfServer();
				System.out.println("动态cpu使用负载" + cpuDynamicUtilization);
				//当内存利用率较小时
				if(memoryStaticUtilization < 0.85 && cpuDynamicUtilization <0.85){
					System.out.println("服务器" + uuid + "正常运行");
					continue;
				}
				//TODO: 如果超过了负载阈值，需要根据AR(P)进行预测，避免瞬时峰值
				System.out.println("服务器" + uuid + "超过了负载,辨别负载类型");
				
				Boolean flag = memoryStaticUtilization>0.85&&cpuDynamicUtilization>0.85?true:memoryStaticUtilization>0.85?true:false;
				//如果是因为cpu过载造成的，则延迟迁移，进行预测
				if(!flag){
					//TODO: 预测
					boolean foreCast = calculateService.foreCast(uuid);
					if(!foreCast){
						continue;
					}
				}
				//如果是由于内存原因，或者由于内存和cpu共同过载，立即迁移。
				String vmName = calculateService.vmSelectStrategy(uuid);
				if(StringUtils.isEmpty(vmName)){
					System.err.println("没有合适VM进行迁移.........");
					continue;
				}
				VmInfo vmInfo = xenService.getVmInfoByNameOrUuid(vmName, null);
				System.out.println("找到合适的VM进行迁移" + vmInfo.getName() + "cpu" +vmInfo.getCpu() + "内存大小" + vmInfo.getMemory());
				
				String hostName = calculateService.hostSelectStrategy();
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

}
