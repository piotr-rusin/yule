package com.github.piotr_rusin.yule.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@EnableConfigurationProperties(UserConfig.class)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	UserConfig application;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/admin/**").hasRole("ADMIN")
			.and()
				.formLogin();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		for (User user: application.getUsers()) {
			auth
				.inMemoryAuthentication()
				.withUser(user.getUsername())
				.password(user.getPassword())
				.roles(user.getRoles());
		}
	}
}
