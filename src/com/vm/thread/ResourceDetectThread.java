package com.vm.thread;

import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vm.resource.AllServerResource;
import com.vm.resource.ServerResource;
import com.vm.service.XenService;
import com.vm.xen.XenTools;


/**
 * 资源探测线程，主动探测物理节点拓扑变化，并更新资源。
 * @author Administrator
 *
 */
@Component("resourceDetectThread")
public class ResourceDetectThread extends Thread {

	@Autowired
	private XenService xenService;
	@Autowired
	private AllServerResource allServerResource;

	@Override
	public void run() {
		while (true) {
			Set<String> uuidOfServers = xenService.getHostsUUid();
			if (CollectionUtils.isNotEmpty(uuidOfServers)) {
				for (String uuid : uuidOfServers) {
					allServerResource.updateResourceByUuid(uuid);
				}
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
