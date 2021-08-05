package com.epam.esm.config;

import com.epam.esm.dto.Permission;
import com.epam.esm.jwt.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final JwtFilter jwtFilter;

    @Autowired
    public SecurityConfiguration(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
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
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
