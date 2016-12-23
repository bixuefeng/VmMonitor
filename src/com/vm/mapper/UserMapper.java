package com.vm.mapper;



import org.springframework.stereotype.Component;

import com.vm.domain.User;

@Component
public interface UserMapper {

	public void deleteByName(String userName);
	public void update(User user);
	public User getUserByName(String userName);
}
