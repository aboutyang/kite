package com.aboutyang.config;

import com.aboutyang.common.filter.RequestResponseLoggingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;

/**
 * @author yangwang【yangwang@jd.com】
 * @date 2018/8/24 17:39
 */
@Configuration
@EnableConfigurationProperties(RequestResponseLoggingProperties.class)
public class RequestResponseLoggingConfiguration {

    @Autowired
    private RequestResponseLoggingProperties responseLoggingProperties;

    @ConditionalOnProperty(value = "kite.http.logging.enable", havingValue = "true")
    @Bean
    public FilterRegistrationBean loggingFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        RequestResponseLoggingFilter loggingFilter = new RequestResponseLoggingFilter();
        loggingFilter.setIncludeHeaders(responseLoggingProperties.isIncludeHeaders());
        loggingFilter.setIncludeQueryString(responseLoggingProperties.isIncludeQueryString());
        loggingFilter.setIncludeClientInfo(responseLoggingProperties.isIncludeClientInfo());
        loggingFilter.setIncludePayload(responseLoggingProperties.isIncludePayload());
        loggingFilter.setIncludeResponse(responseLoggingProperties.isIncludeResponse());
        loggingFilter.setIncludeResponseBody(responseLoggingProperties.isIncludeResponseBody());
        loggingFilter.setMaxPayloadLength(responseLoggingProperties.getMaxPayloadLength());
        loggingFilter.setMaxResponsePayloadLength(responseLoggingProperties.getMaxResponsePayloadLength());
        loggingFilter.setBeautifyDividingLine(responseLoggingProperties.getBeautifyDividingLine());
        loggingFilter.setSupportContentTypes(responseLoggingProperties.getSupportContentTypes());
        loggingFilter.setTraceKey(responseLoggingProperties.getTraceKey());
        loggingFilter.setUrlPatterns(responseLoggingProperties.getUrlPatterns());

        registration.setFilter(loggingFilter);
        registration.addUrlPatterns("/*");
        registration.setName(RequestResponseLoggingFilter.class.getName());
        //最早启动，记录所有请求和响应日志。
        registration.setOrder(Integer.MIN_VALUE);
        return registration;
    }

}
