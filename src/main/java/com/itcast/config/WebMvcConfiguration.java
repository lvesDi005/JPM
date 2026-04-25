package com.itcast.config;

import com.itcast.interceptor.JwtTokenAdminInterceptor;
import com.itcast.interceptor.JwtTokenUserInterceptor;
import com.itcast.json.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@Slf4j
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;
    @Autowired
    private JwtTokenUserInterceptor jwtTokenUserInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/employee/login")
                .excludePathPatterns("/admin/redis/**")
                .excludePathPatterns("/admin/common/**")
                .excludePathPatterns("/ws/**")
                .excludePathPatterns("/doc.html")
                .excludePathPatterns("/v3/api-docs/**")
                .excludePathPatterns("/swagger-resources/**")
                .excludePathPatterns("/webjars/**");

        registry.addInterceptor(jwtTokenUserInterceptor)
                .addPathPatterns("/user/**")
                .excludePathPatterns("/user/user/login")
                .excludePathPatterns("/user/shop/status")
                .excludePathPatterns("/user/payment/**")
                .excludePathPatterns("/ws/**")
                .excludePathPatterns("/doc.html")
                .excludePathPatterns("/v3/api-docs/**")
                .excludePathPatterns("/swagger-resources/**")
                .excludePathPatterns("/webjars/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/");

        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器...");
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(new JacksonObjectMapper());
        // Keep Spring's default converters first to avoid breaking springdoc/knife4j responses.
        converters.add(converter);
    }
}
