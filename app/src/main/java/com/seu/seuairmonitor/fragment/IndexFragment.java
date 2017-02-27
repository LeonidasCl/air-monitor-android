package com.seu.seuairmonitor.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kot32.ksimpleframeworklibrary.R;
import com.seu.seuairmonitor.refresh.JDRefreshHeaderView;
import com.kot32.ksimplelibrary.activity.i.IBaseAction;
import com.kot32.ksimplelibrary.fragment.t.KRefreshFragment;
import com.kot32.ksimplelibrary.widgets.view.KRefreshView;

/**
 * Created by pc on 2016/2/4.
 */
/**
 * Created by kot32 on 15/11/4.
 */
public class IndexFragment extends KRefreshFragment implements IBaseAction {

    private JDRefreshHeaderView jdRefreshHeaderView;

    private WebView webview;

    private static final String BD_URL = "http://121.42.55.194:8000/wwwww/index.php";


    @Override
    public IBaseAction getIBaseAction() {
        return this;
    }


    @Override
    public int initLocalData() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return LOAD_NETWORK_DATA_AND_DISMISS;
    }

    @Override
    public void initView(ViewGroup view) {
        webview = (WebView) view.findViewById(R.id.webview);
        webview.loadUrl(BD_URL);
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
        return R.layout.fragment_test_webview;
    }

    @Override
    public View onRefresh() {
        //webview 需要在主线程刷新
        webview.post(new Runnable() {
            @Override
            public void run() {
                // webView.reload();
                webview.loadUrl(BD_URL);
            }
        });
        //刷新啦
        return null;
    }

    @Override
    public View onRefreshComplete() {
        //刷新完啦
        return null;
    }

    @Override
    public View customHeaderView() {
        jdRefreshHeaderView = new JDRefreshHeaderView(getActivity());
        return jdRefreshHeaderView;
    }

    @Override
    public void customRefreshView(KRefreshView refreshView) {
        refreshView.setRefreshViewHolder(jdRefreshHeaderView);
        refreshView.setHeaderHeight(80);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        setView();
        setListener();
        super.onActivityCreated(savedInstanceState);
    }

    private void setListener() {
        // TODO Auto-generated method stub
        webview.loadUrl(BD_URL);
        webview.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView   view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    private void setView() {
        // TODO Auto-generated method stub
        webview=(WebView)getView().findViewById(R.id.webview);
    }

}
