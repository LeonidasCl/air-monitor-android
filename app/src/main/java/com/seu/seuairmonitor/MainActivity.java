package com.seu.seuairmonitor;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kot32.ksimpleframeworklibrary.R;
import com.seu.seuairmonitor.domain.LoadingPublicResponse;
import com.seu.seuairmonitor.domain.LoadingPublicTask;
import com.seu.seuairmonitor.fragment.DetailsFragment;
import com.seu.seuairmonitor.fragment.IndexFragment;
import com.seu.seuairmonitor.fragment.OverviewFragment;
import com.seu.seuairmonitor.fragment.UserFragment;
import com.seu.seuairmonitor.model.UserInfo;
import com.kot32.ksimplelibrary.activity.i.IBaseAction;
import com.kot32.ksimplelibrary.activity.t.KTabActivity;
import com.kot32.ksimplelibrary.cache.ACache;
import com.kot32.ksimplelibrary.manager.task.base.NetworkTask;
import com.kot32.ksimplelibrary.manager.task.base.SimpleTaskManager;
import com.kot32.ksimplelibrary.model.response.BaseResponse;
import com.kot32.ksimplelibrary.network.NetworkExecutor;
import com.kot32.ksimplelibrary.widgets.drawer.KDrawerBuilder;
import com.kot32.ksimplelibrary.widgets.drawer.component.DrawerComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends KTabActivity implements IBaseAction {

    private List<Fragment> fragmentList = new ArrayList<>();

    private Toolbar toolbar;

    private String AVATAR_URL = "https://api.sinas3.com/v1/SAE_airserverseu/avatar/default%2Fhead.png";

    public static String DATA_URL = "http://www.airserverseu.applinzi.com/fetchdata.php";

    private DrawerLayout drawer;

    private ACache mCache;
    private String checkLogin;

 public enum ChartType {
       COLUMN_CHART,  PREVIEW_LINE_CHART,  OTHER
    }

    @Override
    public int initLocalData() {
        return 0;
    }

    @Override
    public void initView(ViewGroup view) {
        mCache = ACache.get(MainActivity.this);
        toolbar.setTitleTextColor(0xffffffff);
        setTitle("区域扬尘监测系统");

        final DrawerComponent.DrawerHeader header = new DrawerComponent.DrawerHeader(DrawerComponent.DrawerHeader.DrawerHeaderStyle.NORMAL,
                R.drawable.drawer_theme_6_bg,
                this);


        header.addAvatar(R.drawable.avatar, AVATAR_URL, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断当前是否登录
                mCache = ACache.get(MainActivity.this);
                checkLogin=mCache.getAsString("username");
                if (checkLogin!=null) {

                    Toast.makeText(MainActivity.this, "已登录,查看并修改用户信息", Toast.LENGTH_SHORT).show();

                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    if (drawer != null) {
                        drawer.closeDrawers();
                    }
                }
            }
        });
        header.addNickName("未登录");
        header.addIntroduction("请点击默认头像登录");

        drawer = new KDrawerBuilder(this)
                .withToolBar(toolbar)
                .withWidth(300)
                .addDrawerHeader(header, null)
                .addDrawerSectionTitle("菜单", Color.DKGRAY)
                .addDrawerSubItem(R.drawable.ic_commented, "意见反馈", null, null)
                .addDrawerSubItem(R.drawable.ic_drawer_explore_normal, "注销", null,  new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCache = ACache.get(MainActivity.this);
                        checkLogin=mCache.getAsString("username");
                        if (checkLogin==null){
                            Toast.makeText(MainActivity.this, "你还没有登录", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        getSimpleApplicationContext().logout();

                        mCache.remove("username");
                        mCache.remove("useravatar");
                        mCache.remove("token");
                        mCache.remove("subscribenodes");
                        mCache.remove("nodeid");
                        mCache.remove("nodecomment");
                        checkLogin=mCache.getAsString("username");

                        if (checkLogin==null){
                            Toast.makeText(MainActivity.this, "注销成功", Toast.LENGTH_SHORT).show();
                            /*刷新界面*/
                            finish();
                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                            startActivityForResult(intent, 1);
                        }
                    }
                })
                .addDrawerSubItem(R.drawable.ic_register_normal, "注册", null, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //判断当前是否登录
                        mCache = ACache.get(MainActivity.this);
                        checkLogin=mCache.getAsString("username");
                        if (checkLogin!=null) {

                            Toast.makeText(MainActivity.this, "你已经注册过了！", Toast.LENGTH_SHORT).show();

                        } else {
                            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                            startActivityForResult(intent, 1);
                            if (drawer != null) {
                                drawer.closeDrawers();
                            }
                        }
                    }
                })
                .addDrawerDivider(Color.parseColor("#f1f2f1"))
                .addDrawerSubItem("", "关于本软件", null, null)
                .addDrawerSubItem("", "检查更新", null, null)
                .withDrawerAction(new KDrawerBuilder.DrawerAction() {
                    @Override
                    public void onDrawerOpened(View kDrawerView) {
                        //打开了侧滑菜单
                        mCache = ACache.get(MainActivity.this);
                        checkLogin=mCache.getAsString("username");
                        if (checkLogin!=null) {
                            UserInfo user = (UserInfo) getSimpleApplicationContext().getUserModel();
                            header.changeNickName(checkLogin);
                            header.changeAvatorURL(mCache.getAsString("useravatar"));
                            header.changeIntroduction("正式用户");
                        }else{
                            header.changeNickName("未登录");
                            header.changeAvatorURL("https://api.sinas3.com/v1/SAE_airserverseu/avatar/default%2Fhead.png");
                            header.changeIntroduction("点击头像登录");
                        }
                    }

                    @Override
                    public void onDrawerClosed(View kDrawerView) {
                        //关闭了侧滑菜单
                    }
                })
                .build();



    }

    @Override
    public void initController() {
        addTab(R.mipmap.chats, R.mipmap.chats_green, "首页", Color.GRAY, Color.parseColor("#04b00f"));
        addTab(R.mipmap.contacts, R.mipmap.contacts_green, "概况", Color.GRAY, Color.parseColor("#04b00f"));
        addTab(R.mipmap.contacts, R.mipmap.contacts_green, "详情", Color.GRAY, Color.parseColor("#04b00f"));
        addTab(R.mipmap.discover, R.mipmap.discover_green, "用户中心", Color.GRAY, Color.parseColor("#04b00f"));

    }

    @Override
    public void onLoadingNetworkData() {
        final ACache publicdataCache = ACache.get(this);
        HashMap<String,String> publicDataParam = new HashMap<>();
        publicDataParam.put("publicdata", "get");
        /*刷新公共缓存数据*/
        SimpleTaskManager.startNewTask(new LoadingPublicTask(getTaskTag(),getSimpleApplicationContext() ,LoadingPublicResponse.class,
                publicDataParam,DATA_URL, NetworkTask.GET) {
                @Override
                public boolean isSucceed(BaseResponse baseResponse) {

                    LoadingPublicResponse publicResponse=(LoadingPublicResponse)baseResponse;
                    publicdataCache.put("nodes",publicResponse.getNodes(),ACache.TIME_HOUR);
                    publicdataCache.put("publicdata",publicResponse.getPublicdatas(),ACache.TIME_HOUR);

                    return true;
                }

                @Override
                public void onConnectFailed(NetworkExecutor.NetworkResult result) {
                    Toast.makeText(MainActivity.this, "警告：获取公共数据失败！请检查网络连接！" , Toast.LENGTH_SHORT).show();
                }
            });

    }

    @Override
    public void onLoadedNetworkData(View view) {

    }


    @Override
    public List<Fragment> getFragmentList() {

        fragmentList.add(new IndexFragment());
        fragmentList.add(new OverviewFragment());
        fragmentList.add(new DetailsFragment());
        fragmentList.add(new UserFragment());

        return fragmentList;
    }

    @Override
    public KTabActivity.TabConfig getTabConfig() {
        return null;
    }

    @Override
    public View getCustomContentView(View v) {
        ViewGroup vg = (ViewGroup) super.getCustomContentView(v);
        toolbar = (Toolbar) getLayoutInflater().inflate(R.layout.default_toolbar, null);
        vg.addView(toolbar, 0);
        return vg;
    }

    public void onNodesClick(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, 1);
    }

    public void onCheckUpdateClick(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, 1);
    }

    public void onUserInfoClick(View view){
        mCache = ACache.get(this);
        String judge=mCache.getAsString("username");
        Button button=(Button)(findViewById(R.id.Btn_edit_info));
        TextView textview=(TextView)(findViewById(R.id.userlabel));

        if (judge!=null) {
//            Intent intent=new Intent(this,UserInfoActivity.class);
//            startActivity(intent);   //////////待修改是否要接收信息即要不要加修改资料选项

            //已登录，查看并修改用户信息
        } else {//未登录
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    public String getTaskTag() {
        return this.getClass().getSimpleName();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 1) {
            finish();
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);

        }
    }
}
