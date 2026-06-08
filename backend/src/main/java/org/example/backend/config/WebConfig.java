package org.example.backend.config;

import org.example.backend.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new JwtInterceptor())
                .addPathPatterns("/movies/**")
                .excludePathPatterns(
                        "/users/login",
                        "/users/register"
                );
    }
}