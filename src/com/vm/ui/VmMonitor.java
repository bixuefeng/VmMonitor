package com.vm.ui;


import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vm.node.ControlNode;
import com.vm.node.FirstNode;
import com.vm.node.PoolNode;
import com.vm.node.SrGeneralNode;
import com.vm.node.VmGeneralNode;
import com.vm.node.VmOpera;
import com.vm.node.VmPerform;
import com.vm.node.XenNode;
import com.vm.service.XenService;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * 主界面
 * @author Administrator
 *
 */
@Component
public class VmMonitor {

	@Autowired
	private XenService xenService;
	
	private GridPane grid;
	public  TreeView<String> samplesTreeView;
	private  TabPane tabPane;
	private  Tab first; //欢迎界面
	private  Tab poolTab;//池基本信息
	private  Tab xenTab;//主机基本信息
	private  Tab generalTab;//存储库或者虚拟机基本信息
	private  Tab performTab;//性能
	private  Tab operateTab;//操作
	
    public  TreeItem<String> root;
	
	public GridPane getPane() {
		// TODO Auto-generated method stub
		grid = new GridPane();
		grid.setPadding(new Insets(5, 10, 10, 10));
		final TextField searchBox = new TextField();
		searchBox.setPromptText("查找虚拟机");
		searchBox.getStyleClass().add("search-box");
		GridPane.setMargin(searchBox, new Insets(5, 10, 0, 2));
		grid.add(searchBox, 0, 0);

		root = buildSampleTree();
		samplesTreeView = new TreeView<>(root);
		samplesTreeView.getStyleClass().add("samples-tree");
		samplesTreeView.setMinWidth(200);
		samplesTreeView.setMaxWidth(300);
		// 设置树的监听事件
		samplesTreeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {
			@Override
			public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue,
					TreeItem<String> newSample) {
				if (newSample == null) {
					return;
				}
				changeSample(newSample);
			}
		});
		GridPane.setVgrow(samplesTreeView, Priority.ALWAYS);
		GridPane.setMargin(samplesTreeView, new Insets(2, 10, 2, 0));
		grid.add(samplesTreeView, 0, 1);
		// 创建pane
		tabPane = new TabPane();
		tabPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		//加载登录之后欢迎界面
		first = new Tab("服务中心综合管理系统");
		first.setContent(FirstNode.getNode());
		tabPane.getTabs().add(first);
		poolTab = new Tab("池信息");
		xenTab = new Tab("主机基本信息");
		generalTab = new Tab("常规");
		performTab = new Tab("性能");
		operateTab = new Tab("操作");
		GridPane.setMargin(tabPane, new Insets(5, 0, 0, 2));
		grid.add(tabPane, 1, 0, 1, 2);
		Node node = ControlNode.getNode();
		GridPane.setMargin(node, new Insets(5, 0, 0, 10));
		grid.add(node, 2, 0, 1, 2);
		samplesTreeView.requestFocus();
		return grid;
	}

	
	/**
	 * 控制节点单击事件,如果点击的节点为池，则显示池信息。如果是“虚拟机”或者“存储库”则显示欢迎界面，如果为某一个虚拟机。。。。
	 * @param node
	 */
	public void changeSample(TreeItem<String> node){
		if (node.getValue() == null) {
			return;
		}
		if (node.getValue().contains(xenService.getPoolName())) {
			tabPane.getTabs().clear();
			poolTab.setContent(PoolNode.getPoolNode());
			tabPane.getTabs().add(poolTab);
			return;
		} else if (node.getValue().equals("虚拟机") || node.getValue().equals("存储库") || node.getValue().equals("共享存储")) {
			tabPane.getTabs().clear();
			tabPane.getTabs().add(first);
			return;
		} else if (node.getParent().getValue().contains(xenService.getPoolName()) && !node.getValue().contains("共享")) {
			// 服务器各个主机
			tabPane.getTabs().clear();
			xenTab.setContent(XenNode.getXenNode(node));
			tabPane.getTabs().add(xenTab);
		} else if (node.getParent().getValue().contains("共享")) {
			tabPane.getTabs().clear();
			// 共享存储库
			tabPane.getTabs().add(generalTab);
			generalTab.setContent(SrGeneralNode.getGeneralNode(node));
		} else {
			if (node.getParent().getValue().equals("虚拟机")) {
				tabPane.getTabs().clear();
				tabPane.getTabs().add(generalTab);
				tabPane.getTabs().add(operateTab);
				tabPane.getTabs().add(performTab);
				generalTab.setContent(VmGeneralNode.getGeneralNode(node));
				operateTab.setContent(VmOpera.getPanel(node));
				performTab.setContent(VmPerform.getNode(node));
			} else {
				//TODO 如果选择的是某一个服务器中的存储库，则应该显示该服务器下的sr内容，目前忽略。
			}
		}

	}
	
	/**
	 * 创建树的所有节点
	 * @return 树节点
	 */
	public TreeItem<String> buildSampleTree() {     	
		Node rootIcon = new ImageView(new Image(VmMonitor.class.getClassLoader().getResourceAsStream("picture/pool.png")));
		root = new TreeItem<String>(xenService.getPoolName(),rootIcon);
        root.setExpanded(true);    
        // 创建所有服务器节点和各个服务器中的VM与SR   
        Set<String> hostNames = xenService.getHostsNames();
        if(CollectionUtils.isEmpty(hostNames)){
        	return root;
        }
        //添加各个服务器节点
		for (String hostName : hostNames) {
			TreeItem<String> serverNode = new TreeItem<>(hostName,
					new ImageView(new Image(VmMonitor.class.getClassLoader().getResourceAsStream("picture/home.png"))));
			root.getChildren().add(serverNode);
			// 添加此服务器上面的虚拟机和SR存储库
			TreeItem<String> vmNode = new TreeItem<>("虚拟机",
					new ImageView(new Image(VmMonitor.class.getClassLoader().getResourceAsStream("picture/vm.png"))));
			TreeItem<String> srNode = new TreeItem<>("存储库",
					new ImageView(new Image(VmMonitor.class.getClassLoader().getResourceAsStream("picture/sr.png"))));
			serverNode.getChildren().add(vmNode);
			serverNode.getChildren().add(srNode);
			// 添加所有虚拟机节点
			Set<String> vmsNames = xenService.getVmsNamesFromHost(hostName);
			if (CollectionUtils.isNotEmpty(vmsNames)) {
				for (String vmName : vmsNames) {
					TreeItem<String> vmLeaf = null;
					if (xenService.isRunningByName(vmName))
						vmLeaf = new TreeItem<>(vmName, new ImageView(
								new Image(VmMonitor.class.getClassLoader().getResourceAsStream("picture/vm2.png"))));
					else
						vmLeaf = new TreeItem<>(vmName, new ImageView(
								new Image(VmMonitor.class.getClassLoader().getResourceAsStream("picture/vm1.png"))));
					vmNode.getChildren().add(vmLeaf);
				}
			}
			// 添加所有SR节点
			Set<String> srsNames = xenService.getSrsNamesFromHost(hostName);
			if (CollectionUtils.isNotEmpty(srsNames)) {
				for (String srName : srsNames) {
					TreeItem<String> srLeaf = new TreeItem<>(srName, new ImageView(
							new Image(VmMonitor.class.getClassLoader().getResourceAsStream("picture/sr1.png"))));
					srNode.getChildren().add(srLeaf);
				}
			}
		}
        //创建共享存储库节点
        TreeItem<String> srShare = new TreeItem<>("共享存储",new ImageView(new Image(VmMonitor.class.getClassLoader().getResourceAsStream("picture/share.png"))));
        root.getChildren().add(srShare);
        Set<String> shareSrNames = xenService.getShareSrNames();
		if (CollectionUtils.isNotEmpty(shareSrNames)) {
			for (String srName : shareSrNames) {
				TreeItem<String> srLeaf = new TreeItem<>(srName, new ImageView(
						new Image(VmMonitor.class.getClassLoader().getResourceAsStream("picture/shareSR.png"))));
				srShare.getChildren().add(srLeaf);
			}
		}
        return root;
    }


	public XenService getXenService() {
		return xenService;
	}


	public void setXenService(XenService xenService) {
		this.xenService = xenService;
	}


	public GridPane getGrid() {
		return grid;
	}


	public void setGrid(GridPane grid) {
		this.grid = grid;
	}


	public TreeView<String> getSamplesTreeView() {
		return samplesTreeView;
	}


	public void setSamplesTreeView(TreeView<String> samplesTreeView) {
		this.samplesTreeView = samplesTreeView;
	}


	public TabPane getTabPane() {
		return tabPane;
	}


	public void setTabPane(TabPane tabPane) {
		this.tabPane = tabPane;
	}


	public Tab getFirst() {
		return first;
	}


	public void setFirst(Tab first) {
		this.first = first;
	}


	public Tab getPoolTab() {
		return poolTab;
	}


	public void setPoolTab(Tab poolTab) {
		this.poolTab = poolTab;
	}


	public Tab getXenTab() {
		return xenTab;
	}


	public void setXenTab(Tab xenTab) {
		this.xenTab = xenTab;
	}


	public Tab getGeneralTab() {
		return generalTab;
	}


	public void setGeneralTab(Tab generalTab) {
		this.generalTab = generalTab;
	}


	public Tab getPerformTab() {
		return performTab;
	}


	public void setPerformTab(Tab performTab) {
		this.performTab = performTab;
	}


	public Tab getOperateTab() {
		return operateTab;
	}


	public void setOperateTab(Tab operateTab) {
		this.operateTab = operateTab;
	}


	public TreeItem<String> getRoot() {
		return root;
	}


	public void setRoot(TreeItem<String> root) {
		this.root = root;
	}
	
}
