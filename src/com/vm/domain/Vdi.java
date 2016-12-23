package com.vm.domain;

/**
 * 此类用于界面显示sr中的磁盘信息
 * @author Administrator
 *
 */
import javafx.beans.property.SimpleStringProperty;

public class Vdi {

	private final SimpleStringProperty name;
	private final SimpleStringProperty des;
	private final SimpleStringProperty size;//磁盘容量
	private final SimpleStringProperty vm;//所属虚拟机
	
	
	public Vdi(String name ,String des,String size,String vm)
	{
		this.name = new SimpleStringProperty(name);
		this.des = new SimpleStringProperty(des);
		this.size = new SimpleStringProperty(size);
		this.vm = new SimpleStringProperty(vm);
	}

	public String getName() {
		return name.get();
	}

	public String getDes() {
		return des.get();
	}

	public String getSize() {
		return size.get();
	}
	public String getVm() {
		return vm.get();
	}
}

