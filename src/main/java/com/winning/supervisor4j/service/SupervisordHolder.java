package com.winning.supervisor4j.service;

import com.satikey.tools.supervisord.Supervisord;
import com.winning.supervisor4j.config.SupervisorConfig;

public class SupervisordHolder {
    private final Supervisord supervisord;

    public SupervisordHolder(SupervisorConfig supervisorConfig) {
        supervisord = Supervisord
                //API ADDR,Default:http://localhost:9001/RPC2
                .connect(supervisorConfig.getUrl() + "/RPC2")
                // USERNAME AND PASSWORD
                .auth(supervisorConfig.getUsername(), supervisorConfig.getPassword())
                // the Identification, U shoud find it in "supervisord.conf" default supervisor
                .namespace("supervisor");
    }

    public Supervisord getSupervisord() {
        return supervisord;
    }
}
