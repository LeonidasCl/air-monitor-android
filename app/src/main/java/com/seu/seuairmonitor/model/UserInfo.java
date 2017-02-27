package com.seu.seuairmonitor.model;

import com.kot32.ksimplelibrary.model.domain.BaseUserModel;

/**
 * Created by pc on 2016/2/6.
 */
public class UserInfo implements BaseUserModel {

    /*数据模型键名必须严格与JSON包相同，区分字母大小写*/
    private String username;
    private String avatar;
    private String token;
    private String subscribenodes;
    private String nodeid;
    private String nodecomment;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public String getSubscribeNodes() {
        return subscribenodes;
    }

    public void setSubscribeNodes(String subscribeNodes) {
        this.subscribenodes = subscribeNodes;
    }

    public String getNodeID() {
        return nodeid;
    }


    public String getNodecomment() {
        return nodecomment;
    }

    public void setNodecomment(String nodecomment) {
        this.nodecomment = nodecomment;
    }
}
