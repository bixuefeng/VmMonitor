package com.vm.xen;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import com.xensource.xenapi.Connection;
import com.xensource.xenapi.Session;

public class ConnectionUtil {

	private static Connection conn = null;
	private static Properties prop = null;

	static {
		InputStream in = ConnectionUtil.class.getClassLoader().getResourceAsStream("Xen.properties");
		prop = new Properties();
		try {
			prop.load(in);
		} catch (Exception e) {
			System.err.println("º”‘ÿxen≈‰÷√Œƒº˛¥ÌŒÛ£°");
		}
	}

	public static Connection getConnection() throws Exception {
		conn = new Connection(new URL("http://" + prop.getProperty("hostName")));
		Session.loginWithPassword(conn, prop.getProperty("userName"), prop.getProperty("passWord"));
		return conn;
	}

	public static void release(Connection conn) {
		if (conn != null) {
			conn.dispose();
		}
	}

}
