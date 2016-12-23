package com.vm.test;

import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.vm.xen.ConnectionUtil;
import com.vm.xen.XenTools;
import com.xensource.xenapi.Connection;
import com.xensource.xenapi.Host;
import com.xensource.xenapi.HostMetrics;
import com.xensource.xenapi.VM;
import com.xensource.xenapi.VMGuestMetrics;
import com.xensource.xenapi.VMMetrics;

import Decoder.BASE64Encoder;

public class TestXen {

	@Test
	public void testDouble(){
		long d1 = 34124099584l;
		long d2 = 31464181760l;
		double d = (double)(d1 -d2)/d1;
		System.out.println((double)d);
	}
	
	@Test
	public void testXml() throws Exception{
		Connection conn = ConnectionUtil.getConnection();
		URL url = new URL(
				"http://192.168.1.124/rrd_updates?start=" + (System.currentTimeMillis() / 1000));
		URLConnection urlConnection = url.openConnection();
		String encoding = new BASE64Encoder().encode(("root" + ":" + "123456").getBytes());
		urlConnection.setRequestProperty("Authorization", "Basic " + encoding);
		String rrdXportData = IOUtils.toString(urlConnection.getInputStream());
		System.out.println(rrdXportData);
	} 
	
	@Test
	public void testMemonty()throws Exception{
		Connection conn = ConnectionUtil.getConnection();
		Host host = Host.getByUuid(conn, "a0c98390-f28b-4fbe-8b08-78680c8255ce");
		HostMetrics m = host.getMetrics(conn);
		System.out.println("free@@@" + m.getMemoryFree(conn));
		System.out.println("total@@@" + m.getMemoryTotal(conn));
		//System.out.println("host over" + host.getMemoryOverhead(conn));
	
		
		
		/**
		 * vm+dmin4294967296
vm+dmax8589934592
vm+ov71303168
vm+smax8589934592
vm+smin2147483648
vm+target6996086784
vm+actual6996090880
		 */
		VM vm = VM.getByUuid(conn, "8ca5a775-0f65-cbad-ebe8-9a4519e47aa2");
		System.out.println("vm+dmin"+vm.getMemoryDynamicMin(conn));
		System.out.println("vm+dmax"+vm.getMemoryDynamicMax(conn));
		System.out.println("vm+ov"+vm.getMemoryOverhead(conn));
		System.out.println("vm+smax"+vm.getMemoryStaticMax(conn));
		System.out.println("vm+smin"+vm.getMemoryStaticMin(conn));
		System.out.println("vm+target"+vm.getMemoryTarget(conn));
		VMMetrics metrics = vm.getMetrics(conn);
		System.out.println("vm+actual"+metrics.getMemoryActual(conn));
		VMGuestMetrics mGuestMetrics = vm.getGuestMetrics(conn);
		System.out.println("vm"+mGuestMetrics.getMemory(conn));
		
	}
	
	@Test
	public void testR()throws Exception{
		Connection conn = ConnectionUtil.getConnection();
		Set<VM> vms = VM.getAll(conn);
		for(VM vm : vms){
			if(vm.getIsControlDomain(conn)){
				System.out.println("vm+dmin"+vm.getMemoryDynamicMin(conn));
				System.out.println("vm+dmax"+vm.getMemoryDynamicMax(conn));
				System.out.println("vm+ov"+vm.getMemoryOverhead(conn));
				System.out.println("vm+smax"+vm.getMemoryStaticMax(conn));
				System.out.println("vm+smin"+vm.getMemoryStaticMin(conn));
				System.out.println("vm+target"+vm.getMemoryTarget(conn));
				VMMetrics metrics = vm.getMetrics(conn);
				System.out.println("vm+actual"+metrics.getMemoryActual(conn));
			}
		}
	}
	
	@Test
	public void testVmInfo()throws Exception{
		Connection conn = ConnectionUtil.getConnection();
		VM vm = VM.getByUuid(conn, "3ad0732f-7ecb-ffe4-c2a5-025bb19acfc3");
		System.out.println(Long.toString(vm.getVCPUsMax(conn)));
	}
	@Test
	public void testCPU() throws Exception{
		Connection conn = ConnectionUtil.getConnection();
		Host host  = Host.getByUuid(conn, "a0c98390-f28b-4fbe-8b08-78680c8255ce");
		Map<String, String> map = host.getCpuInfo(conn);
		for(Map.Entry<String, String> entry : map.entrySet()){
			String key = entry.getKey();
			String value = entry.getValue();
			System.out.println("key = "+ key+" value="+value);
		}
	}
}
