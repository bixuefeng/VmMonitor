package com.vm.xen;

import java.net.URL;

import com.xensource.xenapi.Connection;
import com.xensource.xenapi.Session;


/**
 * ��֤��¼��Ϣ�Ƿ���ȷ
 * @author Administrator
 *
 */
public class Authenticator {

	private static Connection conn = null;

	public static boolean validate(String ip, String user, String password) {
		try {
			conn = new Connection(new URL("http://" + ip));
			Session.loginWithPassword(conn, user, password);
			conn.dispose();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
