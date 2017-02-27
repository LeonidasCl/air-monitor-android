package com.seu.seuairmonitor;

import com.seu.seuairmonitor.domain.LoginResponse;
import com.kot32.ksimplelibrary.KSimpleApplication;
import com.kot32.ksimplelibrary.cache.ACache;
import com.kot32.ksimplelibrary.manager.task.LoginTask;
import com.kot32.ksimplelibrary.manager.task.base.NetworkTask;
import com.kot32.ksimplelibrary.model.response.BaseResponse;
import com.kot32.ksimplelibrary.network.NetworkExecutor;

import java.util.HashMap;

/**
 * Created by kot32 on 15/11/8.
 */
public class TestApplication extends KSimpleApplication {

    private ACache mCache;
    private final static String SERVER_URL = "http://www.airserverseu.applinzi.com/login.php";

    @Override
    public void startInit() {

    }

    @Override
    public void initLocalPreference(HashMap<String, ?> dataMap) {

    }

    @Override
    public void onInitLocalUserModelFailed() {

    }

    @Override
    public LoginTask getLoginTask() {
        mCache = ACache.get(this);/*检查缓存中是否有用户名密码*/
        String username = mCache.getAsString("username");
        String token = mCache.getAsString("token");
        HashMap<String, String> loginParams = new HashMap<>();
        /*从缓存中取出用户名密码并放入*/
        if (username != null) {
                loginParams.put("username", username);
                loginParams.put("token",token);
            }

        return new LoginTask(getTaskTag(), this,
                LoginResponse.class, loginParams, SERVER_URL, NetworkTask.GET) {
            @Override
            public boolean isLoginSucceed(BaseResponse baseResponse) {
                LoginResponse loginResponse = (LoginResponse) baseResponse;
                if (loginResponse.getResponseCode() == 1) {
                    /*自动登录后主动刷新缓存*/
                    String nodelist=loginResponse.getNodeID();
                    mCache.put("username", loginResponse.getUsername(), 7 * ACache.TIME_DAY);
                    mCache.put("useravatar", loginResponse.getAvatar(), 7 * ACache.TIME_DAY);
                    mCache.put("token", loginResponse.getToken(), 7 * ACache.TIME_DAY);
                    mCache.put("subscribenodes", loginResponse.getSubscribeNodes(), ACache.TIME_HOUR);
                    mCache.put("nodeid",nodelist ,  ACache.TIME_HOUR);
                    mCache.put("nodecomment", loginResponse.getNodeComment(), ACache.TIME_HOUR);

                    String[] nodes=nodelist.split("&");
                    for(int i=0;i<nodes.length;i++){
                        String putA1="detailsA" + nodes[i];
                        String putA2= loginResponse.getDatastr(nodes[i]+"A");
                       mCache.put(putA1,putA2,ACache.TIME_HOUR);
                       mCache.put("detailsB" + nodes[i], loginResponse.getDatastr(nodes[i]+"B"),ACache.TIME_HOUR);
                    }

                    return true;
                }
                return false;
            }

            @Override
            public void onConnectFailed(NetworkExecutor.NetworkResult result) {

            }
        };
    }


}
