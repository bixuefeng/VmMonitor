package com.vm.node;

import com.vm.xen.XenTools;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VmOpera {

	public static Node getPanel(TreeItem<String> node) {
		// TODO Auto-generated method stub
		 VBox root = new VBox(10);
	        root.setPadding(new Insets(10, 10, 10, 10));
	        root.setMaxHeight(Double.MAX_VALUE);
	    

	        GridPane fontDemo = new GridPane();
	        fontDemo.setHgap(5);
	        fontDemo.setVgap(5);
	       
	        	Button button1 = new Button("开机",new ImageView(new Image(VmOpera.class.getClassLoader().getResourceAsStream("picture/start.png"))));
	        	button1.setContentDisplay(ContentDisplay.TOP);
	        	button1.setMaxWidth(Double.MAX_VALUE);
	        	
	        	
	        	
	        	Button button2 = new Button("关闭",new ImageView(new Image(VmOpera.class.getClassLoader().getResourceAsStream("picture/shut.png"))));
	        	button2.setContentDisplay(ContentDisplay.TOP);
	        	button2.setMaxWidth(Double.MAX_VALUE);
	        	
	        	
	        	
	        	Button button3 = new Button("迁移",new ImageView(new Image(VmOpera.class.getClassLoader().getResourceAsStream("picture/m.png"))));
	        	button3.setContentDisplay(ContentDisplay.TOP);
	        	button3.setMaxWidth(Double.MAX_VALUE);
	        	
	        	String vmName = node.getValue();
	        	button1.setOnAction((ActionEvent e)->{
	        		XenTools.startVmByName(vmName);
	        		Platform.runLater(new Runnable() {
	        			
	        			@Override
	        			public void run() {
	        				node.setGraphic(new ImageView(new Image(VmOpera.class.getClassLoader().getResourceAsStream("picture/vm2.png"))));
	                		
	        			}
	        		});
	        		
	        		
	        		
	        	});
	        	button2.setOnAction((ActionEvent e)->{
	        		XenTools.closeVmByName (vmName);
	        			Platform.runLater(new Runnable() {
	        			
	        			@Override
	        			public void run() {
	        				node.setGraphic(new ImageView(new Image(VmOpera.class.getClassLoader().getResourceAsStream("picture/vm1.png"))));
	                		
	        			}
	        		});
	        			
	        	});
	        	
	        	
	        	button3.setOnAction((ActionEvent e)->{
	        		//迁移指定虚拟机
	        		
	        		
	      
	        		 Stage stage = new Stage(); 
		    		 stage.setScene(new Scene(new Migration().getGridPane(node)));
		    		 stage.setTitle("迁移虚拟机"); 
		    		 stage.show();
	    	});
	        	
	        	
	        	fontDemo.add( button1,0,1);
	        	GridPane.setFillHeight(button1, true);
	        	GridPane.setFillWidth(button1, true);
	        	
	        	fontDemo.add( button2,1,1);
	        	GridPane.setFillHeight(button2, true);
	        	GridPane.setFillWidth(button2, true);
	        	
	        	
	        	fontDemo.add( button3,2,1);
	        	GridPane.setFillHeight(button3, true);
	        	GridPane.setFillWidth(button3, true);
	        	 root.getChildren().add(fontDemo);

	             //VBox.setVgrow(tabs, Priority.ALWAYS);

	             

	             return root;
	}

}
