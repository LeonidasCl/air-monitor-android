package com.seu.seuairmonitor.refresh;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kot32.ksimpleframeworklibrary.R;
import com.seu.seuairmonitor.fragment.DetailsFragment;
import com.kot32.ksimplelibrary.activity.i.IBaseAction;
import com.kot32.ksimplelibrary.activity.t.KTabActivity;
import com.kot32.ksimplelibrary.cache.ACache;
import com.kot32.ksimplelibrary.widgets.drawer.KDrawerBuilder;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PreviewLineChartView;

public class PreviewLineChartActivity extends KTabActivity implements IBaseAction {

    private List<Fragment> fragmentList = new ArrayList<>();
    private Toolbar toolbar;
    private KDrawerBuilder drawer;
    private  ACache detailCache;
    public static String nodeid;


 /*   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_line_chart);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        }
    }*/

    @Override
    public List<Fragment> getFragmentList() {
        fragmentList.add(new PlaceholderFragment());
        fragmentList.add(new PlaceholderFragment2());
        return fragmentList;
    }

    @Override
    public int initLocalData() {
        return IBaseAction.LOAD_NETWORK_DATA_AND_SHOW;
    }

    @Override
    public void initView(ViewGroup view) {
        /*不允许滑动*/
        turnOffScroll();
        toolbar.setTitleTextColor(0xffffffff);
        toolbar.setTitle("节点详情");
        toolbar.setCollapsible(true);

    }

    @Override
    public void initController() {
        addTab(R.mipmap.chats, R.mipmap.chats_green, "污染分时图", Color.GRAY, Color.parseColor("#04b00f"));
        addTab(R.mipmap.contacts, R.mipmap.contacts_green, "污染物组成", Color.GRAY, Color.parseColor("#04b00f"));
    }

    @Override
    public View getCustomContentView(View v) {
        ViewGroup vg = (ViewGroup) super.getCustomContentView(v);
        toolbar = (Toolbar) getLayoutInflater().inflate(R.layout.default_toolbar, null);
        vg.addView(toolbar, 0);
        return vg;
    }

    @Override
    public void onLoadingNetworkData() {
       Intent intent = getIntent();
        nodeid = intent.getStringExtra(DetailsFragment.NODE_MESSAGE);

    }

    @Override
    public void onLoadedNetworkData(View contentView) {

    }

    @Override
    public int getContentLayoutID() {
        return 0;
    }

    @Override
    public TabConfig getTabConfig() {
        return null;
    }


    /**
     * 48小时污染数值统计图
     */
    public static class PlaceholderFragment extends Fragment {

        private LineChartView chart;
        private PreviewLineChartView previewChart;
        private LineChartData data;
        /**
         * Deep copy of data.
         */
        private LineChartData previewData;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            setHasOptionsMenu(true);
            View rootView = inflater.inflate(R.layout.fragment_preview_line_chart, container, false);

            chart = (LineChartView) rootView.findViewById(R.id.chart);
            previewChart = (PreviewLineChartView) rootView.findViewById(R.id.chart_preview);

            // Generate data for previewed chart and copy of that data for preview chart.
            generateDefaultData();

            chart.setLineChartData(data);
            // Disable zoom/scroll for previewed chart, visible chart ranges depends on preview chart viewport so
            // zoom/scroll is unnecessary.
            chart.setZoomEnabled(false);
            chart.setScrollEnabled(false);

            previewChart.setLineChartData(previewData);
            previewChart.setViewportChangeListener(new ViewportListener());

            previewX(false);

            return rootView;
        }

        // MENU
        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.preview_line_chart, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_reset) {
                generateDefaultData();
                chart.setLineChartData(data);
                previewChart.setLineChartData(previewData);
                previewX(true);
                return true;
            }
            if (id == R.id.action_preview_both) {
                previewXY();
                previewChart.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
                return true;
            }
            if (id == R.id.action_preview_horizontal) {
                previewX(true);
                return true;
            }
            if (id == R.id.action_preview_vertical) {
                previewY();
                return true;
            }
            if (id == R.id.action_change_color) {
                int color = ChartUtils.pickColor();
                while (color == previewChart.getPreviewColor()) {
                    color = ChartUtils.pickColor();
                }
                previewChart.setPreviewColor(color);
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        private void generateDefaultData() {

            ACache mCache=ACache.get(getActivity());
            String getA="detailsA" + PreviewLineChartActivity.nodeid;
            String valuesA=mCache.getAsString(getA);
           // String valuesB=mCache.getAsString("detailsB"+PreviewLineChartActivity.nodeid);
            if(valuesA==null){
                Toast.makeText(getActivity(), "数据加载中，请稍后查看\n长时间无显示，请检查网络连接", Toast.LENGTH_SHORT).show();
                return;
            }
            String[] valueA=valuesA.split("&");

            List<PointValue> values = new ArrayList<PointValue>();
            for (int i = 0; i < valueA.length; ++i) {
                values.add(new PointValue(i, (float)Integer.parseInt(valueA[i])));
            }

            Line line = new Line(values);
            line.setColor(ChartUtils.COLOR_GREEN);
            line.setHasPoints(true);// too many values so don't draw points.

            List<Line> lines = new ArrayList<Line>();
            lines.add(line);

            data = new LineChartData(lines);
            data.setAxisXBottom(new Axis());
            data.setAxisYLeft(new Axis().setHasLines(true));

            // prepare preview data, is better to use separate deep copy for preview chart.
            // Set color to grey to make preview area more visible.
            previewData = new LineChartData(data);
            previewData.getLines().get(0).setColor(ChartUtils.DEFAULT_DARKEN_COLOR);

        }

        private void previewY() {
            Viewport tempViewport = new Viewport(chart.getMaximumViewport());
            float dy = tempViewport.height() / 4;
            tempViewport.inset(0, dy);
            previewChart.setCurrentViewportWithAnimation(tempViewport);
            previewChart.setZoomType(ZoomType.VERTICAL);
        }

        private void previewX(boolean animate) {
            Viewport tempViewport = new Viewport(chart.getMaximumViewport());
            float dx = tempViewport.width() / 4;
            tempViewport.inset(dx, 0);
            if (animate) {
                previewChart.setCurrentViewportWithAnimation(tempViewport);
            } else {
                previewChart.setCurrentViewport(tempViewport);
            }
            previewChart.setZoomType(ZoomType.HORIZONTAL);
        }

        private void previewXY() {
            // Better to not modify viewport of any chart directly so create a copy.
            Viewport tempViewport = new Viewport(chart.getMaximumViewport());
            // Make temp viewport smaller.
            float dx = tempViewport.width() / 4;
            float dy = tempViewport.height() / 4;
            tempViewport.inset(dx, dy);
            previewChart.setCurrentViewportWithAnimation(tempViewport);
        }

        /**
         * Viewport listener for preview chart(lower one). in {@link #onViewportChanged(Viewport)} method change
         * viewport of upper chart.
         */
        private class ViewportListener implements ViewportChangeListener {

            @Override
            public void onViewportChanged(Viewport newViewport) {
                // don't use animation, it is unnecessary when using preview chart.
                chart.setCurrentViewport(newViewport);
            }

        }

    }

    /**
     * 显示不同污染物数值变化关系图
     */
    public static class PlaceholderFragment2 extends Fragment {
        public final static String[] contents = new String[]{"PM2.5", "PM10", "NO2", "SO2"};

        public final static String[] times = new String[]{"-12小时", "-10小时", "-8小时", "-6小时", "-4小时", "-2小时", "现在",};

        private LineChartView chartTop;
        private ColumnChartView chartBottom;
        private String[] valueB;
        private LineChartData lineData;
        private ColumnChartData columnData;

        public PlaceholderFragment2() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_line_column_dependency, container, false);

            // *** TOP LINE CHART ***
            chartTop = (LineChartView) rootView.findViewById(R.id.chart_top);

            // Generate and set data for line chart
            generateInitialLineData();

            // *** BOTTOM COLUMN CHART ***

            chartBottom = (ColumnChartView) rootView.findViewById(R.id.chart_bottom);

            generateColumnData();

            return rootView;
        }

        private void generateColumnData() {

            int numSubcolumns = 1;
            int numColumns = contents.length;

            ACache mCache=ACache.get(getActivity());
            String getB="detailsB" + PreviewLineChartActivity.nodeid;
            String valuesB=mCache.getAsString(getB);
            if(valuesB==null){
                Toast.makeText(getActivity(), "数据加载中，请稍后查看\n长时间无显示，请检查网络连接", Toast.LENGTH_SHORT).show();
                return;
            }
            valueB=valuesB.split("&");

            List<AxisValue> axisValues = new ArrayList<AxisValue>();
            List<Column> columns = new ArrayList<Column>();
            List<SubcolumnValue> values;
            for (int i = 0; i < numColumns; ++i) {

                values = new ArrayList<SubcolumnValue>();
                for (int j = 0; j < numSubcolumns; ++j) {
                    values.add(new SubcolumnValue((float)Integer.parseInt(valueB[i]), ChartUtils.pickColor()));
                }

                axisValues.add(new AxisValue(i).setLabel(contents[i]));

                columns.add(new Column(values).setHasLabelsOnlyForSelected(true));
            }

            columnData = new ColumnChartData(columns);

            columnData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
            columnData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(2));

            chartBottom.setColumnChartData(columnData);

            // Set value touch listener that will trigger changes for chartTop.
            chartBottom.setOnValueTouchListener(new ValueTouchListener());

            // Set selection mode to keep selected month column highlighted.
            chartBottom.setValueSelectionEnabled(true);

            chartBottom.setZoomType(ZoomType.HORIZONTAL);

        }

        /**
         * Generates initial data for line chart. At the begining all Y values are equals 0. That will change when user
         * will select value on column chart.
         */
        private void generateInitialLineData() {
            int numValues = times.length;

            List<AxisValue> axisValues = new ArrayList<AxisValue>();
            List<PointValue> values = new ArrayList<PointValue>();
            for (int i = 0; i < numValues; ++i) {
                values.add(new PointValue(i, 0));
                axisValues.add(new AxisValue(i).setLabel(times[i]));
            }

            Line line = new Line(values);
            line.setColor(ChartUtils.COLOR_GREEN).setCubic(true);

            List<Line> lines = new ArrayList<Line>();
            lines.add(line);

            lineData = new LineChartData(lines);
            lineData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
            lineData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(3));

            chartTop.setLineChartData(lineData);

            // For build-up animation you have to disable viewport recalculation.
            chartTop.setViewportCalculationEnabled(false);

            // And set initial max viewport and current viewport- remember to set viewports after data.
            Viewport v = new Viewport(0, 250, 6, 0);
            chartTop.setMaximumViewport(v);
            chartTop.setCurrentViewport(v);

            chartTop.setZoomType(ZoomType.HORIZONTAL);
        }

        private void generateLineData(int color, int range) {
            // Cancel last animation if not finished.
            chartTop.cancelDataAnimation();

            // Modify data targets
            Line line = lineData.getLines().get(0);// For this example there is always only one line.
            line.setColor(color);
            /*从B列数据的第i个开始取，取满一个for则正好取满该污染物的一列数值*/
            int i=contents.length+7*range;
            for (PointValue value : line.getValues()) {
                // Change target only for Y value.
                int temp= Integer.parseInt(valueB[i++]);
                if(temp>251)
                    Toast.makeText(getActivity(), "爆表了！太可怕了！", Toast.LENGTH_SHORT).show();
                value.setTarget(value.getX(), (float)temp);
            }

            // Start new data animation with 300ms duration;
            chartTop.startDataAnimation(300);
        }

        private class ValueTouchListener implements ColumnChartOnValueSelectListener {

            @Override
            public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
                generateLineData(value.getColor(), columnIndex);
            }

            @Override
            public void onValueDeselected() {

                generateLineData(ChartUtils.COLOR_GREEN, 0);

            }
        }
    }
}
