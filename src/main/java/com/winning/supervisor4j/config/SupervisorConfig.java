package com.winning.supervisor4j.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Configuration
@ConfigurationProperties("supervisor")
@Data
public class SupervisorConfig {

    @NestedConfigurationProperty
    private List<Instance> instances;


    @Data
    public static class Instance {
        private String url;
        private String username;
        private String password;

        public String getHost(){
            String host = "未知";
            try {
                URL mUrl = new URL(url);
                host = mUrl.getHost();
            } catch (MalformedURLException ignore) {
            }

            return host;
        }
    }

}
