package com.epam.esm.config;

import com.epam.esm.dto.Permission;
import com.epam.esm.security.JwtConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtConfigurer jwtConfigurer;

    @Autowired
    public SecurityConfig(JwtConfigurer jwtConfigurer) {
        this.jwtConfigurer = jwtConfigurer;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/auth/**").permitAll()
                .antMatchers(HttpMethod.GET, "/certificates/**").permitAll()
                .antMatchers(HttpMethod.GET, "/tags/**").hasAuthority(Permission.TAGS_READ.getPermission())
                .antMatchers(HttpMethod.POST, "/tags/**").hasAuthority(Permission.TAGS_WRITE.getPermission())
                .antMatchers(HttpMethod.DELETE, "/tags/**").hasAuthority(Permission.TAGS_DELETE.getPermission())
                .antMatchers(HttpMethod.GET, "certificates/**").hasAuthority(Permission.CERTIFICATES_READ.getPermission())
                .antMatchers(HttpMethod.POST, "/certificates/**").hasAuthority(Permission.CERTIFICATES_WRITE.getPermission())
                .antMatchers(HttpMethod.PATCH, "/certificates/**").hasAuthority(Permission.CERTIFICATES_EDIT.getPermission())
                .antMatchers(HttpMethod.DELETE, "/certificates/**").hasAuthority(Permission.CERTIFICATES_DELETE.getPermission())
                .antMatchers(HttpMethod.GET, "/users/**").hasAuthority(Permission.USERS_READ.getPermission())
                .antMatchers(HttpMethod.PATCH, "/users/**").hasAuthority(Permission.USERS_WRITE.getPermission())
                .anyRequest()
                .authenticated()
                .and()
                .apply(jwtConfigurer);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
