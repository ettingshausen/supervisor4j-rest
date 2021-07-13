package com.winning.supervisor4j.service;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.taobao.api.ApiException;

import com.winning.supervisor4j.config.DingTalkConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DingTalkMessageService {


    @Resource
    private DingTalkConfig dingTalkConfig;

    private static final String API = "https://oapi.dingtalk.com/robot/send?access_token=";


    public void sendTextMessage(String app) {
        DingTalkClient client = new DefaultDingTalkClient(buildServiceURL());
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("actionCard");
        OapiRobotSendRequest.Actioncard actioncard = new OapiRobotSendRequest.Actioncard();
        actioncard.setTitle(app);
        actioncard.setText(String.format("# %s \n", app) +
                "---\n" +
               String.format("启动状态: <font color=#52C41A>%s</font> \n", "成功"));
        request.setActionCard(actioncard);

        try {
            client.execute(request);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }


    public void sendActionCard(OapiRobotSendRequest.Actioncard actionCard) {
        DingTalkClient client = new DefaultDingTalkClient(buildServiceURL());
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("actionCard");
        request.setActionCard(actionCard);

        try {
            client.execute(request);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    private String buildServiceURL(){
        String serveUrl = API + dingTalkConfig.getToken();
        if (!StringUtils.isEmpty(dingTalkConfig.getSecret() )) {
            Map<String, Object> signMap = getSignMap();
            serveUrl += "&sign=" + signMap.get("sign") + "&timestamp=" + signMap.get("timestamp");
        }
        return serveUrl;
    }



    private Map<String, Object> getSignMap() {
        Map<String, Object> map = new HashMap<>(16);
        Long timestamp = System.currentTimeMillis();
        log.info("获取时间戳" + timestamp);
        String secret = dingTalkConfig.getSecret();
        log.info("获取配置信息里面的密钥" + secret);
        if (!StringUtils.isEmpty(secret)) {
            try {
                String stringToSign = timestamp + "\n" + secret;
                Mac mac = Mac.getInstance("HmacSHA256");
                mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
                byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
                String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), StandardCharsets.UTF_8.toString());
                log.info("获取签名信息sign" + sign);
                System.out.println(sign);
                map.put("sign", sign);
                map.put("timestamp", timestamp);
            } catch (Exception ignore) {
            }
        } else {
            map.put("sign", "");
            map.put("timestamp", "");
        }

        return map;
    }

}
