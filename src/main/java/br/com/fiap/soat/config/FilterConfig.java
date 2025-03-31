package br.com.fiap.soat.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.fiap.soat.controller.api.filter.JwtAuthFilter;

@Configuration
public class FilterConfig {

  @Bean
  public FilterRegistrationBean<JwtAuthFilter> 
      jwtAuthFilterRegistration(JwtAuthFilter jwtAuthFilter) {

    FilterRegistrationBean<JwtAuthFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(jwtAuthFilter);
    registrationBean.addUrlPatterns("/video/upload", "/video/listar");
    return registrationBean;
  }
}