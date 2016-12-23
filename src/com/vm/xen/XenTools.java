package com.vm.xen;

import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import com.vm.domain.PoolInfo;
import com.vm.domain.ServerInfo;
import com.vm.domain.Vdi;
import com.vm.domain.VmInfo;
import com.vm.resource.ServerResource;
import com.vm.resource.VmResource;
import com.vm.tools.Tools;
import com.xensource.xenapi.Connection;
import com.xensource.xenapi.Host;
import com.xensource.xenapi.HostMetrics;
import com.xensource.xenapi.Pool;
import com.xensource.xenapi.SR;
import com.xensource.xenapi.Types;
import com.xensource.xenapi.VBD;
import com.xensource.xenapi.VDI;
import com.xensource.xenapi.VIF;
import com.xensource.xenapi.VM;
import com.xensource.xenapi.VMGuestMetrics;
import Decoder.BASE64Encoder;
import javafx.scene.chart.PieChart;
import jdk.management.resource.internal.TotalResourceContext;

/**
 * Xen工具类
 * 
 * @author Administrator
 *
 */
public final class XenTools {

	private static Object mutex = new Object();

	private XenTools() {
	}

	/**
	 * 获取池基本信息
	 * 
	 * @return
	 */
	public static PoolInfo getPoolInfo() {
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			Pool pool = (Pool) Pool.getAll(conn).toArray()[0];
			PoolInfo poolInfo = null;
			if (pool != null) {
				poolInfo = new PoolInfo();
				poolInfo.setName(pool.getNameLabel(conn));
				poolInfo.setUuid(pool.getUuid(conn));
				poolInfo.setOtherConfig(pool.getOtherConfig(conn));
				poolInfo.setWlbEnabled(pool.getWlbEnabled(conn));
				poolInfo.setWlbUrl(pool.getWlbUrl(conn));
				poolInfo.setWlbUsername(pool.getWlbUsername(conn));
				poolInfo.setLicenseState(pool.getLicenseState(conn));
				poolInfo.setNameDescription(pool.getNameDescription(conn));
			}
			return poolInfo;
		} catch (Exception e) {
			System.err.println("XenTools.getPoolInfo调用出错,e=" + e.getMessage());
			return null;
		} finally {
			ConnectionUtil.release(conn);
		}
	}

	/**
	 * 获取池名称
	 * 
	 * @return 池名称
	 */
	public static String getPoolName() {

		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			Pool pool = (Pool) Pool.getAll(conn).toArray()[0];
			return pool.getNameLabel(conn);

		} catch (Exception e) {
			System.err.println("XenTools.getPoolName调用出错,e=" + e.getMessage());
			return null;
		} finally {
			ConnectionUtil.release(conn);
		}
	}

	public static Set<String> getHostsUuid() {
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			Set<Host> hosts = Host.getAll(conn);

			Set<String> uuids = new HashSet<String>(hosts.size());
			for (Host host : hosts) {
				uuids.add(host.getUuid(conn));
			}
			return uuids;
		} catch (Exception e) {
			System.err.println("XenTools.getHostsUuid调用出错,e=" + e.getMessage());
			return null;
		} finally {
			ConnectionUtil.release(conn);
		}
	}

	/**
	 * 
	 * 获取所有主机名称
	 * 
	 * @return 所有主机的名称
	 */
	public static Set<String> getHostsName() {
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			Set<Host> hosts = Host.getAll(conn);
			Set<String> names = new HashSet<String>(hosts.size());
			for (Host host : hosts) {
				names.add(host.getNameLabel(conn));
			}
			return names;
		} catch (Exception e) {
			System.err.println("XenTools.getHostsName调用出错,e=" + e.getMessage());
			return null;
		} finally {
			ConnectionUtil.release(conn);
		}
	}

	
	/**
	 * 获取主机内运行的虚拟机的
	 * @param uuid
	 * @return
	 */
	public static Set<VM> getVMFromHost(String uuid,boolean containControl){
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			Set<VM> vms = VM.getAll(conn);
			Set<VM> vMs = new HashSet<VM>();
			for (VM vm : vms) {
				if (vm.getIsATemplate(conn) || (vm.getIsControlDomain(conn)&& !containControl)) {
					continue;
				}
				if (XenTools.isRunning(vm.getUuid(conn))) {
					// 如果开机状态，则判断register
					if (vm.getResidentOn(conn).getUuid(conn).equals(uuid)) {
						vMs.add(vm);
					}
				} 
			}
			return vMs;
		} catch (Exception e) {
			System.err.println("XenTools.getVMFromHost调用出错,uuid=" + uuid + ",e=" + e.getMessage());
			return null;
		} finally {
			ConnectionUtil.release(conn);
		}
	}
	/**
	 * 根据主机名称，获取主机内VM的名称，并排除类型为模板和类型为controlDomain的虚拟机
	 * 
	 * @param hostName主机名称
	 * @return
	 */
	public static Set<String> getVmsNamesFromHost(String hostName) {
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			Set<VM> vms = VM.getAll(conn);
			Set<String> names = new HashSet<String>();
			for (VM vm : vms) {
				if (vm.getIsATemplate(conn) || vm.getIsControlDomain(conn)) {
					System.out.println(vm.getUuid(conn));
					continue;
				}
				if (XenTools.isRunning(vm.getUuid(conn))) {
					// 如果开机状态，则判断register
					System.out.println(vm.getUuid(conn));
					if (vm.getResidentOn(conn).getNameLabel(conn).equals(hostName)) {
						names.add(vm.getNameLabel(conn));
					}
				} else {
					System.out.println(vm.getUuid(conn));
					if (vm.getAffinity(conn).getNameLabel(conn).equals(hostName)) {
						names.add(vm.getNameLabel(conn));
					}
				}
			}
			return names;
		} catch (Exception e) {
			System.err.println("XenTools.getVmsNamesFromHost调用出错,hostName=" + hostName + ",e=" + e.getMessage());
			return null;
		} finally {
			ConnectionUtil.release(conn);
		}
	}

	/**
	 * 根据主机名称，获取主机内SR的名称
	 * 
	 * @param hostName主机名称
	 * @return
	 */
	public static Set<String> getSrsNamesFromHost(String hostName) {
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			Host host = (Host) Host.getByNameLabel(conn, hostName).toArray()[0];
			Set<SR> srs = new HashSet<SR>();
			srs.add(host.getSuspendImageSr(conn));
			Set<String> srNames = new HashSet<String>(srs.size());
			for (SR sr : srs) {
				srNames.add(sr.getNameLabel(conn));
			}
			return srNames;
		} catch (Exception e) {
			System.err.println("XenTools.getSrsNamesFromHost调用出错,hostName=" + hostName + ",e=" + e.getMessage());
			return null;
		} finally {
			ConnectionUtil.release(conn);
		}
	}

	/**
	 * 获取整个池的共享SR库名称
	 * 
	 * @return
	 */
	public static Set<String> getShareSrNames() {
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			Set<SR> srs = SR.getAll(conn);
			Set<String> srsNames = new HashSet<String>();
			for (SR sr : srs) {
				if (sr.getShared(conn)) {
					srsNames.add(sr.getNameLabel(conn));
				}
			}
			return srsNames;
		} catch (Exception e) {
			System.err.println("XenTools.getShareSrNames调用出错,e=" + e.getMessage());
			return null;
		} finally {
			ConnectionUtil.release(conn);
		}
	}

	/**
	 * 
	 * 判断指定虚拟机运行状态
	 * 
	 * @param vmID虚拟机Id
	 * @return 开机或关机
	 */
	public static boolean isRunning(String vmId) {
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			VM vm = VM.getByUuid(conn, vmId);
			if (Types.VmPowerState.RUNNING == vm.getPowerState(conn)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.err.println("XenTools.isRunning调用出错,e=" + e.getMessage());
			return false;
		} finally {
			ConnectionUtil.release(conn);
		}
	}

	/**
	 * 根据vm名称判断vm运行状态
	 * 
	 * @param vmName
	 * @return
	 */
	public static boolean isRunningByName(String vmName) {
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			VM vm = (VM) VM.getByNameLabel(conn, vmName).toArray()[0];
			if (Types.VmPowerState.RUNNING == vm.getPowerState(conn))
				return true;
			else
				return false;
		} catch (Exception e) {
			System.err.println("XenTools.isRunningByName调用出错,e=" + e.getMessage());
			return false;
		} finally {
			ConnectionUtil.release(conn);
		}
	}

	/**
	 * 获取所有主机名称，和对应的运行在其上面的VM集合
	 * 
	 * @return
	 */
	public static Map<String, Set<VM>> getVmsRunOnHost() {
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			Set<Host> hosts = Host.getAll(conn);
			Map<String, Set<VM>> vmsOnHost = null;
			if (CollectionUtils.isNotEmpty(hosts)) {
				vmsOnHost = new HashMap<String, Set<VM>>();
				for (Host host : hosts) {
					Set<VM> vms = host.getResidentVMs(conn);
					vmsOnHost.put(host.getNameLabel(conn), vms);
				}
			}
			return vmsOnHost;
		} catch (Exception e) {
			System.err.println("XenTools.getVmsRunOnHost调用出错,e=" + e.getMessage());
			return null;
		} finally {
			ConnectionUtil.release(conn);
		}
	}

	/**
	 * 根据虚拟机对象，获取虚拟机名称
	 * 
	 * @param vm虚拟机对象
	 * @return
	 */
	public static String getNameOfVM(VM vm) {
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			return vm.getNameLabel(conn);
		} catch (Exception e) {
			System.err.println("XenTools.getVmsRunOnHost调用出错,vm=" + vm + " e=" + e.getMessage());
			return null;
		} finally {
			ConnectionUtil.release(conn);
		}
	}

	/**
	 * 获取虚拟机被分配内存大小，单位M
	 * @param vm
	 * @param vmName
	 * @return
	 */
	public static double getMemoryOfVM(VM vm, String vmName) {
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			if (vm != null) {
				return vm.getMetrics(conn).getMemoryActual(conn) / 1024 / 1024;
			} else {
				VM vm1 = (VM) VM.getByNameLabel(conn, vmName).toArray()[0];
				return vm1.getMetrics(conn).getMemoryActual(conn) / 1024 / 1024;
			}

		} catch (Exception e) {
			System.err.println("XenTools.getMemoryOfVM 调用出错,vm=" + vm + " e=" + e.getMessage());
			return 0;
		} finally {
			ConnectionUtil.release(conn);
		}
	}

	/**
	 * 获取主机基本信息
	 * 
	 * @param hostName
	 * @return
	 */
	public static ServerInfo getServerInfoByName(String hostName) {
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			Host master = (Host) Host.getByNameLabel(conn, hostName).toArray()[0];
			ServerInfo serverInfo = new ServerInfo();
			serverInfo.setName(master.getHostname(conn));
			serverInfo.setUuid(master.getUuid(conn));
			serverInfo.setCpu(master.getCpuInfo(conn));
			serverInfo.setTotalMemory(master.getMemoryOverhead(conn).toString());
			serverInfo.setIp(master.getAddress(conn));
			serverInfo.setOther(master.getOtherConfig(conn));
			serverInfo.setAPIversion(master.getAPIVersionVendor(conn));
			serverInfo.setEdition(master.getEdition(conn));
			serverInfo.setPowerOnMode(master.getPowerOnMode(conn));
			return serverInfo;
		} catch (Exception e) {
			System.err.println("XenTools.getServerInfoByName 调用出错,hostName=" + hostName + " e=" + e.getMessage());
			return null;
		} finally {
			ConnectionUtil.release(conn);
		}
	}

	/**
	 * 获取主机的内存使用率，用于界面显示
	 * 
	 * @param hostName
	 * @return
	 */
	public static List<PieChart.Data> getMemoryUsageByName(String hostName) {
		List<PieChart.Data> list = new ArrayList<PieChart.Data>();
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			Host master = (Host) Host.getByNameLabel(conn, hostName).toArray()[0];
			HostMetrics m = master.getMetrics(conn);
			long total = m.getMemoryTotal(conn) / 1024 / 1024;
			long use = 0;
			Set<VM> vms = master.getResidentVMs(conn);
			for (VM vm : vms) {
				long ms = vm.getMetrics(conn).getMemoryActual(conn) / 1024 / 1024;
				PieChart.Data pd = new PieChart.Data(vm.getNameLabel(conn) + " : " + ms + "M", ms);// (double)((vm.getMetrics(conn).getMemoryActual(conn)/total)*100)
				list.add(pd);
				use = use + vm.getMetrics(conn).getMemoryActual(conn) / 1024 / 1024;
			}
			list.add(new PieChart.Data("未使用 : " + (total - use) + "M", total - use));// (double)((total-use)/total*100)
			return list;
		} catch (Exception e) {
			System.err.println("XenTools.getMemoryUsageByName 调用出错,hostName=" + hostName + " e=" + e.getMessage());
			return list;
		} finally {
			ConnectionUtil.release(conn);
		}
	}

	/**
	 * 通过sr名称获取sr下面的所有vdi，用于界面显示，主要是显示共享sr的内容
	 * 
	 * @param srName
	 * @return
	 */
	public static List<Vdi> getVDIsbySrName(String srName) {
		List<Vdi> list = new ArrayList<Vdi>();
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			SR sr = (SR) SR.getByNameLabel(conn, srName).toArray()[0];
			Set<VDI> vdis = sr.getVDIs(conn);
			for (VDI vdi : vdis) {
				Vdi v = null;
				if (vdi.getVBDs(conn).size() > 0) {
					VBD vbd = (VBD) vdi.getVBDs(conn).toArray()[0];
					v = new Vdi(vdi.getNameLabel(conn), vdi.getNameDescription(conn),
							Long.toString(vdi.getVirtualSize(conn) / 1024 / 1024 / 1024) + "G",
							vbd.getVM(conn).getNameLabel(conn));
				} else {
					v = new Vdi(vdi.getNameLabel(conn), vdi.getNameDescription(conn),
							Long.toString(vdi.getVirtualSize(conn) / 1024 / 1024 / 1024) + "G", " ");
				}
				list.add(v);
			}
			return list;
		} catch (Exception e) {
			System.err.println("XenTools.getVDIsbySrName 调用出错,srName=" + srName + " e=" + e.getMessage());
			return list;
		} finally {
			ConnectionUtil.release(conn);
		}
	}

	/**
	 * 根据虚拟机名称，获取虚拟机的基本信息，封装成domain返回
	 * 
	 * @param nameVm
	 * @return
	 */
	public static VmInfo getVmInfoByNameOrUuid(String nameVm, String uuid) {
		// TODO Auto-generated method stub
		VmInfo instance = new VmInfo();
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			VM vm = null;
			if (StringUtils.isNotEmpty(nameVm)) {
				vm = (VM) VM.getByNameLabel(conn, nameVm).toArray()[0];
			} else {
				vm = VM.getByUuid(conn, uuid);
			}
			instance.setName(vm.getNameLabel(conn));
			instance.setUuid(vm.getUuid(conn));
			instance.setOs(vm.getGuestMetrics(conn).getOsVersion(conn).get("name").split("\\|")[0]);
			instance.setLastTime(vm.getGuestMetrics(conn).getLastUpdated(conn).toLocaleString());
			instance.setMemory(Long.toString(vm.getMetrics(conn).getMemoryActual(conn) / 1024 / 1024 / 1024));
			instance.setCpu(Long.toString(vm.getVCPUsMax(conn)));
			instance.setState(vm.getPowerState(conn).toString());
			instance.setVifs(vm.getVIFs(conn).size());
			instance.setVbds(vm.getVBDs(conn).size());
			if (vm.getPowerState(conn) != Types.VmPowerState.RUNNING) {
				instance.setVmIp("<无>");
			} else {
				VM.Record vmRecord = vm.getRecord(conn);
				VMGuestMetrics vmGuestMetrics = vmRecord.guestMetrics;
				Set<VIF> vifs = vm.getVIFs(conn);
				Map<String, String> netWorks = vmGuestMetrics.getNetworks(conn);
				for (VIF vif : vifs) {
					for (Map.Entry<String, String> netWork : netWorks.entrySet()) {
						if (netWork.getKey().equals(String.format("%s/ip", vif.getDevice(conn))))
							instance.setVmIp(netWork.getValue().toString());
					}
				}
			}
			return instance;
		} catch (Exception e) {
			System.err.println("XenTools.getVmInfoByName 调用出错,nameVm=" + nameVm + " e=" + e.getMessage());
			return instance;
		} finally {
			ConnectionUtil.release(conn);
		}
	}

	public static void startVmByName(String vmName) {
		// TODO Auto-generated method stub
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			if (vmName == "")
				JOptionPane.showMessageDialog(null, "请选中虚拟机", "错误", JOptionPane.ERROR_MESSAGE);
			else {

				VM vm = (VM) VM.getByNameLabel(conn, vmName).toArray()[0];
				if (vm.getPowerState(conn) != Types.VmPowerState.RUNNING) {
					vm.startAsync(conn, false, false);
				} else {
					JOptionPane.showMessageDialog(null, "虚拟机处于开启状态", "重复开启", JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (Exception e) {
			System.err.println("XenTools.startVmByName 调用出错,vmName=" + vmName + " e=" + e.getMessage());
		} finally {
			ConnectionUtil.release(conn);
		}
	}

	public static void closeVmByName(String vmName) {
		// TODO Auto-generated method stub
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			if (vmName == "")
				JOptionPane.showMessageDialog(null, "请选中虚拟机", "错误", JOptionPane.ERROR_MESSAGE);
			else {
				VM vm = (VM) VM.getByNameLabel(conn, vmName).toArray()[0];
				if (vm.getPowerState(conn) != Types.VmPowerState.HALTED) {
					vm.shutdownAsync(conn);
				} else {
					JOptionPane.showMessageDialog(null, "虚拟机处于关机状态", "重复开启", JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (Exception e) {
			System.err.println("XenTools.startVmByName 调用出错,vmName=" + vmName + " e=" + e.getMessage());
		} finally {
			ConnectionUtil.release(conn);
		}
	}

	/**
	 * 根据虚拟机名称，获取该虚拟机所隶属的根服务器名称
	 * 
	 * @param name
	 * @return
	 */
	public static String getAffiy(String name) {
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			VM vm = (VM) VM.getByNameLabel(conn, name).toArray()[0];
			return vm.getAffinity(conn).getNameLabel(conn);
		} catch (Exception e) {
			System.err.println("XenTools.getAffiy 调用出错,name=" + name + " e=" + e.getMessage());
			return null;
		} finally {
			ConnectionUtil.release(conn);
		}
	}

	/**
	 * 通过判断该虚拟机的挂在的vbd是否在共享存储中，来判断是否支持迁移
	 * 
	 * @param name
	 * @return
	 */

	public static boolean isMigration(String name) {

		boolean isMigration = false;
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			VM vm = (VM) VM.getByNameLabel(conn, name).toArray()[0];
			Set<VBD> vbds = vm.getVBDs(conn);
			for (VBD vbd : vbds) {
				if (vbd.getDevice(conn).toString().equals("xvdd"))
					continue;
				VDI vdi = vbd.getVDI(conn);
				isMigration = vdi.getSR(conn).getShared(conn);
			}
			return isMigration;
		} catch (Exception e) {
			System.err.println("XenTools.isMigration 调用出错,name=" + name + " e=" + e.getMessage());
			return false;
		} finally {
			ConnectionUtil.release(conn);
		}
	}

	/**
	 * 通过虚拟机名称获取该虚拟机的运行主机，前提是该虚拟机处于开机状态
	 * 
	 * @param name
	 * @return
	 */
	public static String getRegister(String name) {
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			VM vm = (VM) VM.getByNameLabel(conn, name).toArray()[0];
			return vm.getResidentOn(conn).getNameLabel(conn);
		} catch (Exception e) {
			System.err.println("XenTools.getRegister 调用出错,name=" + name + " e=" + e.getMessage());
			return null;
		} finally {
			ConnectionUtil.release(conn);
		}
	}

	/**
	 * 迁移指定虚拟机到指定目标主机上
	 * 
	 * @param targetName
	 * @param name
	 */
	public static void migration(String targetName, String name) {
		// TODO Auto-generated method stub
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			Host host = (Host) Host.getByNameLabel(conn, targetName).toArray()[0];
			VM vm = (VM) VM.getByNameLabel(conn, name).toArray()[0];
			vm.poolMigrate(conn, host, new HashMap<String, String>());
		} catch (Exception e) {
			System.err.println(
					"XenTools.migration 调用出错,targetName=" + targetName + " name=" + name + " e=" + e.getMessage());
			throw new RuntimeException(e);
		} finally {
			ConnectionUtil.release(conn);
		}
	}

	public static Map<String, Map<Integer, double[]>> getVmPerform(String name) {
		Connection conn = null;
		try {

			conn = ConnectionUtil.getConnection();
			VM vm = (VM) VM.getByNameLabel(conn, name).toArray()[0];
			Host host = vm.getResidentOn(conn);
			URL url = new URL(
					"http://" + host.getAddress(conn) + "/rrd_updates?start=" + (System.currentTimeMillis() / 1000));
			URLConnection urlConnection = url.openConnection();
			String encoding = new BASE64Encoder().encode(("root" + ":" + "123456").getBytes());
			urlConnection.setRequestProperty("Authorization", "Basic " + encoding);
			String rrdXportData = IOUtils.toString(urlConnection.getInputStream());
			int endTime = 0;
			HashMap<String, double[]> metricsTimelines = null;
			int startTime = 0;
			int step = 0;

			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setNamespaceAware(true);
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			StringReader stringReader = new StringReader(rrdXportData);
			InputSource inputSource = new InputSource(stringReader);
			Document doc = builder.parse(inputSource);
			stringReader.close();

			ArrayList<ArrayList<String>> dataRows = new ArrayList<ArrayList<String>>();
			ArrayList<String> legends = new ArrayList<String>();
			NodeList xportChildNodes = doc.getDocumentElement().getChildNodes();
			for (int i = 0; i < xportChildNodes.getLength(); i++) {
				Node xportChildNode = xportChildNodes.item(i);
				if (xportChildNode.getNodeName().equals("meta")) {
					NodeList metaChildNodes = xportChildNode.getChildNodes();
					for (int j = 0; j < metaChildNodes.getLength(); j++) {
						Node metaChildNode = metaChildNodes.item(j);
						if (metaChildNode.getNodeName().equals("step")) {
							step = new Integer(metaChildNode.getTextContent()).intValue();
						} else if (metaChildNode.getNodeName().equals("start")) {
							startTime = new Integer(metaChildNode.getTextContent()).intValue();
						} else if (metaChildNode.getNodeName().equals("end")) {
							endTime = new Integer(metaChildNode.getTextContent()).intValue();
						} else if (metaChildNode.getNodeName().equals("legend")) {
							NodeList legendChildNodes = metaChildNode.getChildNodes();
							for (int k = 0; k < legendChildNodes.getLength(); k++) {
								Node legendChildNode = legendChildNodes.item(k);
								legends.add(k, legendChildNode.getTextContent());
							}
						}
					}
				} else if (xportChildNode.getNodeName().equals("data")) {
					NodeList dataChildNodes = xportChildNode.getChildNodes();
					for (int j = 0; j < dataChildNodes.getLength(); j++) {
						Node rowNode = dataChildNodes.item(j);
						NodeList rowChildNodes = rowNode.getChildNodes();
						ArrayList<String> dataRow = new ArrayList<String>();
						for (int k = 1; k < rowChildNodes.getLength(); k++) {
							Node rowChildNode = rowChildNodes.item(k);
							dataRow.add(k - 1, rowChildNode.getTextContent());
						}
						dataRows.add(dataRow);
					}
				}
			}
			int nrDataRows = dataRows.size();
			int nrLegends = legends.size();
			metricsTimelines = new HashMap<String, double[]>();
			for (int i = 0; i < nrLegends; i++) {
				metricsTimelines.put(legends.get(i), new double[nrDataRows]);
			}
			for (int i = 0; i < nrLegends; i++) {
				for (int j = 0; j < nrDataRows; j++) {
					double[] values = metricsTimelines.get(legends.get(i));
					values[j] = new Double(dataRows.get(j).get(i)).doubleValue();
				}
			}
			String uuid = vm.getUuid(conn);
			int cpuNums = vm.getVCPUsMax(conn).intValue();
			int vifs = vm.getVIFs(conn).size();
			// int vbds = vm.getVBDs(conn).size();

			// 获得cpu使用率
			Map<Integer, double[]> cpuMap = new HashMap<Integer, double[]>();
			for (int i = 0; i < cpuNums; i++) {
				cpuMap.put(i, metricsTimelines.get("AVERAGE:vm:" + uuid + ":cpu" + i));
			}

			// 获得内存使用率
			Map<Integer, double[]> memoryMap = new HashMap<Integer, double[]>();
			memoryMap.put(0, metricsTimelines.get("AVERAGE:vm:" + uuid + ":memory_internal_free"));

			// 获得网络T使用率
			Map<Integer, double[]> vifTMap = new HashMap<Integer, double[]>();
			for (int i = 0; i < vifs; i++) {
				vifTMap.put(i, metricsTimelines.get("AVERAGE:vm:" + uuid + ":vif_" + i + "_tx"));
			}

			// 获得网络R使用率
			Map<Integer, double[]> vifRMap = new HashMap<Integer, double[]>();
			for (int i = 0; i < vifs; i++) {
				vifRMap.put(i, metricsTimelines.get("AVERAGE:vm:" + uuid + ":vif_" + i + "_rx"));
			}

			Map<Integer, double[]> vbdWMap = new HashMap<Integer, double[]>();
			vbdWMap.put(0, metricsTimelines.get("AVERAGE:vm:" + uuid + ":vbd_xvda_write"));

			Map<Integer, double[]> vbdRMap = new HashMap<Integer, double[]>();
			vbdRMap.put(0, metricsTimelines.get("AVERAGE:vm:" + uuid + ":vbd_xvda_read"));

			Map<String, Map<Integer, double[]>> preform = new HashMap<String, Map<Integer, double[]>>();
			preform.put("cpuMap", cpuMap);
			preform.put("memoryMap", memoryMap);
			preform.put("vifTMap", vifTMap);
			preform.put("vifRMap", vifRMap);
			preform.put("vbdWMap", vbdWMap);
			preform.put("vbdRMap", vbdRMap);
			return preform;
		} catch (Exception e) {
			System.err.println("XenTools.getVmPerform 调用出错,vmName=" + name + " e=" + e.getMessage());
			throw new RuntimeException(e);
		} finally {
			ConnectionUtil.release(conn);
		}
	}

	/**
	 * 获取所有iso的名称和uuid，用于创建新的虚拟机
	 * 
	 * @return
	 */
	public static Set<String> getISOSrNameAndUUID() {
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			Set<String> srNamesAndUuids = new HashSet<String>();
			Set<SR> srs = SR.getAll(conn);
			if (CollectionUtils.isNotEmpty(srs)) {
				for (SR sr : srs) {
					if (sr.getType(conn).equals("iso")) {
						srNamesAndUuids.add(sr.getNameLabel(conn) + ":" + sr.getUuid(conn));
					}
				}
			}
			return srNamesAndUuids;
		} catch (Exception e) {
			System.err.println("XenTools.getISOSrNameAndUUID 调用出错 e=" + e.getMessage());
			return new HashSet<String>();
		} finally {
			ConnectionUtil.release(conn);
		}
	}

	/**
	 * 根据SR的uuid获取该sr中的所有VDI和名称和uuid
	 * 
	 * @param uuid
	 * @return
	 */
	public static Set<String> getVDIFromSR(String uuid) {
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			SR sr = SR.getByUuid(conn, uuid);
			Set<VDI> vdis = sr.getVDIs(conn);
			Set<String> vdiNamesAndUuids = new HashSet<String>();
			if (CollectionUtils.isNotEmpty(vdis)) {
				for (VDI vdi : vdis) {
					vdiNamesAndUuids.add(vdi.getNameLabel(conn) + ":" + vdi.getUuid(conn));
				}
			}
			return vdiNamesAndUuids;
		} catch (Exception e) {
			System.err.println("XenTools.getVDIFromSR 调用出错 uuid=" + uuid + "e=" + e.getMessage());
			return new HashSet<String>();
		} finally {
			ConnectionUtil.release(conn);
		}
	}

	/**
	 * 根据targetIp获取目标存储服务器上的IQN 和iSCSIid
	 * 
	 * @param targetIp
	 * @return
	 */
	public static Map<String, String> probIscsiSR(String targetIp) {
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			// 开始探测。将探测的结果添加到IQN和SCSIid中
			Map<String, String> info = new HashMap<String, String>();
			Map<String, String> deviceConfig = new HashMap<String, String>();
			deviceConfig.put("target", targetIp);
			try {
				SR.probe(conn, getHost(conn), deviceConfig, "lvmoiscsi", new HashMap<String, String>());
			} catch (Exception e) {
				String[] slit = e.toString().split("<TargetIQN>");
				String[] slit1 = slit[1].split("</TargetIQN>");
				String iqn = slit1[0].trim();
				// 将IQN添加到框中
				info.put("iqn", iqn);
				deviceConfig.put("targetIQN", iqn);
				try {
					SR.probe(conn, getHost(conn), deviceConfig, "lvmoiscsi", new HashMap<String, String>());
				} catch (Exception e1) {
					slit = e1.toString().split("<SCSIid>");
					slit1 = slit[1].split("</SCSIid>");
					String scsiId = slit1[0].trim();
					// 加入框中
					info.put("id", scsiId);
					return info;
				}
			}
			return null;
		} catch (Exception e) {
			System.err.println("XenTools.probIscsiSR 调用出错 targetIp=" + targetIp + "e=" + e.getMessage());
			return new HashMap<String, String>();
		} finally {
			ConnectionUtil.release(conn);
		}
	}

	public static Host getHost(Connection conn) {
		try {
			Pool pool = (Pool) Pool.getAll(conn).toArray()[0];
			Host master = pool.getMaster(conn);
			return master;
		} catch (Exception e) {
			System.err.println("XenTools.getHost 调用出错  e=" + e.getMessage());
			return null;
		}
	}

	/**
	 * 创建共享sr
	 * 
	 * @param srName
	 * @param target
	 * @param iqn
	 * @param scsiId
	 */
	public static void createShareSR(String srName, String target, String iqn, String scsiId) {
		// TODO Auto-generated method stub
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			Host host = getHost(conn);
			Map<String, String> deviceConfig = new HashMap<String, String>();
			deviceConfig.put("target", target);
			deviceConfig.put("targetIQN", iqn);
			deviceConfig.put("SCSIid", scsiId);
			String type = "lvmoiscsi";
			String contentType = "user";
			Boolean shared = true;
			Long pyhsical = 100000L;
			String desc = "createBySnow";
			SR sr;
			sr = SR.create(conn, host, deviceConfig, pyhsical, srName, desc, type, contentType, shared);
		} catch (Exception e) {
			System.err.println("XenTools.createShareSR 调用出错" + "srName = " + srName + "target = " + target + "iqn = "
					+ iqn + "scsiId = " + scsiId + "  e=" + e.getMessage());
			return;
		} finally {
			ConnectionUtil.release(conn);
		}
	}

	/**
	 * 根据虚拟机uuid，获取虚拟机的ip地址。
	 * 
	 * @param vmUuid
	 * @return
	 */
	public static String getIpByUuid(String vmUuid) {
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			String ipAdd = "";
			while (true) {
				try {
					VM vmGetFromId = VM.getByUuid(conn, vmUuid);
					// 判断虚拟机是否已经启动
					if (Types.VmPowerState.RUNNING != vmGetFromId.getPowerState(conn)) {
						// 启动虚拟机
						vmGetFromId.start(conn, false, false);

					}
					VM.Record vmRecord = vmGetFromId.getRecord(conn);
					VMGuestMetrics vmGuestMetrics = vmRecord.guestMetrics;
					Set<VIF> vifs = vmGetFromId.getVIFs(conn);
					Map<String, String> netWorks = vmGuestMetrics.getNetworks(conn);
					for (VIF vif : vifs) {
						for (Map.Entry<String, String> netWork : netWorks.entrySet()) {
							if (netWork.getKey().equals(String.format("%s/ip", vif.getDevice(conn))))
								// ipAdd = ipAdd + " * "
								// +netWork.getValue().toString();
								ipAdd = netWork.getValue().toString();
						}
					}
					if (ipAdd.equals(""))
						continue;
					return ipAdd;
				} catch (Exception e) {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					continue;
				}
			}
		} catch (Exception e) {
			System.err.println("XenTools.getIpByUuid 调用出错" + "vmUuid = " + vmUuid + "  e=" + e.getMessage());
			throw new RuntimeException(e);
		} finally {
			ConnectionUtil.release(conn);
		}
	}

	/**
	 * 创建新的虚拟机，并返回虚拟机信息
	 * 
	 * @param vmname
	 * @return
	 */
	public static VmInfo createWin10ByTemple(String vmName) {
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			VM temple = VM.getByUuid(conn, "6d7ca59a-4e4d-0c2f-8c28-a262d68a777f");
			VM newVM = temple.createClone(conn, vmName);
			newVM.provision(conn);
			newVM.start(conn, false, false);
			VmInfo vmInfo = getVmInfoByNameOrUuid(null, newVM.getUuid(conn));
			return vmInfo;
		} catch (Exception e) {
			System.err.println("XenTools.createWin10ByTemple 调用出错" + "vmname = " + vmName + "  e=" + e.getMessage());
			return null;
		} finally {
			ConnectionUtil.release(conn);
		}
	}

	/**
	 * 获取主机的剩余可用内存，B单位
	 * @param uuid
	 * @return
	 */
	public static Long getServerRestMemory(String uuid){
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			Host host = Host.getByUuid(conn, uuid);
			HostMetrics m = host.getMetrics(conn);
			long total = m.getMemoryTotal(conn);
			long result = 0l;
			Set<VM> vms = getVMFromHost(uuid,true);
			for(VM vm : vms){
				result += vm.getMemoryDynamicMin(conn); 
			}
			return total - result;
		} catch (Exception e) {
			System.err.println("XenTools.getServerRestMemory 调用出错" + "uuid = " + uuid + "  e=" + e.getMessage());
			return null;
		} finally {
			ConnectionUtil.release(conn);
		}
	}
	
	/**
	 * 根据服务器uuid，获取该服务器的资源信息,主要包含运行的虚拟机
	 * 
	 * @param uuid
	 * @return
	 */
	public static ServerResource getServerResourceByUuid(String uuid) {
		synchronized (mutex) {
			Connection conn = null;
			try {
				ServerResource serverResource = new ServerResource();
				serverResource.setUuid(uuid);
				conn = ConnectionUtil.getConnection();
				Host host = Host.getByUuid(conn, uuid);
				HostMetrics m = host.getMetrics(conn);
				serverResource.setTotalMemory(m.getMemoryTotal(conn));
				serverResource.setRestMemory(getServerRestMemory(uuid));
				serverResource.setCpuNum(Integer.parseInt(host.getCpuInfo(conn).get("cpu_count")));
				serverResource.setSpeed(Integer.parseInt(host.getCpuInfo(conn).get("speed")));
				Map<String, VmResource> allVmResource = new HashMap<String, VmResource>();
				Set<VM> vms = host.getResidentVMs(conn);
				if (CollectionUtils.isNotEmpty(vms)) {
					for (VM vm : vms) {
						if(vm.getIsControlDomain(conn)){
							continue;
						}
						String uuidOfVm = vm.getUuid(conn);
						VmResource vmResource = new VmResource();
						vmResource.setUuid(uuidOfVm);
						vmResource.setTotalMemory(getMemoryOfVM(vm, ""));
						
						vmResource.setCpuNum(vm.getVCPUsMax(conn).intValue());
						Map<String, Map<Integer, double[]>> state = getVmPerform(vm.getNameLabel(conn));
						vmResource.setState(state);
						allVmResource.put(uuidOfVm, vmResource);
					}
				}	
				serverResource.setAllVmResource(allVmResource);
				return serverResource;
			} catch (Exception e) {
				System.err.println("XenTools.getServerResourceByUuid 调用出错" + "uuid = " + uuid + "  e=" + e.getMessage());
				return null;
			} finally {
				ConnectionUtil.release(conn);
			}
		}
	}

	/**
	 * 获取VM的迁移因子
	 * @param vm
	 * @return
	 */
	public static long getVmFactor(VM vm){
		Connection conn = null;
		try{
			conn = ConnectionUtil.getConnection();
			String name = vm.getNameLabel(conn);
			long memory = vm.getMetrics(conn).getMemoryActual(conn);
			Map<String, Map<Integer, double[]>> state = getVmPerform(name);
			Map<Integer, double[]> cpuMap = state.get("cpuMap");
			double cpuAverageResult = 0.0;
			for(Map.Entry<Integer, double[]> entry : cpuMap.entrySet()){
				double[] ds = entry.getValue();
				double average = Tools.getAverage(ds);
				cpuAverageResult += average;
			}
			if(cpuAverageResult == 0.0){
				return Long.MAX_VALUE;
			}
			return (long) (memory/(cpuAverageResult * 100));
			
		}catch (Exception e) {
			System.err.println("XenTools.getVmFactor 调用出错,e=" + e.getMessage());
			return Long.MAX_VALUE;
		}finally {
			ConnectionUtil.release(conn);
		}
	}
	/**
	 * 根据选择策略，选择一个合适VM进行迁移memory/cpu,求和(cpu[i]*num)
	 * @param uuid
	 * @return 虚拟机名称
	 */
	public static String getVmBySelectStrategy(String uuid) {
		// TODO Auto-generated method stub
		Connection conn = null;
		try{
			conn = ConnectionUtil.getConnection();
			Set<VM> vms = getVMFromHost(uuid, false);
			long factor = Long.MAX_VALUE;
			VM target = null;
			if(CollectionUtils.isNotEmpty(vms)){
				for(VM vm : vms){
					long vFactor = getVmFactor(vm);
					System.out.println(vm.getNameLabel(conn)+vFactor);
					if(vFactor <= factor){
						factor = vFactor;
						target = vm;
					}
				}
			}
			if(target == null){
				return null;
			}else{
				return target.getNameLabel(conn);
			}
		}catch (Exception e) {
			System.err.println("XenTools.getVmBySelectStrategy 调用出错" + "uuid = " + uuid + "  e=" + e.getMessage());
			return null;
		}finally {
			ConnectionUtil.release(conn);
		}
		
	}

	
	/**
	 * 根据目标选取策略，选取主机
	 * @return
	 */
	public static String hostSelectStrategy() {
		Connection conn = null;
		try{
			Set<Host> hosts =Host.getAll(conn);
			
		}catch (Exception e) {
			System.err.println("XenTools.hostSelectStrategy 调用出错  e=" + e.getMessage());
			return null;
		}finally {
			ConnectionUtil.release(conn);
		}
	}
}
