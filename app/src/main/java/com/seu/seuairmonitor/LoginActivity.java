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
 *
 */
public class LoginActivity extends KSimpleBaseActivityImpl implements IBaseAction {

    private ProgressDialog progressDialog;
    private Button confirm;
    private HashMap<String, String> loginParams;

    private final static String SERVER_URL = "http://www.airserverseu.applinzi.com/login.php";

    public static int IS_LOGIN=0;

    @Override
    public int initLocalData() {
        loginParams = new HashMap<>();
       // loginParams.put("username", "username");
       // loginParams.put("password", "password");
        return 0;
    }

    @Override
    public void initView(ViewGroup view) {
        confirm = (Button) view.findViewById(R.id.loginbutton_login);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("正在登录...");
    }

    @Override
    public void initController() {
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                TextView text=(TextView)findViewById(R.id.editText);
                TextView text2=(TextView)findViewById(R.id.editText2);
                String username=text.getText().toString();
                String password=text2.getText().toString();
                loginParams.put("username", username);
                loginParams.put("password", password);
                SimpleTaskManager.startNewTask(new LoginTask(getTaskTag(), getSimpleApplicationContext(),
                        LoginResponse.class, loginParams, SERVER_URL, NetworkTask.GET) {
                    @Override
                    public void onConnectFailed(NetworkExecutor.NetworkResult result) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "失败:网络无连接或连接失败" + result.resultObject.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public boolean isLoginSucceed(BaseResponse baseResponse) {
                        LoginResponse loginResponse = (LoginResponse) baseResponse;
                        if (loginResponse.getResponseCode() == 1) {
                            Toast.makeText(LoginActivity.this, "登录成功" + loginResponse.getUsername() + loginResponse.getAvatar() + loginResponse.getToken(), Toast.LENGTH_SHORT).show();
                            String nodelist=loginResponse.getNodeID();
                            ACache mCache = ACache.get(LoginActivity.this);
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

                            /*刷新界面*/
                            finish();


                            progressDialog.dismiss();
                            return true;
                        } else if (loginResponse.getResponseCode() == 0) {
                            Toast.makeText(LoginActivity.this, "登录失败：请先注册", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            return false;
                        } else if (loginResponse.getResponseCode() == 2) {
                            Toast.makeText(LoginActivity.this, "登录失败：该用户被冻结", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            return false;
                        } else if (loginResponse.getResponseCode() == 3) {
                            Toast.makeText(LoginActivity.this, "登录失败：用户名或密码错误", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            return false;
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
        return R.layout.activity_login;
    }


}

