package com.seu.seuairmonitor.domain;

import com.seu.seuairmonitor.model.UserInfo;
import com.kot32.ksimplelibrary.model.response.BaseResponse;

/**
 * Created by kot32 on 15/11/15.
 */
public class LoginResponse extends BaseResponse {

    private int responseCode;

    private UserInfo userInfo;

    private  String node1A;
    private  String node2A;
    private  String node3A;
    private  String node4A;
    private  String node5A;
    private  String node6A;
    private  String node7A;
    private  String node8A;
    private  String node9A;
    private  String node10A;
    private  String node1B;
    private  String node2B;
    private  String node3B;
    private  String node4B;
    private  String node5B;
    private  String node6B;
    private  String node7B;
    private  String node8B;
    private  String node9B;
    private  String node10B;

    public int getResponseCode() {
        return responseCode;
    }

    public String getUsername() {
        return userInfo.getUsername();
    }


    public String getAvatar() {
        return userInfo.getAvatar();
    }


    public String getToken() {
        return userInfo.getToken();
    }

    public String getSubscribeNodes() {
        return userInfo.getSubscribeNodes();
    }


    public String getNodeID() {
        return userInfo.getNodeID();
    }

    public String getNodeComment() {
        return userInfo.getNodecomment();
    }

    public String getDatastr(String node) {

        String returnS=null;

        if(node.equals("1A"))
            returnS=node1A;
        else if(node.equals("10A"))
            returnS= node10A;
        else if(node.equals("2A"))
            returnS= node2A;
        if(node.equals("3A"))
            returnS= node3A;
        else if(node.equals("4A"))
            returnS= node4A;
        else if(node.equals("5A"))
            returnS= node5A;
        else if(node.equals("6A"))
            returnS= node6A;
        else if(node.equals("7A"))
            returnS= node7A;
        else if(node.equals("8A"))
            returnS= node8A;
        else if(node.equals("9A"))
            returnS= node9A;
        else if(node.equals("1B"))
            returnS= node1B;
        else if(node.equals("10B"))
            returnS= node10B;
        else if(node.equals("2B"))
            returnS= node2B;
        else if(node.equals("3B"))
            returnS=node3B;
        else if(node.equals("4B"))
            returnS=node4B;
        else if(node.equals("5B"))
            returnS=node5B;
        else if(node.equals("6B"))
            returnS=node6B;
        else if(node.equals("7B"))
            returnS=node7B;
        else if(node.equals("8B"))
            returnS=node8B;
        else if(node.equals("9B"))
            returnS=node9B;

        return  returnS;
    }
}
