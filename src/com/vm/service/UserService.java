package com.vm.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vm.domain.User;
import com.vm.domain.VmInfo;
import com.vm.mapper.UserMapper;
import com.vm.sql.EncoderByMd5;
import com.vm.tools.Tools;




@Service
public class UserService {

	@Autowired
	private VmInfoService vmInfoService;
	@Autowired
	private UserMapper userMapper;
	
	/**
	 * 检查用户的合法性
	 * @param userName
	 * @param passWord
	 * @return
	 */
	public boolean checkAuthority(String userName, String passWord){
		if( StringUtils.isEmpty(userName)|| StringUtils.isEmpty(passWord)){
			return false;
		}
		try{
			String encoderPassWord = EncoderByMd5.Encoder(passWord);
			User user = userMapper.getUserByName(userName);
			if(user != null){
				if(user.getPassword().equals(encoderPassWord)){
					return true;
				}
			}
			return false;
		}catch(Exception e){
			System.err.println("UserService.checkAuthority出錯,userName="+userName+"passWord="+passWord);
			return false;
		}
		
	}

	/**
	 * 获取用户的信息，如果用户不存在，则返回空的map
	 * @param userName
	 * @return
	 */
	public Map<String, Object> getUserInfo(String userName) {
		// TODO Auto-generated method stub
		Map<String,Object> userInfo = new  HashMap<>();
		if(StringUtils.isEmpty(userName)){
			return userInfo;
		}
		User user = userMapper.getUserByName(userName);
		userInfo.put("user", user);
		if(user != null){
			String uuid = user.getVmUuid();
			VmInfo vmInfo = vmInfoService.getVmInfoByUuid(uuid);
			userInfo.put("vmInfo", vmInfo);	
		}else{
			userInfo.put("vmInfo", null);	
		}
		return userInfo;
	}
	
	/**
	 * 更新
	 * @param user
	 */
	public void update(User user) {
		// TODO Auto-generated method stub
		if(user == null){
			System.err.println("UserService.update 出错");
			return;
		}
		userMapper.update(user);
	}
	
	/**
	 * 根据用户名，获取user
	 * @param userName
	 * @return
	 */
	public User getUserByName(String userName){
		if(StringUtils.isEmpty(userName)){
			return null;
		}
		return userMapper.getUserByName(userName);
	}
}
