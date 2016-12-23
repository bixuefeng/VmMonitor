package com.vm.ui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Set;

import javax.management.monitor.Monitor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vm.service.XenService;
import com.vm.xen.XenTools;

import javafx.application.Platform;


public class CreateVm extends JFrame{

	private JComboBox<String> srCombo;
	private JComboBox<String> isoCombo;
	private JTextField vmName;
	private JTextField srName;
	private JTextField target;
	private JComboBox<String> targetIQN;
	private JComboBox<String> SCSIid;
	
	public CreateVm() throws Exception {
		super("����Ӳ�̴��������");
		initConpoent();
	}
	
	private void initConpoent() 
	{
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JLabel labelForVm = new JLabel("���������:");
		JLabel labelForSR = new JLabel("SR����:");
		JLabel labelForIp = new JLabel("IP��ַ:");
		JLabel labelForIQN = new JLabel("IQN");
		JLabel labelForID = new JLabel("iscsiID");
		JLabel labelForISO = new JLabel("ISO�ļ�:");
		vmName = new JTextField(20);
		srName = new JTextField(20); 
		target = new JTextField(20);
		
		
		JButton prob = new JButton("ɨ��Ŀ������");
		JButton create = new JButton("����");
		JButton cancle = new JButton("ȡ��");
		
		srCombo = new JComboBox<String>();
		isoCombo = new JComboBox<String>();
		InitSrCombo();//��ʼ�����е�SR
		targetIQN = new JComboBox<String>();
		SCSIid = new JComboBox<String>();
		
		JPanel contentpane = new JPanel();
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		JPanel panel3 = new JPanel();
		panel1.setLayout(new GridLayout(3,2));
		panel2.setLayout(new GridLayout(2,2));
		panel3.setLayout(new GridLayout(2,3));
		contentpane.setLayout(new GridLayout(3,1));
		
		contentpane.add(panel1);
		contentpane.add(panel2);
		contentpane.add(panel3);
		panel1.add(labelForVm);
		panel1.add(vmName);
		panel1.add(labelForSR);
		panel1.add(srName);
		panel1.add(labelForIp);
		panel1.add(target);
		panel3.add(labelForISO);
		panel3.add(srCombo);
		panel3.add(isoCombo);
		
		panel2.add(labelForIQN);
		panel2.add(targetIQN);
		panel2.add(labelForID);
		panel2.add(SCSIid);
		panel3.add(prob);
		panel3.add(create);
		panel3.add(cancle);
		
		canButtonEvent ccB = new canButtonEvent();
		cancle.addActionListener(ccB);
		probButtonEvent pB = new probButtonEvent();
		prob.addActionListener(pB);
		
		createButtonEvent cB = new createButtonEvent();
		create.addActionListener(cB);
		
		this.getContentPane().add(contentpane);
	}
		
	private void InitSrCombo()
	{	
		Set<String> srNamesAndUuids = XenTools.getISOSrNameAndUUID();
		for(String srNameAndUuid : srNamesAndUuids){
			srCombo.addItem(srNameAndUuid);
		}
		srCombo.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0) {
				isoCombo.removeAll();
				String str = srCombo.getSelectedItem().toString();
				//������*�����֡�����ʹ��* ��Ҫת�� ʹ��\\
				Set<String> vdiNamesAndUuids = XenTools.getVDIFromSR(str.split(":")[1]);
				for(String vdiNameAndUuid : vdiNamesAndUuids){
					isoCombo.addItem(vdiNameAndUuid);
				}
			}	
		});
	}
	
	private class probButtonEvent implements ActionListener
	{

		public void actionPerformed(ActionEvent arg0) {
			SwingWorker work = new SwingWorker()
			{
				protected Object doInBackground() throws Exception {
					if(target.getText().equals(""))
					{
						JOptionPane.showMessageDialog(null, "Ŀ���ַ����Ϊ��", "Ŀ���ַ����Ϊ��", JOptionPane.ERROR_MESSAGE); 
					}
					else
					{
						String inputValue = target.getText();
						if(inputValue.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")){  
				            String s[] = inputValue.split("\\.");  
				            if(Integer.parseInt(s[0])<255 && Integer.parseInt(s[1])<255 && Integer.parseInt(s[2])<255 && Integer.parseInt(s[3])<255)  
				            {
				            	//����SR
				            	Map<String,String> msg = XenTools.probIscsiSR(inputValue);
				            	targetIQN.addItem(msg.get("iqn"));
				            	SCSIid.addItem(msg.get("id"));
				            }
				            else
								JOptionPane.showMessageDialog(null, "�Ƿ�IP��ַ", "����", JOptionPane.ERROR_MESSAGE);
				        }
					  else
						JOptionPane.showMessageDialog(null, "�Ƿ�IP��ַ", "����", JOptionPane.ERROR_MESSAGE);
					}
					return null;
				}
			};
			work.execute();
		}	
	}	
	
		
	private class createButtonEvent implements ActionListener
	{
		public void actionPerformed(ActionEvent e) {
			SwingWorker work = new SwingWorker()
			{
				protected Object doInBackground() throws Exception {
					String nameForVM = vmName.getText();
					String nameForSR = srName.getText();
					String ipForTarget = target.getText();
					String nameForIQN = (String) targetIQN.getSelectedItem();
					String nameForID = (String) SCSIid.getSelectedItem();
					if(nameForVM.equals("") || nameForSR.equals("") || ipForTarget.equals("") || nameForIQN.equals("") || nameForID.equals(""))
						JOptionPane.showMessageDialog(null, "����Ϊ��","����", JOptionPane.ERROR_MESSAGE); 
					else
					{
						/*String isoName = (String) isoCombo.getSelectedItem();
						String isoUuid = isoName.split(":")[1];
						//XenTools.createVM(nameForVM,nameForSR,ipForTarget,nameForIQN,nameForID,isoUuid);
						JOptionPane.showMessageDialog(null, "����ʱ�����������ȷ�����ȴ�...","��ʾ", JOptionPane.INFORMATION_MESSAGE); 
						XenTools.testRegister(nameForVM, nameForSR, ipForTarget, nameForIQN, nameForID);
						Platform.runLater(new Runnable() {
							
							@Override
							public void run() {
								Monitor.root = Monitor.buildSampleTree();
								Monitor.samplesTreeView.setRoot(Monitor.root);
								//System.out.println("aaa");
							}
						});*/
						//TODO �����µ���������ù�����ʱ����ͨ��ֻ�Ǽ���ʾ
						JOptionPane.showMessageDialog(null, "�����ɹ�","��ʾ", JOptionPane.INFORMATION_MESSAGE); 
					}
					return null;
				}		
			};
			work.execute();
		}
	}
	
	private class canButtonEvent implements ActionListener
	{
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			vmName.setText(null);
			srName.setText(null);
			target.setText(null);
		}
	}
}
