package com.vm.node;

import com.vm.domain.VmInfo;
import com.vm.xen.XenTools;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class VmGeneralNode {
	public static Node getGeneralNode(TreeItem<String> node) {
		String nameVm = node.getValue();
		VmInfo info = XenTools.getVmInfoByNameOrUuid(nameVm,null);

		GridPane gene = new GridPane();
		gene.setVgap(10);
		gene.setHgap(10);
		gene.setPadding(new Insets(20, 20, 20, 20));
		/* gene.setMinSize(200, 200); */
		gene.setStyle("-fx-background-color: white; -fx-border-color: white;");

		Label name = new Label("名称：");
		Label name1 = new Label(info.getName());
		gene.add(name, 0, 0);
		gene.add(new Label(" "), 1, 0);
		gene.add(name1, 2, 0);

		Label uuid = new Label("UUID：");
		Label uuid1 = new Label(info.getUuid());
		gene.add(uuid, 0, 1);
		gene.add(new Label(" "), 1, 1);
		gene.add(uuid1, 2, 1);

		Label os = new Label("操作系统：");
		Label os1 = new Label(info.getOs());
		gene.add(os, 0, 2);
		gene.add(new Label(" "), 1, 2);
		gene.add(os1, 2, 2);

		GridPane cpup = new GridPane();
		cpup.setVgap(10);
		cpup.setHgap(10);
		cpup.setPadding(new Insets(20, 20, 20, 20));
		/* cpup.setMinSize(200, 200); */
		cpup.setStyle("-fx-background-color: white; -fx-border-color: white;");

		String cpuNums = info.getCpu();
		Label cpu = new Label("CPU：");
		Label cpu1 = new Label(cpuNums + "个");
		cpup.add(cpu, 0, 0);
		cpup.add(new Label(" "), 1, 0);
		cpup.add(cpu1, 2, 0);

		GridPane memo = new GridPane();
		memo.setVgap(10);
		memo.setHgap(10);
		memo.setPadding(new Insets(20, 20, 20, 20));
		/* memo.setMinSize(200, 200); */
		memo.setStyle("-fx-background-color: white; -fx-border-color: white;");

		Label mem = new Label("内存大小：");
		Label mem1 = new Label(info.getMemory() + "G");
		memo.add(mem, 0, 0);
		memo.add(new Label(" "), 1, 0);
		memo.add(mem1, 2, 0);

		Label ip = new Label("ip地址：");
		Label ip1 = new Label(info.getVmIp());
		memo.add(ip, 0, 1);
		memo.add(new Label(" "), 1, 1);
		memo.add(ip1, 2, 1);

		GridPane oth = new GridPane();
		oth.setVgap(10);
		oth.setHgap(10);
		oth.setPadding(new Insets(20, 20, 20, 20));
		/* oth.setMinSize(200, 200); */
		oth.setStyle("-fx-background-color: white; -fx-border-color: white;");

		Label last = new Label("上次启动时间：");
		Label last1 = new Label(info.getLastTime());
		oth.add(last, 0, 0);
		oth.add(new Label(" "), 1, 0);
		oth.add(last1, 2, 0);

		Label stat = new Label("状态：");
		Label stat1 = new Label(info.getState());
		oth.add(stat, 0, 1);
		oth.add(new Label(" "), 1, 1);
		oth.add(stat1, 2, 1);

		TitledPane t1 = new TitledPane("常规信息", gene);

		TitledPane t2 = new TitledPane("CPU", cpup);

		TitledPane t3 = new TitledPane("内存和IP", memo);

		TitledPane t4 = new TitledPane("其他信息", oth);

		return new VBox(5, new Separator(), t1, t2, t3, t4, new Separator());
	}
}
