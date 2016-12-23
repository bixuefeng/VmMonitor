package com.vm.node;

import com.vm.domain.ServerInfo;
import com.vm.xen.XenTools;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
public class XenNode {

	public static Node getXenNode(TreeItem<String> node)
	{
		String hostName = node.getValue();
		ServerInfo serverInfo = XenTools.getServerInfoByName(hostName);
		GridPane grid = new GridPane();
	    grid.setVgap(10);
	    grid.setHgap(10);
	    grid.setPadding(new Insets(30, 30, 0, 30));
	    grid.setMinSize(200, 200);
	    
	    
	    
	    Label name  = new Label("名称：");
	     Label name1  = new Label(serverInfo.getName());	     
	     grid.add(name, 0, 0);
	     grid.add(new Label(" "), 1, 0);
	     grid.add(name1, 2,0 );   
	     Label uuid = new Label("UUID：");
	     Label uuid1 = new Label(serverInfo.getUuid());
	     grid.add(uuid, 0, 1);
	     grid.add(new Label(" "), 1, 1);
	     grid.add(uuid1, 2,1 );    
	     Label des = new Label("ip地址：");
	     Label des1 = new Label(serverInfo.getIp());
	     grid.add(des, 0, 2);
	     grid.add(new Label(" "), 1, 2);
	     grid.add(des1, 2,2 );
	     Label wlbUrl = new Label("API供应商：");
	     Label wlbUrl1 = new Label(serverInfo.getAPIversion());
	     grid.add(wlbUrl, 0, 3);
	     grid.add(new Label(" "), 1, 3);
	     grid.add(wlbUrl1, 2,3 );  
	     Label wlbname = new Label("Edition：");
	     Label wlbname1 = new Label(serverInfo.getEdition());
	     grid.add(wlbname, 0, 4);
	     grid.add(new Label(" "), 1, 4);
	     grid.add(wlbname1, 2,4 );
	     Label wlbEnabled = new Label("启动模式：");
	     Label wlbEnabled1 = new Label(serverInfo.getPowerOnMode());
	     grid.add(wlbEnabled, 0, 5);
	     grid.add(new Label(" "), 1, 5);
	     grid.add(wlbEnabled1, 2,5 );  
	     Label version = new Label("CPU供应商：");
	     Label version1 = new Label(serverInfo.getCpu().get("vendor"));
	     grid.add(version, 0, 6);
	     grid.add(new Label(" "), 1, 6);
	     grid.add(version1, 2,6 );  
	     Label endline = new Label("CPU个数：");
	     Label endline1 = new Label(serverInfo.getCpu().get("cpu_count")+"个");
	     grid.add(endline, 0, 7);
	     grid.add(new Label(" "), 1, 7);
	     grid.add(endline1, 2,7 );
	     
	     
	     Label cpuType = new Label("CPU型号：");
	     Label cpuType1 = new Label(serverInfo.getCpu().get("modelname"));
	     grid.add(cpuType, 0, 8);
	     grid.add(new Label(" "), 1, 8);
	     grid.add(cpuType1, 2,8);
	     
	     
	     Label cpuSpeed = new Label("CPU速度：");
	     Label cpuSpeed1 = new Label( serverInfo.getCpu().get("speed")+"MHz");
	     grid.add(cpuSpeed, 0, 9);
	     grid.add(new Label(" "), 1, 9);
	     grid.add(cpuSpeed1, 2,9);
	     
	     
	     Label iqn = new Label("IscsiIQN：");
	     Label iqn1 = new Label(serverInfo.getOther().get("iscsi_iqn"));
	     grid.add(iqn, 0, 10);
	     grid.add(new Label(" "), 1, 10);
	     grid.add(iqn1, 2,10);
	     
	     
	     ObservableList<PieChart.Data> pieChartData =
	    		 FXCollections.observableArrayList(
	    		XenTools.getMemoryUsageByName(hostName));
	    PieChart chart = new PieChart(pieChartData);
	    chart.setTitle("XenServer内存使用状况");
	    		 
	    Button button =  new Button("刷新内存状态",new ImageView(new Image(XenNode.class.getClassLoader().getResourceAsStream("picture/flush.png"))));
	 	 button.setOnAction((ActionEvent e) -> {
	 		ObservableList<PieChart.Data> pieChartData1 =FXCollections.observableArrayList(XenTools.getMemoryUsageByName(hostName));
	 		 chart.setData(pieChartData1);
	 	        	
	 	    		});
	 	 
	 	 final Timeline timeline = new Timeline(
	 		      new KeyFrame(Duration.ZERO, new EventHandler() {
	 		        @Override public void handle(Event event) {
	 		        	ObservableList<PieChart.Data> pieChartData1 =FXCollections.observableArrayList(XenTools.getMemoryUsageByName(hostName));
	 			 		 chart.setData(pieChartData1);
	 		        }
	 		      }),  
	 		      new KeyFrame(Duration.millis(5000))
	 		    );
	 timeline.setCycleCount(Timeline.INDEFINITE);
	 	timeline.play();
	 	
	 	Label showDecorationsLabel = new Label("XenServer状态信息:");
	    showDecorationsLabel.setStyle("-fx-font-size: 1.25em; -fx-padding: 0 0 0 5;");
	    VBox v = new VBox(5, grid, new Separator(),showDecorationsLabel,chart,button);
	  
	 	 return v;
	}
}
