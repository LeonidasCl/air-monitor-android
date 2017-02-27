package com.seu.seuairmonitor.fragment;

import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kot32.ksimpleframeworklibrary.R;
import com.kot32.ksimplelibrary.activity.i.IBaseAction;
import com.kot32.ksimplelibrary.cache.ACache;
import com.kot32.ksimplelibrary.fragment.t.base.KSimpleBaseFragmentImpl;



public class UserFragment extends KSimpleBaseFragmentImpl implements IBaseAction {

    private ACache mCache;
    private DrawerLayout drawer;
    private final static String AVATAR_URL = "https://api.sinas3.com/v1/SAE_airserverseu/avatar/default%2Fhead.png";

    @Override
    public IBaseAction getIBaseAction() {
        return this;
    }


    @Override
    public int initLocalData() {

        return LOAD_NETWORK_DATA_AND_DISMISS;
    }

    @Override
    public void initView(ViewGroup view) {
        mCache = ACache.get(getActivity());
        String judge=mCache.getAsString("username");
        Button button=(Button)(getActivity().findViewById(R.id.Btn_edit_info));
        TextView textview=(TextView)(getActivity().findViewById(R.id.userlabel));

        if (judge!=null) {
            button.setText(judge);
            textview.setText(judge);
            //已登录，查看并修改用户信息
        } else {//未登录

        }

    }

    @Override
    public void initController() {

    }

    @Override
    public void onLoadingNetworkData() {

    }

    @Override
    public void onLoadedNetworkData(View view) {

    }

    @Override
    public int getContentLayoutID() {
        return R.layout.fragment_user;
    }



}
