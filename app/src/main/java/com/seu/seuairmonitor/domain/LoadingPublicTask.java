package com.seu.seuairmonitor.domain;

import android.util.Log;

import com.kot32.ksimplelibrary.KSimpleApplication;
import com.kot32.ksimplelibrary.manager.preference.PreferenceManager;
import com.kot32.ksimplelibrary.manager.task.base.NetworkTask;
import com.kot32.ksimplelibrary.model.domain.BaseUserModel;
import com.kot32.ksimplelibrary.model.response.BaseResponse;
import com.kot32.ksimplelibrary.network.NetworkExecutor;
import com.kot32.ksimplelibrary.util.tools.reflect.FieldUtil;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Created by kot32 on 15/12/1.
 */
public abstract class LoadingPublicTask extends NetworkTask {

    private KSimpleApplication context;

    public LoadingPublicTask(String tag, KSimpleApplication context, Class resultClass, HashMap requestParams, String serverURL, int requestType) {
        super(tag, context, resultClass, requestParams, serverURL, requestType);
        this.context = context;
    }

    @Override
    public void onExecutedMission(NetworkExecutor.NetworkResult result) {
        if (isSucceed((BaseResponse) result.resultObject)) {
            onSucceed((BaseResponse) result.resultObject);
        } else {
            context.setUserModel(null);
        }
    }

    @Override
    public void onExecutedFailed(NetworkExecutor.NetworkResult result) {
        onConnectFailed(result);
    }

    /**
     * 在次判断是否请求成功
     *
     * @param baseResponse
     * @return
     */
    public abstract boolean isSucceed(BaseResponse baseResponse);

    public abstract void onConnectFailed(NetworkExecutor.NetworkResult result);

    /**
     * 登录完毕后将用户信息保存
     */
    public void onSucceed(BaseResponse responseModel) {
        //遍历对象中的 UserModel 数据
        for (Field field : FieldUtil.getAllDeclaredFields(responseModel.getClass())) {
            if (BaseUserModel.class.isAssignableFrom(field.getType())) {
                try {
                    BaseUserModel baseUserModel = (BaseUserModel) FieldUtil.get(field, responseModel);
                    if (baseUserModel == null) {
                        Log.e("警告", "返回数据中不包含用户数据模型");
                        return;
                    }
                    context.setUserModel(baseUserModel);
                    PreferenceManager.setLocalUserModel(baseUserModel);//

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
