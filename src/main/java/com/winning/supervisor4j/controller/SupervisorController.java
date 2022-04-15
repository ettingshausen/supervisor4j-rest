package com.winning.supervisor4j.controller;


import com.dingtalk.api.request.OapiRobotSendRequest;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.satikey.tools.supervisord.exceptions.SupervisordException;
import com.winning.supervisor4j.config.DingTalkConfig;
import com.winning.supervisor4j.config.SupervisorConfig;
import com.winning.supervisor4j.service.DingTalkMessageService;
import com.winning.supervisor4j.service.SupervisordHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class SupervisorController {


    @Autowired
    private SupervisorConfig supervisorConfig;
    @Resource
    private DingTalkMessageService dingTalkMessageService;

    @Autowired
    private DingTalkConfig dingTalkConfig;

    @Resource
    private SupervisordHolder supervisordHolder;

    @PostMapping("/stop/{app}")
    public String stop(@PathVariable String app) {
        try {
            supervisordHolder.getSupervisord().stopProcess(app, true);
            if (dingTalkConfig.isEnable()) {
                OapiRobotSendRequest.Actioncard actionCard = new OapiRobotSendRequest.Actioncard();
                actionCard.setTitle(app);
                actionCard.setText(String.format("# %s \n", app) +
                        "---\n" +
                        String.format("<font color=#52C41A>%s</font> \n", "正在部署，请稍后。"));
                dingTalkMessageService.sendActionCard(actionCard);
            }
        } catch (SupervisordException e) {
            return "stop failed!" + e.getMessage();
        }

        return "stop";
    }

    @PostMapping("/start/{app}")
    public String start(@PathVariable String app) {

        try {
            supervisordHolder.getSupervisord().startProcess(app, true);
            if (dingTalkConfig.isEnable()) {
                OapiRobotSendRequest.Actioncard actionCard = new OapiRobotSendRequest.Actioncard();
                actionCard.setTitle(app);
                actionCard.setText(String.format("# %s \n", app) +
                        "---\n" +
                        String.format("启动状态: <font color=#52C41A>%s</font> \n", "成功"));
                dingTalkMessageService.sendActionCard(actionCard);
            }
        } catch (SupervisordException e) {
            return "start failed!" + e.getMessage();
        }

        return "started";
    }

    @GetMapping("/version")
    public String version() throws SupervisordException {

        return String.format("version: %s", supervisordHolder.getSupervisord().getAPIVersion()) +
                System.lineSeparator() +
                String.format("supervisor_host: %s", supervisorConfig.getUrl()) +
                System.lineSeparator() +
                String.format("supervisor_username: %s", supervisorConfig.getUsername()) +
                System.lineSeparator() +
                String.format("supervisor_password: %s", supervisorConfig.getPassword()) +
                System.lineSeparator() +
                String.format("ding-talk_token: %s", dingTalkConfig.getToken()) +
                System.lineSeparator() +
                String.format("ding-talk_secret: %s", dingTalkConfig.getSecret()) +
                System.lineSeparator();
    }


    @GetMapping("/process/{app}")
    public Object processInfo(@PathVariable String app) {
        try {
            return supervisordHolder.getSupervisord().getProcessInfo(app);
        } catch (SupervisordException e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/send/{app}")
    public String send(@PathVariable String app) throws SupervisordException {
        OapiRobotSendRequest.Actioncard actionCard = new OapiRobotSendRequest.Actioncard();
        actionCard.setTitle(app);
        actionCard.setText(String.format("# %s \n", app) +
                "---\n" +
                String.format("启动状态: <font color=#52C41A>%s</font> \n", "成功"));
        dingTalkMessageService.sendActionCard(actionCard);
        return "done";
    }

}
