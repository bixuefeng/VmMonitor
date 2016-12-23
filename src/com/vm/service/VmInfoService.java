package com.vm.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vm.domain.VmInfo;
import com.vm.mapper.VmInfoMapper;

@Service
public class VmInfoService {

	@Autowired
	private VmInfoMapper vmInfoMapper;
	
	
	public VmInfo getVmInfoByUuid(String uuid){
		if(StringUtils.isEmpty(uuid)){
			return null;
		}
		return vmInfoMapper.getVmInfoByUuid(uuid);
	}

	public void update(VmInfo vmInfo) {
		// TODO Auto-generated method stub
		if(vmInfo == null){
			return;
		}
		vmInfoMapper.update(vmInfo);
	}

	public void insert(VmInfo vmInfo) {
		// TODO Auto-generated method stub
		if(vmInfo == null){
			System.err.println("VmInfoService.insert ³ö´í");
			return;
		}
		vmInfoMapper.insert(vmInfo);
	}
}
