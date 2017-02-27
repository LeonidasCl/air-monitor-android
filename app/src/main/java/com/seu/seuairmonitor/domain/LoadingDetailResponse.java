package com.seu.seuairmonitor.domain;

import com.kot32.ksimplelibrary.model.response.BaseResponse;

/**
 * Created by pc on 2016/2/9.
 */
public class LoadingDetailResponse extends BaseResponse{

    private String nodeid;

    private String datastr1;

    private String datastr2;

    public String getDatastr1() {
        return datastr1;
    }


    public String getDatastr2() {
        return datastr2;
    }

    public String getNodeid() {
        return nodeid;
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid;
    }
}
