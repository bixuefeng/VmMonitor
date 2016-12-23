package com.vm.node;

import java.util.ArrayList;

import com.vm.domain.Vdi;
import com.vm.xen.XenTools;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class SrGeneralNode {

	public static Node getGeneralNode(TreeItem<String> node) {
		ObservableList<Vdi> data = null;
		data = FXCollections.observableArrayList(XenTools.getVDIsbySrName(node.getValue()));
		TableView table = new TableView();

		table.setMinSize(600, 600);
		TableColumn nameC = new TableColumn("����");
		TableColumn desC = new TableColumn("˵��");
		TableColumn stC = new TableColumn("����");
		TableColumn vmC = new TableColumn("���������");

		nameC.setMinWidth(200);
		nameC.setCellValueFactory(new PropertyValueFactory<>("name"));
		desC.setMinWidth(200);
		desC.setCellValueFactory(new PropertyValueFactory<>("des"));
		stC.setMinWidth(200);
		stC.setCellValueFactory(new PropertyValueFactory<>("size"));

		vmC.setMinWidth(200);
		vmC.setCellValueFactory(new PropertyValueFactory<>("vm"));

		table.setItems(data);
		table.getColumns().add(nameC);
		table.getColumns().add(desC);
		table.getColumns().add(stC);
		table.getColumns().add(vmC);

		VBox vbox = new VBox();
		vbox.getChildren().addAll(table);
		return vbox;
	}
}