package com.vm.node;


import java.util.Map;

import com.vm.xen.XenTools;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
public class CreateSR {

	public static GridPane getGridPane(){
		// TODO Auto-generated method stub
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(5);
		grid.setHgap(5);
		final TextField sr = new TextField();
		sr.setPromptText("SR名称");
		GridPane.setConstraints(sr, 0, 0);
		grid.getChildren().add(sr);
		//Defining the Name text field
		final TextField name = new TextField();
		name.setPromptText("ip地址");
		GridPane.setConstraints(name, 0, 1);
		grid.getChildren().add(name);
		//Defining the Last Name text field
		final TextField IQN = new TextField();
		IQN.setPromptText("IQN码");
		IQN.setEditable(false);
		GridPane.setConstraints(IQN, 0, 2);
		grid.getChildren().add(IQN);
		
		final TextField iscsiID = new TextField();
		iscsiID.setPromptText("iscsiID码");
		iscsiID.setEditable(false);
		GridPane.setConstraints(iscsiID, 0, 3);
		grid.getChildren().add(iscsiID);
		
		
		
		//Defining the Submit button
		Button test = new Button("探测");
		GridPane.setConstraints(test, 1, 0);
		grid.getChildren().add(test);
		
		
		Button submit = new Button("创建");
		GridPane.setConstraints(submit, 1, 1);
		grid.getChildren().add(submit);
		//Defining the Clear button
		Button clear = new Button("取消");
		GridPane.setConstraints(clear, 1, 2);
		grid.getChildren().add(clear);

		final Label label = new Label();
		GridPane.setConstraints(label, 0, 4);
		GridPane.setColumnSpan(label, 2);
		grid.getChildren().add(label);
		
		
		test.setOnAction((ActionEvent e) -> {
			label.setText("");
			if (name.getText() == null || "".equals(name.getText().trim())) 
			{
				label.setText("各项不能为空");
			} 
			else
			{
				String target = name.getText();
				Map<String,String> info = XenTools.probIscsiSR(target);
				if(info == null)
				{
					label.setText("探测目标失败");
				}
				else
				{
					IQN.setText(info.get("iqn"));
					iscsiID.setText(info.get("id"));
				}
		
			}
			 
			});
		
		
		submit.setOnAction((ActionEvent e) -> {
			label.setText("");
		if (name.getText() == null || "".equals(name.getText().trim())) 
		{
			label.setText("各项不能为空");
		} 
		else
		{
			String srName = sr.getText();
			String target = name.getText();
			
			String iqn = IQN.getText();
			String scsiId = iscsiID.getText();
			XenTools.createShareSR(srName, target, iqn, scsiId);
			//TODO 回显界面的变动。暂时不开通
			label.setText("创建成功");
		}
		 
		});
		
		clear.setOnAction((ActionEvent e) -> {
			label.setText("");
		name.clear();
		
		});
		return grid;
	}
}
