package com.vm.thread;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sun.tracing.dtrace.ProviderAttributes;
import com.vm.constant.CommandType;
import com.vm.constant.ReturnType;
import com.vm.domain.User;
import com.vm.domain.VmInfo;
import com.vm.entity.RequestMesg;
import com.vm.entity.ResponseMesg;
import com.vm.service.UserService;
import com.vm.service.VmInfoService;
import com.vm.service.XenService;

/**
 * �����û�����
 * 
 * @author Administrator
 *
 */
@Component("acceptCustomer")
public class AcceptCustomer extends Thread {

	private Socket client;
	private ServerSocket server;

	@Autowired
	private XenService xenService;
	@Autowired
	private UserService userService;
	@Autowired
	private VmInfoService vmInfoService;

	@Override
	public void run() {
		try {
			server = new ServerSocket(9527);
			ExecutorService executorService = Executors.newFixedThreadPool(10);
			while (true) {
				client = server.accept();
				executorService.execute(new ServerThread(client));
			}
		} catch (Exception e) {
			System.out.println("��ʼ�������߳�ʧ��" + e.toString());
		}

	}

	/**
	 * ���������û�������
	 * 
	 * @author Administrator
	 *
	 */
	class ServerThread implements Runnable {
		private Socket client;
		private String clientIp;
		private ObjectInputStream readObject;

		public ServerThread(Socket client) throws IOException {
			this.client = client;
			this.clientIp = this.client.getInetAddress().getHostAddress();
			this.readObject = getReadObjectStream(this.client);
		}

		@Override
		public void run() {
			try {
				// TODO Auto-generated method stub
				RequestMesg rm = (RequestMesg) readObject.readObject();
				this.client.close();
				doCommand(rm);
			} catch (Exception e) {
				System.err.println("ServerThread ����clientIp=" + clientIp);
			}
		}

		private void doCommand(RequestMesg rm) throws Exception {
			if (rm.getCommand() == CommandType.TEST_CONNECT) {
				sendMessage(rm.getPortNumber(), "OK", ReturnType.THROUGH_CONNECT);
				return;
			}

			// ����û��ĺϷ���
			if (!userService.checkAuthority(rm.getUserName(), rm.getPassWord())) {
				sendMessage(rm.getPortNumber(), "�û������������", ReturnType.USERNAME_PASSWORD_ERRO);
				return;
			}
			// ��ȡ�û��Ļ�����Ϣ
			Map<String, Object> userInfo = userService.getUserInfo(rm.getUserName());
			User user = (User) userInfo.get("user");
			VmInfo vmInfo = (VmInfo) userInfo.get("vmInfo");
			if (rm.getCommand() == CommandType.GET_IP) {
				// ���û������û�����������û�û�ж�Ӧ�������uuid������ʾ��Ҫע��

				if (user == null || StringUtils.isEmpty((user.getVmUuid()))) {
					sendMessage(rm.getPortNumber(), "�������û�����ע��", ReturnType.REGISTE);
					return;
				}
				if (!xenService.isRunningByUuid(user.getVmUuid())) {
					sendMessage(rm.getPortNumber(), "�ȴ�����", ReturnType.WAIT_START_VM);
				}
				String ip = xenService.getIpByUuid(user.getVmUuid());
				vmInfo.setVmIp(ip);
				sendMessage(rm.getPortNumber(), ip, ReturnType.IP);
				// �������������Ϣ,����ip��Ϣ
				vmInfoService.update(vmInfo);
			}
			if (rm.getCommand() == CommandType.REGISTE) {
				int cpu = Integer.parseInt(rm.getCpuNum());
				int memorySize = Integer.parseInt(rm.getMemorySize().split("G")[0]);
				if (user.getState().equals("register")) {
					sendMessage(rm.getPortNumber(), "�����ظ�ע��", ReturnType.REGISTE_AGAIN);
					return;
				} else {

					user.setState("register");
					userService.update(user);
					VmInfo newVmInfo = xenService.createWin10ByTemple(rm.getVmname());
					if (newVmInfo == null) {
						System.err.println("ע���쳣,ע����Ϣ user=" + user);
						user.setState("unregister");
						userService.update(user);
						sendMessage(rm.getPortNumber(), "ע��ʧ��", ReturnType.REGISTE_FAIL);
						return;
					}
					user.setVmUuid(newVmInfo.getUuid());
					userService.update(user);
					vmInfoService.insert(newVmInfo);
					sendMessage(rm.getPortNumber(), "ע��ɹ�", ReturnType.REGISTE_SUCCESS);
				}
			}
		}

		private void sendMessage(int port, String msg, int command) throws UnknownHostException, IOException {
			Socket socket = new Socket(this.clientIp, port);
			ObjectOutputStream writeObject;
			writeObject = getWritetObjectStream(socket);
			writeObject.writeObject(new ResponseMesg(msg, command));
			socket.close();
		}

		private ObjectOutputStream getWritetObjectStream(Socket socket) throws IOException {
			return new ObjectOutputStream(socket.getOutputStream());
		}

		private ObjectInputStream getReadObjectStream(Socket socket) throws IOException {
			BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
			return new ObjectInputStream(bis);
		}
	}
}
