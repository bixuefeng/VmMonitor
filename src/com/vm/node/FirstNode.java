package com.vm.node;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * 用于显示登录之后欢迎界面
 * 
 * @author Administrator
 *
 */
public class FirstNode {
	public static VBox getNode() {
		Label welcomeLabel = new Label(null,
				new ImageView(new Image(FirstNode.class.getClassLoader().getResourceAsStream("picture/first.png"))));
		return new VBox(5, welcomeLabel);
	}
}
