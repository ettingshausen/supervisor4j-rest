package com.winning.supervisor4j.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("supervisor")
@Data
public class SupervisorConfig {

    private String url;
    private String username;
    private String password;

}
