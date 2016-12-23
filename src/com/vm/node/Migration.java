package com.vm.node;

import java.util.HashSet;
import java.util.Set;

import javax.management.monitor.Monitor;

import com.vm.ui.VmMonitor;
import com.vm.xen.XenTools;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


public class Migration {

	
	private VmMonitor vmMonitor;

	public GridPane getGridPane(TreeItem<String> node) {

		String name = node.getValue();
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(5);
		grid.setHgap(5);

		final Label master = new Label("主服务器：");
		grid.add(master, 0, 0);

		String hostName = XenTools.getAffiy(name);
		final Label master1 = new Label(hostName);
		grid.add(master1, 1, 0);

		final Label isMigr = new Label("是否可迁移：");
		grid.add(isMigr, 0, 1);

		final Label isMigr1 = new Label(XenTools.isMigration(name) ? "是" : "否");
		grid.add(isMigr1, 1, 1);

		String rig = null;
		if (!XenTools.isRunningByName(name))
			isMigr1.setText("否");
		else {
			rig = XenTools.getRegister(name);
		}

		final Label target = new Label("迁移主机：");
		grid.add(target, 0, 2);

		Set<String> hostNames = XenTools.getHostsName();
		Set<String> newHostNames = new HashSet<String>();
		for (String str : hostNames) {
			if (!str.equals(rig))
				newHostNames.add(str);
		}
		ObservableList<String> options = FXCollections.observableArrayList(newHostNames);
		final ComboBox comboBox = new ComboBox(options);

		GridPane.setConstraints(comboBox, 1, 2);
		grid.getChildren().add(comboBox);

		// Defining the Submit button
		Button submit = new Button("迁移");
		grid.add(submit, 0, 3);
		Button clear = new Button("取消");
		grid.add(clear, 1, 3);

		final Label label = new Label();
		grid.add(label, 0, 4, 2, 1);

		if (isMigr1.getText().equals("否")) {
			submit.setDisable(true);
			clear.setDisable(true);
			label.setText("此虚拟机不能迁移");
		}

		submit.setOnAction((ActionEvent e) -> {

			try {
				String targetName = comboBox.getValue().toString();
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						node.setGraphic(new ImageView(
								new Image(Migration.class.getClassLoader().getResourceAsStream("picture/vm3.png"))));
					}
				});
				XenTools.migration(targetName, name);

				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						//vmMonitor.buildSampleTree();
						System.out.println("重构虚拟机树成功");
					}
				});
				label.setText("迁移成功");
			} catch (Exception e1) {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						node.setGraphic(new ImageView(
								new Image(Migration.class.getClassLoader().getResourceAsStream("picture/vm1.png"))));
					}
				});
				label.setText("迁移失败");
			}

		});

		clear.setOnAction((ActionEvent e) -> {

		});
		return grid;
	}
}
