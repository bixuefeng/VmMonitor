package com.vm.node;

import java.awt.Dimension;
import java.awt.Toolkit;

import com.vm.ui.CreateVm;

import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ControlNode {

	public static Node getNode() {
		GridPane showPane = new GridPane();
		GridPane control = new GridPane();
		TabPane tabPane = new TabPane();
		Tab xenTab = new Tab("服务器内存状态");
		Tab sqlTab = new Tab("数据库");
		Tab infoTab = new Tab("连接日志");
		xenTab.setContent(XenStateNode.getNode());
		tabPane.getTabs().add(xenTab);
		control.setVgap(10);
		control.setHgap(10);
		control.setPadding(new Insets(5, 30, 0, 30));
		// control.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		Label showDecorationsLabel = new Label("XenServer控制");
		// showDecorationsLabel.setStyle("-fx-font-size: 1.25em; -fx-padding: 0
		// 0 0 5;");
		GridPane.setHalignment(showDecorationsLabel, HPos.CENTER);
		control.add(showDecorationsLabel, 0, 0, 3, 1);
		Button createVm = new Button("创建虚拟机",
				new ImageView(new Image(ControlNode.class.getClassLoader().getResourceAsStream("picture/create.png"))));
		createVm.setOnAction((ActionEvent e) -> {
			Toolkit kit = Toolkit.getDefaultToolkit();
			Dimension screeSize = kit.getScreenSize();
			CreateVm frame;
			try {
				frame = new CreateVm();
				frame.setVisible(true);
				frame.setSize(screeSize.width / 3, screeSize.height / 3);
				frame.setLocation(0, 0);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// 当关闭二级窗口时，一级窗口不关闭
			// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		});
		control.add(createVm, 0, 1);
		createVm.setMinSize(150, 40);
		Button blank1 = new Button("创建SR",
				new ImageView(new Image(ControlNode.class.getClassLoader().getResourceAsStream("picture/migration.png"))));
		Button blank2 = new Button("备份",
				new ImageView(new Image(ControlNode.class.getClassLoader().getResourceAsStream("picture/backup.png"))));
		blank1.setMinSize(150, 40);
		blank2.setMinSize(150, 40);
		blank1.setOnAction((ActionEvent e) -> {
			Stage stage = new Stage();
			stage.setScene(new Scene(CreateSR.getGridPane()));
			stage.setTitle("创建共享SR");
			stage.show();

		});
		control.add(blank1, 1, 1);
		control.add(blank2, 2, 1);
		GridPane listen = new GridPane();
		listen.setVgap(10);
		listen.setHgap(10);
		listen.setPadding(new Insets(5, 30, 0, 30));
		listen.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		Label sh = new Label("数据及日志");
		// sh.setStyle("-fx-font-size: 1.25em; -fx-padding: 0 0 0 5;");
		GridPane.setHalignment(sh, HPos.CENTER);
		listen.add(sh, 0, 0, 3, 1);
		Button showSql = new Button("查看数据库",
				new ImageView(new Image(ControlNode.class.getClassLoader().getResourceAsStream("picture/show.png"))));
		Button showData = new Button("查看连接日志",
				new ImageView(new Image(ControlNode.class.getClassLoader().getResourceAsStream("picture/diary.png"))));
		Button showState = new Button("查看内存",
				new ImageView(new Image(ControlNode.class.getClassLoader().getResourceAsStream("picture/state.png"))));
		listen.add(showState, 0, 1);
		listen.add(showSql, 1, 1);
		listen.add(showData, 2, 1);
		showSql.setOnAction((ActionEvent e) -> {
			showPane.getChildren().clear();
			showPane.add(ShowSql.getNode(), 0, 0);
		});
		showState.setOnAction((ActionEvent e) -> {
			showPane.getChildren().clear();
			showPane.add(XenStateNode.getNode(), 0, 0);
		});
		showData.setOnAction((ActionEvent e) -> {

			showPane.getChildren().clear();
			// TODO 显示连接日志，暂不开通。
		});
		showSql.setMinSize(150, 40);
		showData.setMinSize(150, 40);
		showState.setMinSize(150, 40);
		//showPane.add(XenStateNode.getNode(), 0, 0);
		VBox v = new VBox(5, control, new Separator(), listen, new Separator(), showPane);
		v.setMaxWidth(580);
		return v;
	}
}
