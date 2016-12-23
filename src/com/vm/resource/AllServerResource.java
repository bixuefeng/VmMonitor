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
	 * ��Դ̽���̸߳���ĳ������������Դ
	 * 
	 * @param uuid
	 */
	public void updateResourceByUuid(String uuid) {
		lock.readLock().lock();
		try {
			System.out.println("===============̽���߳�" + Thread.currentThread() + "��ʼ������Դ" + uuid + "==============");
			allServerResource.put(uuid, xenService.getServerResourceByUuid(uuid));
/*			for (Map.Entry<String, ServerResource> entry : allServerResource.entrySet()) {
				System.out.println("������" + entry.getKey() + "��Դ����:");
				ServerResource serverResource = entry.getValue();
				System.out.println("���ڴ�" + serverResource.getTotalMemory());
				System.out.println("�����ڴ�(��̬�̶����䡾������domain0��)" + serverResource.getUsageMemory());
				System.out.println("ʣ������ڴ�" + serverResource.getRestMemory());
				System.out.println("���������������е�VM��Դ����:");
				Map<String, VmResource> vmMap = serverResource.getAllVmResource();
				if (MapUtils.isEmpty(vmMap)) {
					System.out.println("��������û�����е������");

				} else {
					for (Map.Entry<String, VmResource> ventry : vmMap.entrySet()) {
						System.out.println("�����" + ventry.getKey());
						VmResource vmResource = ventry.getValue();
						System.out.println("�ѷ������ڴ棺" + vmResource.getTotalMemory());
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
									System.out.println("û��cpu����");
									continue;
								}
								System.out.print("cpu" + i + "�ڹ�ȥ��" + data.length + "������ʹ����:");
								for (int j = data.length - 1; j >= 0; j--) {
									System.out.print("( " + String.format("%.2f", data[j] * 100) + "% )");
								}
							}
						}
						if (MapUtils.isNotEmpty(memory)) {
							for (int i = 0; i < memory.size(); i++) {
								double[] data = memory.get(i);
								if(data == null){
									System.out.println("\nû���ڴ�����");
									continue;
								}
								int len = data.length;
								System.out.print("\n�ڴ��ڹ�ȥ��" + len + "������ʹ����:");
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
									System.out.println("\nû��������������");
									continue;
								}
								int len = data.length;
								System.out.print("\n����" + i + "�ڹ�ȥ��" + len + "��������������:");
								for (int j = 0; j < len; j++) {
									System.out.print(String.format("("+"%.2f", data[len - j - 1] / 1024) + "KB)");
								}
							}
						}
						if (MapUtils.isNotEmpty(vifR)) {
							for (int i = 0; i < vifR.size(); i++) {
								double[] data = vifR.get(i);
								if(data == null){
									System.out.println("\nû��������������");
									continue;
								}
								int len = data.length;
								System.out.print("\n����" + i + "�ڹ�ȥ��" + len + "��������������:");
								for (int j = 0; j < len; j++) {
									System.out.print(String.format("(" + "%.2f", data[len - j - 1] / 1024) + "KB)");
								}
							}
						}

						if (MapUtils.isNotEmpty(vbdW)) {
							for (int i = 0; i < vbdW.size(); i++) {
								double[] data = vbdW.get(i);
								if(data == null){
									System.out.println("\nû��д��������");
									continue;
								}
								int len = data.length;
								System.out.print("\n����" + i + "�ڹ�ȥ��" + len + "������д����:");
								for (int j = 0; j < len; j++) {
									System.out.print("(" + String.format("%.2f", data[len - j - 1] / 1024) + "KB)");
								}
							}
						}
						if (MapUtils.isNotEmpty(vbdR)) {
							for (int i = 0; i < vbdR.size(); i++) {
								double[] data = vbdR.get(i);
								if(data == null){
									System.out.println("\nû�ж���������");
									continue;
								}
								int len = data.length;
								System.out.print("\n����" + i + "�ڹ�ȥ��" + len + "�����������:");
								for (int j = 0; j < len; j++) {
									System.out.print("(" + String.format("%.2f", data[len - j - 1] / 1024) + "KB)");
								}
							}
						}
						System.out.println("��̬�ڴ�ʹ�ø���" + serverResource.getStaticLoadOfServer());
						System.out.println("��̬�ڴ�ʹ�ø���" + serverResource.getDynamicLoadOfServer());
					}
				}
			}*/
			
			
			System.out.println("\n===============̽���߳�" + Thread.currentThread() + "������Դ���" + uuid + "==============");
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * ����ƽ���߳�ִ������
	 */
	public void balance() {
		lock.writeLock().lock();
		try {
			System.out.println("����ƽ���߳�" + Thread.currentThread() + "��ʼ����");
			System.out.println("��Ⱥ�й���" + allServerResource.size() + "����������������״̬");
			for(Map.Entry<String, ServerResource> entry : allServerResource.entrySet()){
				String uuid = entry.getKey();
				System.out.println("������" + uuid +"����״̬");
				//�Է�������̬���ؼ���
				ServerResource serverResource = entry.getValue();
				double memoryStaticUtilization = serverResource.getStaticLoadOfServer();
				System.out.println("��̬�ڴ�ʹ�ø���" + memoryStaticUtilization);
				//���㶯̬������
				double memoryDynamicUtilization =  serverResource.getDynamicLoadOfServer();
				System.out.println("��̬�ڴ�ʹ�ø���" + memoryDynamicUtilization);
				//����������cpu��̬ʹ����
				double cpuDynamicUtilization =  serverResource.getDynamicCPULoadOfServer();
				System.out.println("��̬cpuʹ�ø���" + cpuDynamicUtilization);
				//���ڴ������ʽ�Сʱ
				if(memoryStaticUtilization < 0.85 && cpuDynamicUtilization <0.85){
					System.out.println("������" + uuid + "��������");
					continue;
				}
				//TODO: ��������˸�����ֵ����Ҫ����AR(P)����Ԥ�⣬����˲ʱ��ֵ
				System.out.println("������" + uuid + "�����˸���,���������");
				
				Boolean flag = memoryStaticUtilization>0.85&&cpuDynamicUtilization>0.85?true:memoryStaticUtilization>0.85?true:false;
				//�������Ϊcpu������ɵģ����ӳ�Ǩ�ƣ�����Ԥ��
				if(!flag){
					//TODO: Ԥ��
					boolean foreCast = calculateService.foreCast(uuid);
					if(!foreCast){
						continue;
					}
				}
				//����������ڴ�ԭ�򣬻��������ڴ��cpu��ͬ���أ�����Ǩ�ơ�
				String vmName = calculateService.vmSelectStrategy(uuid);
				if(StringUtils.isEmpty(vmName)){
					System.err.println("û�к���VM����Ǩ��.........");
					continue;
				}
				VmInfo vmInfo = xenService.getVmInfoByNameOrUuid(vmName, null);
				System.out.println("�ҵ����ʵ�VM����Ǩ��" + vmInfo.getName() + "cpu" +vmInfo.getCpu() + "�ڴ��С" + vmInfo.getMemory());
				
				String hostName = calculateService.hostSelectStrategy();
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

}
