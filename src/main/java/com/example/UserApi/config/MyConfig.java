package com.example.UserApi.config;

import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class MyConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Value("${min.age:18}")
    private int minimumAge;
}
