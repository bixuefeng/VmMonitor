package com.vm.node;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.vm.domain.PoolInfo;
import com.vm.xen.XenTools;
import com.xensource.xenapi.VM;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * 池基本信息节点类
 * @author Administrator
 *
 */
public class PoolNode {
	
	public static Node getPoolNode(){
		 GridPane grid = new GridPane();
	     grid.setVgap(10);
	     grid.setHgap(10);
	     grid.setPadding(new Insets(30, 30, 0, 30));
	     grid.setMinSize(200, 200);
	     PoolInfo poolInfo = XenTools.getPoolInfo();
	     if(poolInfo == null){
	    	 return null;
	     }
	     Label name  = new Label("名称：");
	     Label name1  = new Label(poolInfo.getName());	     
	     grid.add(name, 0, 0);
	     grid.add(new Label(" "), 1, 0);
	     grid.add(name1, 2,0 );   
	     Label uuid = new Label("UUID：");
	     Label uuid1 = new Label(poolInfo.getUuid());
	     grid.add(uuid, 0, 1);
	     grid.add(new Label(" "), 1, 1);
	     grid.add(uuid1, 2,1 );    
	     Label des = new Label("名字描述：");
	     Label des1 = new Label(poolInfo.getNameDescription().equals("")?"<空>":poolInfo.getNameDescription());
	     grid.add(des, 0, 2);
	     grid.add(new Label(" "), 1, 2);
	     grid.add(des1, 2,2 );
	     Label wlbUrl = new Label("URL：");
	     Label wlbUrl1 = new Label(poolInfo.getWlbUrl().equals("")?"<空>":poolInfo.getWlbUrl());
	     grid.add(wlbUrl, 0, 3);
	     grid.add(new Label(" "), 1, 3);
	     grid.add(wlbUrl1, 2,3 );  
	     Label wlbname = new Label("wlbUsername：");
	     Label wlbname1 = new Label(poolInfo.getWlbUsername().equals("")?"<空>":poolInfo.getWlbUsername());
	     grid.add(wlbname, 0, 4);
	     grid.add(new Label(" "), 1, 4);
	     grid.add(wlbname1, 2,4 );
	     Label wlbEnabled = new Label("wlbEnabled：");
	     Label wlbEnabled1 = new Label(poolInfo.isWlbEnabled()?"true":"false");
	     grid.add(wlbEnabled, 0, 5);
	     grid.add(new Label(" "), 1, 5);
	     grid.add(wlbEnabled1, 2,5 );  
	     Label version = new Label("当前版本：");
	     Label version1 = new Label(poolInfo.getLicenseState().get("edition"));
	     grid.add(version, 0, 6);
	     grid.add(new Label(" "), 1, 6);
	     grid.add(version1, 2,6 );  
	     Label endline = new Label("证书截止日期：");
	     Label endline1 = new Label(poolInfo.getLicenseState().get("expiry"));
	     grid.add(endline, 0, 7);
	     grid.add(new Label(" "), 1, 7);
	     grid.add(endline1, 2,7 );    
	     grid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);     
	     return new VBox(5,grid, new Separator() ,getBarChart());
	}
	private static BarChart getBarChart()
	{
		Set<String> nameOfServers = XenTools.getHostsName();
		CategoryAxis yAxis = new CategoryAxis();
		NumberAxis xAxis = new NumberAxis();
		xAxis.setLowerBound(0);
		xAxis.setUpperBound(40000);
		xAxis.setTickUnit(1024);
		BarChart<Number,String> bc = new BarChart<Number,String>(xAxis,yAxis);
		bc.setMinSize(400, 400);
		bc.setTitle("池内存使用");
		yAxis.setLabel("服务器");
        yAxis.setCategories(FXCollections.<String>observableArrayList(nameOfServers));
        xAxis.setLabel("内存大小(M)");
        
        Set<XYChart.Series<Number,String>> set = new HashSet<XYChart.Series<Number,String>>();
        Map<String,Set<VM>> map = XenTools.getVmsRunOnHost();
        for(Map.Entry<String, Set<VM>> entry : map.entrySet())
        {
        	for(VM vm : entry.getValue())
        	{
        		XYChart.Series<Number,String> series = new XYChart.Series<Number,String>();
        		series.setName(XenTools.getNameOfVM(vm));
        		series.getData().add(new XYChart.Data<Number,String>(XenTools.getMemoryOfVM(vm,null), entry.getKey()));
        		set.add(series);
        	}
        	
        }
        for(XYChart.Series<Number,String> series : set)
        bc.getData().add(series);
        
        
        final Timeline timeline = new Timeline(
	 		      new KeyFrame(Duration.ZERO, new EventHandler() {
	 		        @Override public void handle(Event event) {
	 		        	 set.clear();
	 		        	 Map<String,Set<VM>> map = XenTools.getVmsRunOnHost();
	 		            for(Map.Entry<String, Set<VM>> entry : map.entrySet())
	 		            {
	 		            	for(VM vm : entry.getValue())
	 		            	{
	 		            		XYChart.Series<Number,String> series = new XYChart.Series<Number,String>();
	 		            		series.setName(XenTools.getNameOfVM(vm));
	 		            		series.getData().add(new XYChart.Data<Number,String>(XenTools.getMemoryOfVM(vm,null), entry.getKey()));
	 		            		set.add(series);
	 		            	}
	 		            	
	 		            }
	 		           bc.getData().clear();
	 		            for(XYChart.Series<Number,String> series : set)
	 		            {
	 		            	
	 		            	bc.getData().add(series);
	 		            }
	 		          
	 		        }
	 		      }),  
	 		      new KeyFrame(Duration.millis(20000))
	 		    );
        timeline.setCycleCount(Timeline.INDEFINITE);
	 	timeline.play();
       return bc;
	}
}
