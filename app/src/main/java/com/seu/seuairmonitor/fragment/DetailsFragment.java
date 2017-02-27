package com.seu.seuairmonitor.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.seu.seuairmonitor.MainActivity;
import com.kot32.ksimpleframeworklibrary.R;
import com.seu.seuairmonitor.refresh.PreviewLineChartActivity;
import com.kot32.ksimplelibrary.cache.ACache;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.view.AbstractChartView;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.PreviewLineChartView;


/**
 * Created by pc on 2016/2/4.
 */

    /**
     * A placeholder fragment containing a simple view.
     */
    public  class DetailsFragment extends Fragment implements OnItemClickListener {

        private ListView listView;
        private ChartSamplesAdapter adapter;
        private String checkLogin;
        public final static String NODE_MESSAGE = "detailsNodeId";

        public DetailsFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            listView = (ListView) rootView.findViewById(android.R.id.list);
            adapter = new ChartSamplesAdapter(getActivity(), 0, generateSamplesDescriptions());
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
            return rootView;
        }

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {

                if (checkLogin==null){
            Toast.makeText(getActivity(), "体验此项功能，请先注册并登录", Toast.LENGTH_SHORT).show();
            }else{
            if(position!=0){
            Intent intent;
            // Preview Line Chart;
            intent = new Intent(getActivity(), PreviewLineChartActivity.class);
                ACache mCache = ACache.get(DetailsFragment.super.getActivity());
                String nodeid = mCache.getAsString("list"+String.valueOf(position));
                intent.putExtra(NODE_MESSAGE,nodeid);
            startActivity(intent);

            }
            }

        }

        private List<ChartSampleDescription> generateSamplesDescriptions() {

            List<ChartSampleDescription> list = new ArrayList<ChartSampleDescription>();
            ACache mCache = ACache.get(DetailsFragment.super.getActivity());
            String subscribenodes = mCache.getAsString("subscribenodes");
            String nodeid = mCache.getAsString("nodeid");
            String nodecomment = mCache.getAsString("nodecomment");
            checkLogin = mCache.getAsString("username");
            if(nodecomment==null){
                if (checkLogin==null)
                {
                    list.add(new ChartSampleDescription("未获得权限", "请登录后查看", MainActivity.ChartType.OTHER));
                    return list;
                }else{
                    list.add(new ChartSampleDescription("正在加载网络数据", "请稍后查看", MainActivity.ChartType.OTHER));
                    return list;
                }
            }

            String[] node=subscribenodes.split("&");
            String[] nodecommen=nodecomment.split("&");
            String[] nodei=nodeid.split("&");
            int num=nodei.length;
            list.add(new ChartSampleDescription("共订阅"+num+"个节点", "点击节点查看详细数据", MainActivity.ChartType.OTHER));
            for(int i=0;i<num;i++)
            {
                list.add(new ChartSampleDescription(node[i], nodecommen[i], MainActivity.ChartType.PREVIEW_LINE_CHART));
                /*把节点号与list位置的关联存进缓存方便回调时找到相应节点*/
                mCache.put("list"+String.valueOf(i+1),nodei[i],ACache.TIME_HOUR);
            }

            return list;
        }
    }

    class ChartSamplesAdapter extends ArrayAdapter<ChartSampleDescription> {

        public ChartSamplesAdapter(Context context, int resource, List<ChartSampleDescription> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.list_item_sample, null);

                holder = new ViewHolder();
                holder.text1 = (TextView) convertView.findViewById(R.id.text1);
                holder.text2 = (TextView) convertView.findViewById(R.id.text2);
                holder.chartLayout = (FrameLayout) convertView.findViewById(R.id.chart_layout);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ChartSampleDescription item = getItem(position);

            holder.chartLayout.setVisibility(View.VISIBLE);
            holder.chartLayout.removeAllViews();
            AbstractChartView chart;
            switch (item.chartType) {
                case COLUMN_CHART:
                    chart = new ColumnChartView(getContext());
                    holder.chartLayout.addView(chart);
                    break;
                case PREVIEW_LINE_CHART:
                    chart = new PreviewLineChartView(getContext());
                    holder.chartLayout.addView(chart);
                    break;
                default:
                    chart = null;
                    holder.chartLayout.setVisibility(View.GONE);
                    break;
            }

            if (null != chart) {
                chart.setInteractive(false);// Disable touch handling for chart on the ListView.
            }
            holder.text1.setText(item.text1);
            holder.text2.setText(item.text2);

            return convertView;
        }

        private class ViewHolder {

            TextView text1;
            TextView text2;
            FrameLayout chartLayout;
        }

    }

     class ChartSampleDescription {
        String text1;
        String text2;
        MainActivity.ChartType chartType;

        public ChartSampleDescription(String text1, String text2, MainActivity.ChartType chartType) {
            this.text1 = text1;
            this.text2 = text2;
            this.chartType = chartType;
        }
    }




