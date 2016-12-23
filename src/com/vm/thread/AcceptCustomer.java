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
 * 监听用户连接
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
			System.out.println("初始化监听线程失败" + e.toString());
		}

	}

	/**
	 * 用来处理用户的请求
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
				System.err.println("ServerThread 出错，clientIp=" + clientIp);
			}
		}

		private void doCommand(RequestMesg rm) throws Exception {
			if (rm.getCommand() == CommandType.TEST_CONNECT) {
				sendMessage(rm.getPortNumber(), "OK", ReturnType.THROUGH_CONNECT);
				return;
			}

			// 检查用户的合法性
			if (!userService.checkAuthority(rm.getUserName(), rm.getPassWord())) {
				sendMessage(rm.getPortNumber(), "用户名或密码错误", ReturnType.USERNAME_PASSWORD_ERRO);
				return;
			}
			// 获取用户的基本信息
			Map<String, Object> userInfo = userService.getUserInfo(rm.getUserName());
			User user = (User) userInfo.get("user");
			VmInfo vmInfo = (VmInfo) userInfo.get("vmInfo");
			if (rm.getCommand() == CommandType.GET_IP) {
				// 如果没有相关用户，或者相关用户没有对应的虚拟机uuid，则提示需要注册

				if (user == null || StringUtils.isEmpty((user.getVmUuid()))) {
					sendMessage(rm.getPortNumber(), "您是新用户，请注册", ReturnType.REGISTE);
					return;
				}
				if (!xenService.isRunningByUuid(user.getVmUuid())) {
					sendMessage(rm.getPortNumber(), "等待开机", ReturnType.WAIT_START_VM);
				}
				String ip = xenService.getIpByUuid(user.getVmUuid());
				vmInfo.setVmIp(ip);
				sendMessage(rm.getPortNumber(), ip, ReturnType.IP);
				// 更新虚拟机的信息,包括ip信息
				vmInfoService.update(vmInfo);
			}
			if (rm.getCommand() == CommandType.REGISTE) {
				int cpu = Integer.parseInt(rm.getCpuNum());
				int memorySize = Integer.parseInt(rm.getMemorySize().split("G")[0]);
				if (user.getState().equals("register")) {
					sendMessage(rm.getPortNumber(), "不能重复注册", ReturnType.REGISTE_AGAIN);
					return;
				} else {

					user.setState("register");
					userService.update(user);
					VmInfo newVmInfo = xenService.createWin10ByTemple(rm.getVmname());
					if (newVmInfo == null) {
						System.err.println("注册异常,注册信息 user=" + user);
						user.setState("unregister");
						userService.update(user);
						sendMessage(rm.getPortNumber(), "注册失败", ReturnType.REGISTE_FAIL);
						return;
					}
					user.setVmUuid(newVmInfo.getUuid());
					userService.update(user);
					vmInfoService.insert(newVmInfo);
					sendMessage(rm.getPortNumber(), "注册成功", ReturnType.REGISTE_SUCCESS);
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
