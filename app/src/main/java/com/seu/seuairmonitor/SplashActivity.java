package com.seu.seuairmonitor;

/**
 * Created by pc on 2016/3/10.
 */

import com.kot32.ksimpleframeworklibrary.R;
import com.kot32.ksimplelibrary.activity.t.KSplashActivity;



public class SplashActivity extends KSplashActivity {


    //TODO 查明loader第三参数究竟是什么，为什么还是同步加载


    @Override
    public void init() {

    }

    @Override
    public int getLogoImageResource() {
        return  R.drawable.start_logo;

    }

    @Override
    public Class getNextActivityClass() {
        return MainActivity.class;
    }

}
