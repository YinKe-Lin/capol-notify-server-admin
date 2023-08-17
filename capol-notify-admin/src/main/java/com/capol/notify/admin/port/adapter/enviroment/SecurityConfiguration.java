package com.capol.notify.admin.port.adapter.enviroment;


import com.capol.notify.manage.application.user.UserService;
import com.capol.notify.manage.domain.model.permission.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final ObjectMapper objectMapper;
    private final TokenService tokenService;
    private final UserService userService;

    public SecurityConfiguration(ObjectMapper objectMapper, TokenService tokenService, UserService userService) {
        this.objectMapper = objectMapper;
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .authorizeRequests()
                .antMatchers("/**").permitAll()
                .antMatchers("/api/v1.0/admin/**").authenticated()
                .antMatchers("/api/v1.0/open/**").authenticated()
                .and()
                .addFilterBefore(new ExceptionHandlerFilter(objectMapper), BasicAuthenticationFilter.class)
                .addFilterAfter(new CustomAuthenticationFilter(tokenService, userService), ExceptionHandlerFilter.class)
                .httpBasic();
    }
}