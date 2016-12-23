package com.vm.mapper;

import org.springframework.stereotype.Component;

import com.vm.domain.VmInfo;

@Component
public interface VmInfoMapper {

	public VmInfo getVmInfoByUuid(String uuid);
	public void deleteByUuid(String uuid);
	public void update(VmInfo vmInfo);
	public void insert(VmInfo vmInfo);
}
