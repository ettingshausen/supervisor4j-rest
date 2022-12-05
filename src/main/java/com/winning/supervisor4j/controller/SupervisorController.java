package com.winning.supervisor4j.controller;


import com.dingtalk.api.request.OapiRobotSendRequest;
import com.satikey.tools.supervisord.Supervisord;
import com.satikey.tools.supervisord.exceptions.SupervisordException;
import com.winning.supervisor4j.config.DingTalkConfig;
import com.winning.supervisor4j.config.SupervisorConfig;
import com.winning.supervisor4j.service.DingTalkMessageService;
import com.winning.supervisor4j.service.SupervisordHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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
    public String stop(@PathVariable String app, @RequestParam(defaultValue = "0") int index,
                      @RequestParam(defaultValue = "0") int apush) {
        SupervisorConfig.Instance instance = supervisorConfig.getInstances().get(index);
        try {
            Supervisord supervisord = supervisordHolder.getSupervisord(index);
            supervisord.stopProcess(app, true);
            if (dingTalkConfig.isEnable()) {
                OapiRobotSendRequest.Actioncard actionCard = new OapiRobotSendRequest.Actioncard();
                actionCard.setTitle(app);
                actionCard.setText(String.format("# %s \n", app) +
                        "---\n" +
                        String.format("服务器: <font color=#52C41A>%s</font>    \n", instance.getHost()) +
                        String.format("<font color=#52C41A>%s</font> \n", "正在部署，请稍后。"));
                dingTalkMessageService.sendActionCard(actionCard);
                if (apush > 0) {
                    dingTalkMessageService.sendActionCard2Others(actionCard);
                }
            }
        } catch (SupervisordException e) {
            return "stop failed!" + e.getMessage();
        }

        return "stop";
    }

    @PostMapping("/start/{app}")
    public String start(@PathVariable String app, @RequestParam(defaultValue = "0") int index,
                        @RequestParam(required = false) String branch,
                        @RequestParam(required = false) String build,
                        @RequestParam(defaultValue = "0") int apush) {
        SupervisorConfig.Instance instance = supervisorConfig.getInstances().get(index);
        try {
            Supervisord supervisord = supervisordHolder.getSupervisord(index);
            supervisord.startProcess(app, true);
            if (dingTalkConfig.isEnable()) {
                OapiRobotSendRequest.Actioncard actionCard = new OapiRobotSendRequest.Actioncard();
                actionCard.setTitle(app);

                actionCard.setText(String.format("# %s \n", app) +
                        "---\n" +
                        String.format("服务器: <font color=#52C41A>%s</font>    \n", instance.getHost()) +
                        (StringUtils.isEmpty(branch) ? "" : String.format("分支: <font color=#52C41A>%s</font>    \n", branch)) +
                        (StringUtils.isEmpty(build) ? "" : String.format("Build: <font color=#52C41A>#%s</font>    \n", build)) +
                        String.format("启动状态: <font color=#52C41A>%s</font> \n", "成功"));
                dingTalkMessageService.sendActionCard(actionCard);
                if (apush > 0) {
                    dingTalkMessageService.sendActionCard2Others(actionCard);
                }
            }
        } catch (SupervisordException e) {
            return "start failed!" + e.getMessage();
        }

        return "started";
    }

    @GetMapping("/version")
    public String version(@RequestParam(defaultValue = "0") int index) throws SupervisordException {

        SupervisorConfig.Instance instance = supervisorConfig.getInstances().get(index);
        Supervisord supervisord = supervisordHolder.getSupervisord(index);

        return String.format("version: %s", supervisord.getAPIVersion()) +
                System.lineSeparator() +
                String.format("supervisor_host: %s", instance.getUrl()) +
                System.lineSeparator() +
                String.format("supervisor_username: %s", instance.getUsername()) +
                System.lineSeparator() +
                String.format("supervisor_password: %s", instance.getPassword()) +
                System.lineSeparator() +
                String.format("ding-talk_token: %s", dingTalkConfig.getToken()) +
                System.lineSeparator() +
                String.format("ding-talk_secret: %s", dingTalkConfig.getSecret()) +
                System.lineSeparator();
    }

    @GetMapping("/instances")
    public Object instances() {
        return supervisorConfig.getInstances();
    }


    @GetMapping("/process/{app}")
    public Object processInfo(@PathVariable String app, @RequestParam(defaultValue = "0") int index) {
        try {
            Supervisord supervisord = supervisordHolder.getSupervisord(index);
            return supervisord.getProcessInfo(app);
        } catch (SupervisordException e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/send/{app}")
    public String send(@PathVariable String app, @RequestParam(defaultValue = "0") int index,
                       @RequestParam(required = false) String branch,
                       @RequestParam(required = false) String build,
                       @RequestParam(defaultValue = "0") int apush) throws SupervisordException {
        SupervisorConfig.Instance instance = supervisorConfig.getInstances().get(index);

        OapiRobotSendRequest.Actioncard actionCard = new OapiRobotSendRequest.Actioncard();
        actionCard.setTitle(app);
        actionCard.setText(String.format("# %s \n", app) +
                "---\n" +
                String.format("服务器: <font color=#52C41A>%s</font>   \n", instance.getHost()) +
                (StringUtils.isEmpty(branch) ? "" : String.format("分支: <font color=#52C41A>%s</font>    \n", branch)) +
                (StringUtils.isEmpty(build) ? "" : String.format("Build: <font color=#52C41A>#%s</font>    \n", build)) +
                String.format("启动状态: <font color=#52C41A>%s</font> \n", "成功"));
        dingTalkMessageService.sendActionCard(actionCard);
        if (apush > 0) {
            dingTalkMessageService.sendActionCard2Others(actionCard);
        }
        return "done";
    }

}
