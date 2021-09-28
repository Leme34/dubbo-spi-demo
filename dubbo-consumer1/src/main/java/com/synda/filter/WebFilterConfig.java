package com.synda.filter;

import com.synda.traceid.LogTraceWebFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

/**
 * Created by lsd
 * 2021-09-28 21:06
 */
@Configuration
public class WebFilterConfig {

    @Bean
    public FilterRegistrationBean registrationBean(){
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LogTraceWebFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("LogTraceWebFilter");
        registrationBean.setOrder(1);
        return registrationBean;
    }

}
