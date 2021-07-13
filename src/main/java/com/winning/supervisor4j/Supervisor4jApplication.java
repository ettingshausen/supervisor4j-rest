package com.winning.supervisor4j;

import com.winning.supervisor4j.config.SupervisorConfig;
import com.winning.supervisor4j.service.SupervisordHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
public class Supervisor4jApplication {


    @Autowired
    private SupervisorConfig supervisorConfig;


    public static void main(String[] args) {
        SpringApplication.run(Supervisor4jApplication.class, args);
    }

    @Bean
    public SupervisordHolder supervisordHolder() {

        return new SupervisordHolder(supervisorConfig);
    }

}
