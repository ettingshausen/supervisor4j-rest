package com.winning.supervisor4j.service;

import com.satikey.tools.supervisord.Supervisord;
import com.winning.supervisor4j.config.SupervisorConfig;

import java.util.ArrayList;
import java.util.List;

public class SupervisordHolder {
    private final List<Supervisord> supervisords = new ArrayList<>();

    public SupervisordHolder(SupervisorConfig supervisorConfig) {

        List<SupervisorConfig.Instance> instances = supervisorConfig.getInstances();

        for (SupervisorConfig.Instance instance: instances) {
            Supervisord supervisord = Supervisord
                    //API ADDR,Default:http://localhost:9001/RPC2
                    .connect(instance.getUrl() + "/RPC2")
                    // USERNAME AND PASSWORD
                    .auth(instance.getUsername(), instance.getPassword())
                    // the Identification, U shoud find it in "supervisord.conf" default supervisor
                    .namespace("supervisor");
            supervisords.add(supervisord);

        }
    }

    public Supervisord getSupervisord(int instanceIndex) {
        return supervisords.get(instanceIndex);
    }
}
