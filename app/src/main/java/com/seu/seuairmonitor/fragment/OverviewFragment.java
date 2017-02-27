package com.seu.seuairmonitor.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kot32.ksimpleframeworklibrary.R;
import com.kot32.ksimplelibrary.cache.ACache;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.Chart;
import lecho.lib.hellocharts.view.ColumnChartView;


    /**
     * A fragment containing a column chart.
     */
    public class OverviewFragment extends Fragment {

        private static final int DEFAULT_DATA = 0;
        private static final int STACKED_DATA = 2;


        private ColumnChartView chart;
        private ColumnChartData data;
        private boolean hasAxes = true;
        private boolean hasAxesNames = true;
        private boolean hasLabels = true;
        private boolean hasLabelForSelected = false;
        private int dataType = DEFAULT_DATA;

        public OverviewFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            setHasOptionsMenu(true);
            View rootView = inflater.inflate(R.layout.fragment_column_chart, container, false);

            chart = (ColumnChartView) rootView.findViewById(R.id.chart);
            chart.setOnValueTouchListener(new ValueTouchListener());

            generateData();

            return rootView;
        }

        // MENU
        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.column_chart, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_reset) {
                reset();
                generateData();
                return true;
            }
            if (id == R.id.action_stacked) {
                dataType = STACKED_DATA;
                generateData();
                return true;
            }
            if (id == R.id.action_toggle_labels) {
                toggleLabels();
                return true;
            }
            if (id == R.id.action_toggle_axes) {
                toggleAxes();
                return true;
            }
            if (id == R.id.action_animate) {
                prepareDataAnimation();
                chart.startDataAnimation();
                return true;
            }
            if (id == R.id.action_toggle_selection_mode) {
                toggleLabelForSelected();

                Toast.makeText(getActivity(),
                        "标签选择模式已" + (chart.isValueSelectionEnabled()?"开启":"关闭"),
                        Toast.LENGTH_SHORT).show();
                return true;
            }
            if (id == R.id.action_toggle_touch_zoom) {
                chart.setZoomEnabled(!chart.isZoomEnabled());
                Toast.makeText(getActivity(), "手动缩放模式已" + (chart.isZoomEnabled()?"开启":"关闭"), Toast.LENGTH_SHORT).show();
                return true;
            }
            if (id == R.id.action_zoom_both) {
                chart.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
                return true;
            }
            if (id == R.id.action_zoom_horizontal) {
                chart.setZoomType(ZoomType.HORIZONTAL);
                return true;
            }
            if (id == R.id.action_zoom_vertical) {
                chart.setZoomType(ZoomType.VERTICAL);
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        private void reset() {
            generateDefaultData();
            Toast.makeText(getActivity(), "刷新完成！服务器数据每小时更新一次！" , Toast.LENGTH_SHORT).show();
            hasAxes = true;
            hasAxesNames = true;
            hasLabels = true;
            hasLabelForSelected = false;
            dataType = DEFAULT_DATA;
            chart.setValueSelectionEnabled(hasLabelForSelected);

        }

        private void generateDefaultData() {
            /*从缓存中取出公共数据*/
            ACache mCache = ACache.get(OverviewFragment.super.getActivity());/*检查缓存中是否有用户名密码*/
            String nodes = mCache.getAsString("nodes");
            String publicdatas = mCache.getAsString("publicdata");
            if(nodes==null){

                return;}
            String[] node=nodes.split("&");
            String[] publicdata=publicdatas.split("&");

      //     int numSubcolumns = 1;
        //    int numColumns = 8;
            // Column can have many subcolumns, here by default I use 1 subcolumn in each of 8 columns.

            List<AxisValue> axisValues = new ArrayList<AxisValue>();

            List<Column> columns = new ArrayList<Column>();
            List<SubcolumnValue> values;
            for (int i = 0; i < node.length; ++i) {
                values = new ArrayList<SubcolumnValue>();
                for (int j = 0; j < 1; ++j) {
                    //int x=ChartUtils.pickColor();
                    SubcolumnValue subclumn=new SubcolumnValue(Integer.parseInt(publicdata[i]), judgeColorLevel(Integer.parseInt(publicdata[i])));
                    subclumn.setLabel(publicdata[i]);
                    values.add(subclumn);
                }

                axisValues.add(new AxisValue(i).setLabel(node[i]));

                Column column = new Column(values);
                column.setHasLabels(hasLabels);
                column.setHasLabelsOnlyForSelected(hasLabelForSelected);
                columns.add(column);
            }

            data = new ColumnChartData(columns);
          //  data.setAxisXBottom(new Axis(axisValues).setHasLines(true));

            if (hasAxes) {
                Axis axisX = new Axis(axisValues).setHasLines(true);
                Axis axisY = new Axis().setHasLines(true);
                if (hasAxesNames) {
                    axisX.setName("采样地区");
                    axisY.setName("污染数值");
                }
                data.setAxisXBottom(axisX);
                data.setAxisYLeft(axisY);
            } else {
                data.setAxisXBottom(null);
                data.setAxisYLeft(null);
            }

            chart.setColumnChartData(data);

        }

        /*根据污染程度判断统计条应该有的颜色*/
        private int judgeColorLevel(int value) {

            if (value>0&&value<=50)
                return ChartUtils.COLOR_GREEN;
            else if (value>50&&value<=80)
                return ChartUtils.COLOR_BLUE;
            else if (value>80&&value<=100)
                return ChartUtils.COLOR_VIOLET;
            else if (value>100&&value<=150)
                return ChartUtils.COLOR_ORANGE;
            else
            return ChartUtils.COLOR_RED;
        }


        /**
         * Generates columns with stacked subcolumns.
         */
        private void generateStackedData() {
            int numSubcolumns = 4;
            int numColumns = 8;
            // Column can have many stacked subcolumns, here I use 4 stacke subcolumn in each of 4 columns.
            List<Column> columns = new ArrayList<Column>();
            List<SubcolumnValue> values;
            for (int i = 0; i < numColumns; ++i) {

                values = new ArrayList<SubcolumnValue>();
                for (int j = 0; j < numSubcolumns; ++j) {
                    values.add(new SubcolumnValue((float) Math.random() * 20f + 5, ChartUtils.pickColor()));
                }


                Column column = new Column(values);
                column.setHasLabels(hasLabels);
                column.setHasLabelsOnlyForSelected(hasLabelForSelected);
                columns.add(column);
            }

            data = new ColumnChartData(columns);

            // Set stacked flag.
            data.setStacked(true);

            if (hasAxes) {
                Axis axisX = new Axis();
                Axis axisY = new Axis().setHasLines(true);
                if (hasAxesNames) {
                    axisX.setName("Axis X");
                    axisY.setName("Axis Y");
                }
                data.setAxisXBottom(axisX);
                data.setAxisYLeft(axisY);
            } else {
                data.setAxisXBottom(null);
                data.setAxisYLeft(null);
            }

            chart.setColumnChartData(data);
        }



        private int getSign() {
            int[] sign = new int[]{-1, 1};
            return sign[Math.round((float) Math.random())];
        }

        private void generateData() {
            switch (dataType) {
                case DEFAULT_DATA:
                    generateDefaultData();
                    break;
                case STACKED_DATA:
                    generateStackedData();
                    break;
                default:
                    generateDefaultData();
                    break;
            }
        }

        private void toggleLabels() {
            hasLabels = !hasLabels;

            if (hasLabels) {
                hasLabelForSelected = false;
                chart.setValueSelectionEnabled(hasLabelForSelected);
            }

            generateData();
        }

        private void toggleLabelForSelected() {
            hasLabelForSelected = !hasLabelForSelected;
            chart.setValueSelectionEnabled(hasLabelForSelected);

            if (hasLabelForSelected) {
                hasLabels = false;
            }

            generateData();
        }

        private void toggleAxes() {
            hasAxes = !hasAxes;

            generateData();
        }

        private void toggleAxesNames() {
            hasAxesNames = !hasAxesNames;

            generateData();
        }

        /**
         * To animate values you have to change targets values and then call {@link Chart#startDataAnimation()}
         * method(don't confuse with View.animate()).
         */
        private void prepareDataAnimation() {
            for (Column column : data.getColumns()) {
                for (SubcolumnValue value : column.getValues()) {
                    value.setTarget((float) Math.random() * 100);
                }
            }
        }

        private class ValueTouchListener implements ColumnChartOnValueSelectListener {

            @Override
            public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
                if(value.getValue()>=0.0&&value.getValue()<=5.0)
                {Toast.makeText(getActivity(), "数据下载失败，请在右上角菜单刷新\n长时间无显示，请检查网络连接", Toast.LENGTH_SHORT).show();
                return;}
                if(value.getValue()>=10&&value.getValue()<=50)
                Toast.makeText(getActivity(), "非常好！快出去运动吧！", Toast.LENGTH_SHORT).show();
                if(value.getValue()>50&&value.getValue()<=100)
                    Toast.makeText(getActivity(), "虽然不优秀，倒也人畜无害", Toast.LENGTH_SHORT).show();
                if(value.getValue()>100&&value.getValue()<=150)
                    Toast.makeText(getActivity(), "带个口罩什么的么么哒", Toast.LENGTH_SHORT).show();
                if(value.getValue()>150&&value.getValue()<=200)
                    Toast.makeText(getActivity(), "最好不要进行户外剧烈运动", Toast.LENGTH_SHORT).show();
                if(value.getValue()>200&&value.getValue()<=250)
                    Toast.makeText(getActivity(), "外出可以佩戴防毒面具", Toast.LENGTH_SHORT).show();
                if(value.getValue()>250)
                    Toast.makeText(getActivity(), "有毒！！！有毒！！！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onValueDeselected() {
                // TODO Auto-generated method stub

            }

        }

    }

