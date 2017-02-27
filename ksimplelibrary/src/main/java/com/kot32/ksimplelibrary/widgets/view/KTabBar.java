package com.kot32.ksimplelibrary.widgets.view;

import android.content.Context;
import android.support.percent.PercentLayoutHelper;
import android.support.percent.PercentRelativeLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kot32.ksimplelibrary.util.tools.ViewUtil;
import com.kot32.ksimplelibrary.util.tools.reflect.FieldUtils;
import com.kot32.ksimplelibrary.widgets.base.KBaseWidgets;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kot32 on 15/11/15.
 * <p/>
 * 可定制的Tab界面
 * STYLE_GRADUAL 类似微信的渐变+可滑动的效果
 * STYLE_NORMAL 类似美团的点击变成选中+不可滑动效果
 */
public class KTabBar extends KBaseWidgets {

    public enum TabStyle {
        STYLE_GRADUAL, STYLE_NORMAL
    }

    private TabStyle style = TabStyle.STYLE_GRADUAL;

    private List<TabView> tabs = new ArrayList<>();

    private int count = 0;
    private int lastIndex = 0;

    private OnTabClickListener onTabClickListener;

    public KTabBar(Context context, TabStyle style) {
        super(context);
        this.style = style;
    }

    public KTabBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {
        setOrientation(LinearLayout.HORIZONTAL);
        this.setPadding(5, 5, 5, 5);
    }

    @Override
    public void initController() {

    }


    public void addTab(int imgId, int highlightImgId, String text, int fontColor, int highlightFontColor, float widthRatio, float heightRatio, float marginTopRatio) {
        TabView tabView = null;

        if (style == TabStyle.STYLE_GRADUAL) {
            tabView = new GradualTabView(getContext()).NewTabView(imgId, highlightImgId, text, fontColor, highlightFontColor, widthRatio, heightRatio, marginTopRatio);
        } else if (style == TabStyle.STYLE_NORMAL) {
            tabView = new NormalTabView(getContext()).NewTabView(imgId, highlightImgId, text, fontColor, highlightFontColor, widthRatio, heightRatio, marginTopRatio);
        }
        count++;
        final int index = count - 1;
        LayoutParams layoutParams = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        tabView.setLayoutParams(layoutParams);

        this.addView(tabView);
        tabs.add(tabView);

        tabView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTabClickListener != null) {
                    onTabClickListener.onClick(index);
                }
                //重置其他所有TAB
                for (TabView tab : tabs) {
                    if (tab == v) continue;
                    tab.beDarker(1);
                }
                lastIndex = index;
                ((TabView) v).beLighter(1);
            }
        });

        if (index == 0) {
            tabView.beLighter(1);
        }

    }


    class TabView extends PercentRelativeLayout {

        public TabView(Context context) {
            super(context);
        }

        public TabView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        //变为不选中
        public void beDarker(float offset) {

        }

        //变为选中
        public void beLighter(float offset) {

        }

    }

    public TabView getTabView(int position) {
        return tabs.get(position);
    }

    /**
     * 渐变的TabView，占用内存多一点
     */
    public class GradualTabView extends TabView {

        private FrameLayout imageSpace;
        private ImageView imageView_outer;
        private ImageView imageView_inner;

        private TextView textView_outer;
        private TextView textView_inner;

        private FrameLayout textSpace;


        public GradualTabView(Context context) {
            super(context);
            init();
        }

        public GradualTabView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {

            imageSpace = new FrameLayout(getContext());
            imageView_outer = new ImageView(getContext());
            imageView_inner = new ImageView(getContext());


            textView_inner = new TextView(getContext());
            textView_outer = new TextView(getContext());
        }

        public GradualTabView NewTabView(int iconImgId, int highlightImgId, String text, int fontColor, int highlightFontColor, float wr, float hr, float tr) {
            textView_inner.setText(text);
            textView_inner.setTextColor(highlightFontColor);
            textView_inner.setTextSize(12);
            textView_inner.setAlpha(0);

            textView_outer.setText(text);
            textView_outer.setTextColor(fontColor);
            textView_outer.setTextSize(12);
            textView_outer.setAlpha(1);

            imageView_inner.setImageResource(highlightImgId);
            imageView_inner.setAlpha(0f);
            imageView_outer.setImageResource(iconImgId);
            imageView_outer.setAlpha(1f);

            /**
             * Google 竟然没提供set 方法。。罢了
             */
            PercentLayoutHelper.PercentLayoutInfo percentInfo = new PercentLayoutHelper.PercentLayoutInfo();
            percentInfo.heightPercent = hr;
            percentInfo.widthPercent = wr;
            percentInfo.topMarginPercent = tr;
            PercentRelativeLayout.LayoutParams percentLayoutParams = new PercentRelativeLayout.LayoutParams(mContext, null);
            percentLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            //获取到LayoutInfo 并进行修改
            Field infoField = FieldUtils.getDeclaredField(PercentRelativeLayout.LayoutParams.class, "mPercentLayoutInfo", true);
            try {
                FieldUtils.writeField(infoField, percentLayoutParams, percentInfo);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            int dyId = ViewUtil.generateViewId();
            imageSpace.setId(dyId);
            imageSpace.setLayoutParams(percentLayoutParams);
            imageSpace.addView(imageView_inner, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageSpace.addView(imageView_outer, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


            RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textParams.addRule(RelativeLayout.BELOW, dyId);
            textParams.addRule(RelativeLayout.CENTER_HORIZONTAL,TRUE);
            textSpace = new FrameLayout(getContext());

            textSpace.setLayoutParams(textParams);
            textSpace.addView(textView_inner, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            textSpace.addView(textView_outer, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


            textView_inner.setGravity(Gravity.CENTER_HORIZONTAL);
            textView_outer.setGravity(Gravity.CENTER_HORIZONTAL);

            this.addView(imageSpace);
            this.addView(textSpace);
            return this;
        }


        //选中状态
        public void beLighter(float offset) {

            imageView_inner.setAlpha(offset);
            imageView_outer.setAlpha(1 - offset);

            textView_inner.setAlpha(offset);
            textView_outer.setAlpha(1 - offset);

        }


        public void beDarker(float offset) {

            imageView_inner.setAlpha(1 - offset);
            imageView_outer.setAlpha(offset);

            textView_inner.setAlpha(1 - offset);
            textView_outer.setAlpha(offset);


        }

    }

    /**
     * 普通的TabView ，占用内存少一点
     */
    class NormalTabView extends TabView {

        private ImageView imageView;
        private TextView textView;
        private int iconImgId, highlightImgId;
        private int fontColor, highlightFontColor;

        public NormalTabView(Context context) {
            super(context);
            init();
        }

        public NormalTabView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            imageView = new ImageView(getContext());
            textView = new TextView(getContext());
        }

        public NormalTabView NewTabView(int iconImgId, int highlightImgId, String text, int fontColor, int highlightFontColor, float wr, float hr, float tr) {
            this.iconImgId = iconImgId;
            this.highlightImgId = highlightImgId;
            this.fontColor = fontColor;
            this.highlightFontColor = highlightFontColor;

            textView.setText(text);
            textView.setTextColor(fontColor);
            textView.setTextSize(12);

            imageView.setImageResource(iconImgId);

            PercentLayoutHelper.PercentLayoutInfo percentInfo = new PercentLayoutHelper.PercentLayoutInfo();
            percentInfo.heightPercent = hr;
            percentInfo.widthPercent = wr;
            percentInfo.topMarginPercent = tr;
            PercentRelativeLayout.LayoutParams percentLayoutParams = new PercentRelativeLayout.LayoutParams(mContext, null);
            percentLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            //获取到LayoutInfo 并进行修改
            Field infoField = FieldUtils.getDeclaredField(PercentRelativeLayout.LayoutParams.class, "mPercentLayoutInfo", true);
            try {
                FieldUtils.writeField(infoField, percentLayoutParams, percentInfo);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            int dyId = ViewUtil.generateViewId();
            imageView.setLayoutParams(percentLayoutParams);
            imageView.setId(dyId);

            textView.setGravity(Gravity.CENTER_HORIZONTAL);

            RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textParams.addRule(RelativeLayout.BELOW, dyId);
            textParams.addRule(RelativeLayout.CENTER_HORIZONTAL,TRUE);
            textView.setLayoutParams(textParams);

            this.addView(imageView);
            this.addView(textView);
            return this;
        }

        @Override
        public void beDarker(float offset) {

            imageView.setImageResource(this.iconImgId);
            textView.setTextColor(fontColor);
        }

        @Override
        public void beLighter(float offset) {

            imageView.setImageResource(this.highlightImgId);
            textView.setTextColor(highlightFontColor);
        }
    }

    public interface OnTabClickListener {
        void onClick(int index);
    }


    public void setOnTabClickListener(OnTabClickListener onTabClickListener) {
        this.onTabClickListener = onTabClickListener;
    }
}
