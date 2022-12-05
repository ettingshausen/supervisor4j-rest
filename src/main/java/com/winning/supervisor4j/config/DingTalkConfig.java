package com.winning.supervisor4j.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties("dingtalk")
@Data
public class DingTalkConfig {
    private String token;
    private String secret;
    private boolean enable;

    private List<DingTalkConfig> otherChatGroups;
}
