package com.seu.seuairmonitor.domain;

import com.kot32.ksimplelibrary.model.response.BaseResponse;

/**
 * Created by pc on 15/11/15.
 */
public class LoadingPublicResponse extends BaseResponse {

    private  String nodes;
    private String publicdatas;

    public String getNodes() {
        return nodes;
    }

    public void setNodes(String nodes) {
        this.nodes = nodes;
    }

    public String getPublicdatas() {
        return publicdatas;
    }

    public void setPublicdatas(String publicdatas) {
        this.publicdatas = publicdatas;
    }
}
