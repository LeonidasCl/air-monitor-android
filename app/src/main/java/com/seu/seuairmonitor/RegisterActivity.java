package com.seu.seuairmonitor;

import android.app.ProgressDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kot32.ksimpleframeworklibrary.R;
import com.seu.seuairmonitor.domain.LoginResponse;
import com.kot32.ksimplelibrary.activity.i.IBaseAction;
import com.kot32.ksimplelibrary.activity.t.base.KSimpleBaseActivityImpl;
import com.kot32.ksimplelibrary.cache.ACache;
import com.kot32.ksimplelibrary.manager.task.LoginTask;
import com.kot32.ksimplelibrary.manager.task.base.NetworkTask;
import com.kot32.ksimplelibrary.manager.task.base.SimpleTaskManager;
import com.kot32.ksimplelibrary.model.response.BaseResponse;
import com.kot32.ksimplelibrary.network.NetworkExecutor;

import java.util.HashMap;

/**
 * Created by pc on 2016/2/10.
 */
public class RegisterActivity extends KSimpleBaseActivityImpl implements IBaseAction {

    private ProgressDialog progressDialog;
    private Button confirm;
    private HashMap<String, String> registParams;

    private final static String SERVER_URL = "http://www.airserverseu.applinzi.com/login.php";

    public static int IS_LOGIN=0;

    @Override
    public int initLocalData() {
        registParams = new HashMap<>();

        return 0;
    }

    @Override
    public void initView(ViewGroup view) {
        confirm = (Button) view.findViewById(R.id.registbutton_confirm);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("注册中...");
    }

    @Override
    public void initController() {
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                TextView text=(TextView)findViewById(R.id.register_username);
                TextView text2=(TextView)findViewById(R.id.register_password);
                TextView text3=(TextView)findViewById(R.id.register_email);
                String username=text.getText().toString();
                String password=text2.getText().toString();
                String email=text3.getText().toString();
                registParams.put("method", "register");
                registParams.put("username", username);
                registParams.put("password", password);
                registParams.put("email", email);
                SimpleTaskManager.startNewTask(new LoginTask(getTaskTag(), getSimpleApplicationContext(),
                        LoginResponse.class, registParams, SERVER_URL, NetworkTask.GET) {
                    @Override
                    public void onConnectFailed(NetworkExecutor.NetworkResult result) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "失败:网络无连接或连接失败" + result.resultObject.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public boolean isLoginSucceed(BaseResponse baseResponse) {
                        LoginResponse loginResponse = (LoginResponse) baseResponse;
                        if (loginResponse.getResponseCode() == 1) {
                            Toast.makeText(RegisterActivity.this, "注册成功" + loginResponse.getUsername() + loginResponse.getAvatar() + loginResponse.getToken(), Toast.LENGTH_SHORT).show();
                            String nodelist=loginResponse.getNodeID();

                            /*掌握fragment强制刷新技术后，在这里提示用户进行节点订阅设置*/

                            ACache mCache = ACache.get(RegisterActivity.this);
                            mCache.put("username", loginResponse.getUsername(), 7 * ACache.TIME_DAY);
                            mCache.put("useravatar", loginResponse.getAvatar(), 7 * ACache.TIME_DAY);
                            mCache.put("token", loginResponse.getToken(), 7 * ACache.TIME_DAY);
                            mCache.put("subscribenodes", loginResponse.getSubscribeNodes(), 7 * ACache.TIME_DAY);
                            mCache.put("nodeid",nodelist , 7 * ACache.TIME_DAY);
                            mCache.put("nodecomment", loginResponse.getNodeComment(), 7 * ACache.TIME_DAY);

                            String[] nodes=nodelist.split("&");
                            for(int i=0;i<nodes.length;i++){
                                mCache.put("detailsA" + nodes[i], loginResponse.getDatastr(nodes[i]+"A"),ACache.TIME_HOUR);
                                mCache.put("detailsB" + nodes[i], loginResponse.getDatastr(nodes[i]+"B"),ACache.TIME_HOUR);
                            }
                            progressDialog.dismiss();

                            /*刷新界面*/
                            finish();


                            return true;
                        }
                        return false;
                    }
                });
            }
        });
    }

    @Override
    public void onLoadingNetworkData() {

    }

    @Override
    public void onLoadedNetworkData(View view) {

    }

    @Override
    public int getContentLayoutID() {
        return R.layout.activity_register;
    }


}

