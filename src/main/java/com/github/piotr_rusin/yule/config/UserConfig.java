package com.github.piotr_rusin.yule.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("security")
public class UserConfig {

	private final List<User> users = new ArrayList<>();

	public List<User> getUsers() {
		return this.users;
	}
}
