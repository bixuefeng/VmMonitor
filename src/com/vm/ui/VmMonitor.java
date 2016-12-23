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
 * ������
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
	private  Tab first; //��ӭ����
	private  Tab poolTab;//�ػ�����Ϣ
	private  Tab xenTab;//����������Ϣ
	private  Tab generalTab;//�洢����������������Ϣ
	private  Tab performTab;//����
	private  Tab operateTab;//����
	
    public  TreeItem<String> root;
	
	public GridPane getPane() {
		// TODO Auto-generated method stub
		grid = new GridPane();
		grid.setPadding(new Insets(5, 10, 10, 10));
		final TextField searchBox = new TextField();
		searchBox.setPromptText("���������");
		searchBox.getStyleClass().add("search-box");
		GridPane.setMargin(searchBox, new Insets(5, 10, 0, 2));
		grid.add(searchBox, 0, 0);

		root = buildSampleTree();
		samplesTreeView = new TreeView<>(root);
		samplesTreeView.getStyleClass().add("samples-tree");
		samplesTreeView.setMinWidth(200);
		samplesTreeView.setMaxWidth(300);
		// �������ļ����¼�
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
		// ����pane
		tabPane = new TabPane();
		tabPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		//���ص�¼֮��ӭ����
		first = new Tab("���������ۺϹ���ϵͳ");
		first.setContent(FirstNode.getNode());
		tabPane.getTabs().add(first);
		poolTab = new Tab("����Ϣ");
		xenTab = new Tab("����������Ϣ");
		generalTab = new Tab("����");
		performTab = new Tab("����");
		operateTab = new Tab("����");
		GridPane.setMargin(tabPane, new Insets(5, 0, 0, 2));
		grid.add(tabPane, 1, 0, 1, 2);
		Node node = ControlNode.getNode();
		GridPane.setMargin(node, new Insets(5, 0, 0, 10));
		grid.add(node, 2, 0, 1, 2);
		samplesTreeView.requestFocus();
		return grid;
	}

	
	/**
	 * ���ƽڵ㵥���¼�,�������Ľڵ�Ϊ�أ�����ʾ����Ϣ������ǡ�����������ߡ��洢�⡱����ʾ��ӭ���棬���Ϊĳһ���������������
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
		} else if (node.getValue().equals("�����") || node.getValue().equals("�洢��") || node.getValue().equals("����洢")) {
			tabPane.getTabs().clear();
			tabPane.getTabs().add(first);
			return;
		} else if (node.getParent().getValue().contains(xenService.getPoolName()) && !node.getValue().contains("����")) {
			// ��������������
			tabPane.getTabs().clear();
			xenTab.setContent(XenNode.getXenNode(node));
			tabPane.getTabs().add(xenTab);
		} else if (node.getParent().getValue().contains("����")) {
			tabPane.getTabs().clear();
			// ����洢��
			tabPane.getTabs().add(generalTab);
			generalTab.setContent(SrGeneralNode.getGeneralNode(node));
		} else {
			if (node.getParent().getValue().equals("�����")) {
				tabPane.getTabs().clear();
				tabPane.getTabs().add(generalTab);
				tabPane.getTabs().add(operateTab);
				tabPane.getTabs().add(performTab);
				generalTab.setContent(VmGeneralNode.getGeneralNode(node));
				operateTab.setContent(VmOpera.getPanel(node));
				performTab.setContent(VmPerform.getNode(node));
			} else {
				//TODO ���ѡ�����ĳһ���������еĴ洢�⣬��Ӧ����ʾ�÷������µ�sr���ݣ�Ŀǰ���ԡ�
			}
		}

	}
	
	/**
	 * �����������нڵ�
	 * @return ���ڵ�
	 */
	public TreeItem<String> buildSampleTree() {     	
		Node rootIcon = new ImageView(new Image(VmMonitor.class.getClassLoader().getResourceAsStream("picture/pool.png")));
		root = new TreeItem<String>(xenService.getPoolName(),rootIcon);
        root.setExpanded(true);    
        // �������з������ڵ�͸����������е�VM��SR   
        Set<String> hostNames = xenService.getHostsNames();
        if(CollectionUtils.isEmpty(hostNames)){
        	return root;
        }
        //��Ӹ����������ڵ�
		for (String hostName : hostNames) {
			TreeItem<String> serverNode = new TreeItem<>(hostName,
					new ImageView(new Image(VmMonitor.class.getClassLoader().getResourceAsStream("picture/home.png"))));
			root.getChildren().add(serverNode);
			// ��Ӵ˷�����������������SR�洢��
			TreeItem<String> vmNode = new TreeItem<>("�����",
					new ImageView(new Image(VmMonitor.class.getClassLoader().getResourceAsStream("picture/vm.png"))));
			TreeItem<String> srNode = new TreeItem<>("�洢��",
					new ImageView(new Image(VmMonitor.class.getClassLoader().getResourceAsStream("picture/sr.png"))));
			serverNode.getChildren().add(vmNode);
			serverNode.getChildren().add(srNode);
			// �������������ڵ�
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
			// �������SR�ڵ�
			Set<String> srsNames = xenService.getSrsNamesFromHost(hostName);
			if (CollectionUtils.isNotEmpty(srsNames)) {
				for (String srName : srsNames) {
					TreeItem<String> srLeaf = new TreeItem<>(srName, new ImageView(
							new Image(VmMonitor.class.getClassLoader().getResourceAsStream("picture/sr1.png"))));
					srNode.getChildren().add(srLeaf);
				}
			}
		}
        //��������洢��ڵ�
        TreeItem<String> srShare = new TreeItem<>("����洢",new ImageView(new Image(VmMonitor.class.getClassLoader().getResourceAsStream("picture/share.png"))));
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
