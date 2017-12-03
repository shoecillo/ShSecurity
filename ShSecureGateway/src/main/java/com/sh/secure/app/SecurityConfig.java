package com.sh.secure.app;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.sh.auth.filter.AuthRSAFilter;
import com.sh.auth.provider.CustomSecProvider;
import com.sh.auth.repository.UsersRepository;
import com.sh.auth.service.AttempsService;

/**
 * Spring security configuration
 * @author shoe011
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter   
{
	
	@Autowired
	private CustomSecProvider provider;
	
	@Autowired
	private AuthRSAFilter filter;
	
	@Autowired
	private UsersRepository dao;
	
	@Autowired
	private AttempsService att;
	
	 @Override
	 protected void configure(HttpSecurity http) throws Exception {
	        http
	        	.csrf()
	        		.disable()
	        	//.httpBasic()
	        	//.and()
		        .authenticationProvider(provider)
		        .addFilterAfter(filter, BasicAuthenticationFilter.class)
	            .formLogin()
	                .loginPage("/login.html")
	                .loginProcessingUrl("/login")
	                .usernameParameter("j_username")
	                .passwordParameter("j_password")
	                .defaultSuccessUrl("/index.html")
	                .permitAll()
	                .and()
	            .logout()
	                .permitAll()
	                .and()
	            .authorizeRequests()
	            	
	            	.antMatchers("/signIn","/css/**","/jsLib/**","/fonts/**","/appz/**","/img/**").permitAll()
	            	.anyRequest().authenticated();
	            	
	                
	    }

	    @Autowired
	    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
	        
	    	List<String> lsUsu = new ArrayList<>();
	    	dao.findAll().stream().forEach(e -> 
	    	{
	    		lsUsu.add(e.user);
	    	});
	        att.createBlacklist(lsUsu); 
	            
	    }
}
