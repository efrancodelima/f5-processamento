package br.com.fiap.soat.config;

import br.com.fiap.soat.controller.filter.JwtAuthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
  
  @Bean
  public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilter() {
    FilterRegistrationBean<JwtAuthFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new JwtAuthFilter());
    registrationBean.addUrlPatterns("/*");
    return registrationBean;
  }
}
