package com.vm.ui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.vm.thread.AcceptCustomer;
import com.vm.thread.BalanceThred;
import com.vm.thread.ResourceDetectThread;
import com.vm.xen.Authenticator;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * 登录界面
 * @author Administrator
 *
 */
public class Login extends Application{

	private Stage stage;
	private static Stage stage1;
	private final double MINIMUM_WINDOW_WIDTH = 390.0;
	private final double MINIMUM_WINDOW_HEIGHT = 500.0;
    private static ApplicationContext applicationContext;
	
	public static void main(String[] args) {
		

		initSpring();
		acceptCustomer();
		startResourceDetectThread();
		startBalanceThread();
		if (SystemTray.isSupported()) {
			String title = "服务中心综合管理系统";
			SystemTray systemTray = SystemTray.getSystemTray();
			Image image = Toolkit.getDefaultToolkit().getImage(Login.class.getClassLoader().getResource("picture/sysTray.png"));
			TrayIcon trayicon = new TrayIcon(image, title, createMenu());
			try {
				systemTray.add(trayicon);
				trayicon.displayMessage(title, "", MessageType.INFO);
			} catch (AWTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		launch(args);
	}

	/**
	 * 开启资源探测线程
	 */
	private static void startResourceDetectThread(){
		ResourceDetectThread resourceDetectThread = (ResourceDetectThread) applicationContext.getBean("resourceDetectThread");
		resourceDetectThread.start();
	}
	
	/**
	 * 开启负载平衡线程
	 */
	private static void startBalanceThread(){
		BalanceThred balanceThred = (BalanceThred) applicationContext.getBean("balanceThred");
		balanceThred.start();
	}
	/**
	 * 开启一个线程，监听用户请求
	 */
	private static void acceptCustomer(){
		AcceptCustomer acceptCustomer = (AcceptCustomer) applicationContext.getBean("acceptCustomer");
		acceptCustomer.start();
	}
	
	/**
	 * 加载spring
	 */
	private static void initSpring(){
		applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		
	}
	
	private static PopupMenu createMenu() {
		PopupMenu menu = new PopupMenu();
		MenuItem exit = new MenuItem("关闭");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ex) {
				System.exit(0);
			}
		});
		MenuItem open = new MenuItem("打开");
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ex) {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (!stage1.isShowing())
							stage1.show();
						stage1.toFront();
						;
					}
				});

			}
		});
		menu.add(open);
		menu.addSeparator();
		menu.add(exit);
		return menu;
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			stage = primaryStage;
			stage.setTitle("服务中心综合管理系统");
			stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
			stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
			gotoLogin();
			primaryStage.show();
		} catch (Exception ex) {
			throw new RuntimeException();
		}
	}

	private void gotoLogin() {
		try {
			LoginController login = (LoginController) replaceSceneContent("Login.fxml");
			login.setApp(this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public boolean userLogging(String ip, String userId, String password) {
		if (Authenticator.validate(ip, userId, password)) {
			gotoProfile();
			return true;
		} else {
			return false;
		}
	}

	private void gotoProfile() {

		stage1 = new Stage();
		Scene scene = new Scene(((VmMonitor)applicationContext.getBean("vmMonitor")).getPane());
		//Scene scene = new Scene(new VmMonitor().getPane());

		//scene.getStylesheets().add(getClass().getClassLoader().getResource("fxsampler.css").toExternalForm());
		stage1.setScene(scene);
		stage1.setMinWidth(1000);
		stage1.setMinHeight(600);
		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		stage1.setWidth(screenBounds.getWidth() * 0.75);
		stage1.setHeight(screenBounds.getHeight() * .75);
		stage1.setTitle("服务中心综合管理系统");
		stage1.setMaximized(true);
		stage1.show();
		stage.close();

	}

	private Initializable replaceSceneContent(String fxml) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		InputStream in = Login.class.getResourceAsStream(fxml);
		loader.setBuilderFactory(new JavaFXBuilderFactory());
		loader.setLocation(Login.class.getResource(fxml));
		AnchorPane page;
		try {
			page = (AnchorPane) loader.load(in);
		} finally {
			in.close();
		}
		Scene scene = new Scene(page, 800, 600);
		stage.setScene(scene);
		stage.sizeToScene();
		return (Initializable) loader.getController();
	}
}
