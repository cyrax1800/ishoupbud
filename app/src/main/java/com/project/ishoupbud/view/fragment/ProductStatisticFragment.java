package com.project.ishoupbud.view.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.DataSet;
import com.project.ishoupbud.api.model.Product;
import com.project.ishoupbud.api.model.Statistic;
import com.project.ishoupbud.api.model.Vendor;
import com.project.ishoupbud.api.repositories.ProductRepo;
import com.project.michael.base.api.APICallback;
import com.project.michael.base.api.APIManager;
import com.project.michael.base.models.GenericResponse;
import com.project.michael.base.utils.DateUtils;
import com.project.michael.base.utils.Utils;
import com.project.michael.base.views.BaseFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.ceryle.segmentedbutton.SegmentedButtonGroup;
import info.hoang8f.android.segmented.SegmentedGroup;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by michael on 4/10/17.
 */

public class ProductStatisticFragment extends BaseFragment {

    @BindView(R.id.spinner_vendor)
    Spinner spinnerVendor;
    @BindView(R.id.segmented2)
    SegmentedButtonGroup sgDayFilter;
    @BindView(R.id.chart)
    LineChart lineChart;

    public int productId;
    public List<Vendor> vendorList;
    private ArrayAdapter<String> vendorNameList;

    boolean alreadyInitiateRequest;
    int selectedVendorIdx;
    int vendorId;
    int day;
    int selectedDayIdx;

    HashMap<Integer, LineData> statisticMapData;
    HashMap<Integer, List<String>> statisticDateMapData;

    int[] colors = new int[]{
            Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.BLACK
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (this._rootView == null) {
            _rootView = inflater.inflate(R.layout.fragment_history_product, container, false);

            ButterKnife.bind(this, _rootView);

            selectedVendorIdx = 0;
            alreadyInitiateRequest = false;
            day = 7;
            selectedDayIdx = 0;
            vendorId = 0;

//            sgDayFilter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(RadioGroup group, int checkedId) {
//
//                }
//            });

            sgDayFilter.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition() {
                @Override
                public void onClickedButtonPosition(int position) {
                    selectedDayIdx = position;
                    if (position == 0) day = 7;
                    if (position == 1) day = 30;
                    if (position == 2) day = 90;
                    if(statisticMapData.get(selectedDayIdx) == null){
                        getStatistic();
                    }else{
                        setChart();
                    }
//                    if (statisticMapData.get(vendorId).get(selectedDayIdx) == null) {
//                        getStatistic();
//                    } else {
//                        setChart();
//                    }
                }
            });
            sgDayFilter.setPosition(0, 0);

            vendorNameList = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
            spinnerVendor.setAdapter(vendorNameList);
            spinnerVendor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedVendorIdx = position;

                    if(statisticMapData.get(selectedDayIdx) == null){
                        getStatistic();
                    }else{
                        setChart();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        lineChart.setScaleEnabled(false);
        lineChart.getAxisLeft().setDrawLabels(false);
        lineChart.getAxisRight().setDrawLabels(false);
        Description description = lineChart.getDescription();
        description.setText("");
        Legend l = lineChart.getLegend();
        l.setWordWrapEnabled(true);

        return _rootView;
    }

    public void initialRequest() {
        if (alreadyInitiateRequest) return;
        alreadyInitiateRequest = true;
        getStatistic();
    }

    public void getStatistic() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("product_id", productId);
        map.put("vendor_id","");
        map.put("range", day);
        Call<Statistic> getStatisticCall = APIManager.getRepository(ProductRepo.class)
                .getStatistic(map);
        getStatisticCall.enqueue(new APICallback<Statistic>() {
            @Override
            public void onSuccess(Call<Statistic> call, Response<Statistic> response) {
                super.onSuccess(call, response);
                List<DataSet> dataSets = response.body().datasets;
                int range = Integer.valueOf(call.request().url().queryParameter("range"));
                int rangeIdx = 0;
                if (range == 7) rangeIdx = 0;
                else if (range == 30) rangeIdx = 1;
                else if (range == 90) rangeIdx = 2;
                int totalDataSet = dataSets.size();
                List<String> dateList = new ArrayList<String>();
                List<Entry> statisticData;
                LineData lineData = new LineData();
                for(int i = 0; i< totalDataSet; i++){
                    statisticData = new ArrayList<>();
                    if(rangeIdx == 1){
                        List<String> tmpDateList = new ArrayList<String>();
                        int start = dataSets.get(i).data.size() - 1;
                        tmpDateList.add(response.body().labels.get(start));
                        statisticData.add(new Entry(0, dataSets.get(i).data.get(start)));
                        if(start - 7 > 0){
                            start = start - 7;
                            tmpDateList.add(response.body().labels.get(start));
                            statisticData.add(new Entry(1, dataSets.get(i).data.get(start)));
                        }
                        if(start - 7 > 0){
                            start = start - 7;
                            tmpDateList.add(response.body().labels.get(start));
                            statisticData.add(new Entry(2, dataSets.get(i).data.get(start)));
                        }
                        if(start - 7 > 0){
                            start = start - 7;
                            tmpDateList.add(response.body().labels.get(start));
                            statisticData.add(new Entry(3, dataSets.get(i).data.get(start)));
                        }
                        if(dateList.size() < tmpDateList.size()){
                            dateList = tmpDateList;
                        }
                    }else if(rangeIdx == 2){
                        dateList = new ArrayList<String>();
                        int start = response.body().labels.size() - 1;
                        for (int j = start - 1; j > -1; j--) {
                            if(dateList.indexOf(DateUtils.getMonth(Integer.parseInt(response.body().labels
                                    .get(i).split("/")[1]) - 1)) > -1 ) continue;
                            dateList.add(DateUtils.getMonth(Integer.parseInt(response.body().labels
                                            .get(i).split("/")[1]) - 1));
                            statisticData.add(new Entry(dateList.size() - 1, response.body().datasets
                                    .get(i).data.get(j) - 1));
                        }
                        if(dateList.size() > 3){
                            while (dateList.size() > 3){
                                dateList.remove(dateList.size()-1);
                                statisticData.remove(statisticData.size()-1);
                            }
                        }
                    }else{
                        dateList = response.body().labels;
                        for (int j = 0; j < dataSets.get(i).data.size(); j++) {
                            statisticData.add(
                                    new Entry(j, dataSets.get(i).data.get(j)));
                        }
                    }
                    LineDataSet lineDataSet = new LineDataSet(statisticData, dataSets.get(i).label);
                    lineDataSet.setCircleRadius(2f + ((totalDataSet-i)));
                    lineDataSet.setLineWidth(1f + (totalDataSet-i));
                    lineDataSet.setValueTextSize(10f);
                    int color = colors[i % totalDataSet];
                    lineDataSet.setColors(color);
                    lineDataSet.setCircleColors(color);
                    lineData.addDataSet(lineDataSet);
                }
                statisticMapData.put(rangeIdx, lineData);
                statisticDateMapData.put(rangeIdx, dateList);
                setChart();
//                statisticMapData.get(vendorId).set(rangeIdx, lineData);
            }

            @Override
            public void onFailure(Call<Statistic> call, Throwable t) {
                super.onFailure(call, t);
            }

            @Override
            public void onError(Call<Statistic> call, Response<Statistic> response) {
                super.onError(call, response);
            }
        });
//        Call<GenericResponse<List<Statistic>>> getStatisticCall = APIManager.getRepository(ProductRepo.class)
//                .getStatistic(productId, vendorList.get(selectedVendorIdx).id, day);
//        getStatisticCall.enqueue(new APICallback<GenericResponse<List<Statistic>>>() {
//            @Override
//            public void onSuccess(Call<GenericResponse<List<Statistic>>> call, Response<GenericResponse<List<Statistic>>> response) {
//                super.onSuccess(call, response);
//                int vendorId = Integer.valueOf(call.request().url().queryParameter("vendor_id"));
//                int range = Integer.valueOf(call.request().url().queryParameter("range"));
//                int rangeIdx = 0;
//                if (range == 7) rangeIdx = 0;
//                else if (range == 30) rangeIdx = 1;
//                else if (range == 90) rangeIdx = 2;
//                List<Entry> statisticData = new ArrayList<>();
//                List<String> dateList = new ArrayList<>();
//                if(rangeIdx == 0){
//                    for (int i = 0; i < response.body().data.size(); i++) {
//                        dateList.add(DateUtils.getDate(response.body().data.get(i).date.getTime()));
//                        statisticData.add(new Entry(i, response.body().data.get(i).value));
//                    }
//                }else if(rangeIdx == 1){
//                    int count = response.body().data.size();
//                    int start = 0;
//                    dateList.add(DateUtils.getDate(response.body().data.get(start).date.getTime()));
//                    statisticData.add(new Entry(0, response.body().data.get(start).value));
//                    if(start + 7 < count){
//                        start = start +7;
//                        dateList.add(DateUtils.getDate(response.body().data.get(start).date.getTime()));
//                        statisticData.add(new Entry(1, response.body().data.get(start).value));
//                    }
//                    if(start + 7 < count){
//                        start = start + 7;
//                        dateList.add(DateUtils.getDate(response.body().data.get(start).date.getTime()));
//                        statisticData.add(new Entry(2, response.body().data.get(start).value));
//                    }
//                    if(start + 7 < count){
//                        start = start + 7;
//                        dateList.add(DateUtils.getDate(response.body().data.get(start).date.getTime()));
//                        statisticData.add(new Entry(3, response.body().data.get(start).value));
//                    }
//                }else if(rangeIdx == 2){
//                    for (int i = 0; i < response.body().data.size(); i++) {
//                        if(dateList.indexOf(DateUtils.getMonth(
//                                response.body().data.get(i).date.getMonth())) > -1) continue;
//                        dateList.add(DateUtils.getMonth(response.body().data.get(i).date.getMonth()));
//                        statisticData.add(new Entry(dateList.size() - 1, response.body().data.get(i)
//                                .value));
//                    }
//                    if(dateList.size() > 3){
//                        while (dateList.size() > 3){
//                            dateList.remove(dateList.size()-1);
//                            statisticData.remove(statisticData.size()-1);
//                        }
//                    }
//                }
//                LineDataSet lineDataSet = new LineDataSet(statisticData, "harga");
//                lineDataSet.setCircleRadius(5f);
//                lineDataSet.setCircleColor(Color.RED);
//                lineDataSet.setLineWidth(2f);
//                lineDataSet.setValueTextSize(10f);
//                lineDataSet.setColor(Color.RED);
//                LineData lineData = new LineData(lineDataSet);
//                statisticMapData.get(vendorId).set(rangeIdx, lineData);
//                statisticDateMapData.get(vendorId).set(rangeIdx, dateList);
//                if (selectedDayIdx == rangeIdx && vendorList.get(selectedVendorIdx).id == vendorId) {
//                    setChart();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GenericResponse<List<Statistic>>> call, Throwable t) {
//                super.onFailure(call, t);
//            }
//        });
    }

    public void setChart() {
        if(statisticMapData.get(selectedDayIdx) == null) return;
        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if((int)value >= statisticDateMapData.get(selectedDayIdx).size()){
                    value = statisticDateMapData.get(selectedDayIdx).size() - 1;
                }
                if(value == -1){
                    value = 0;
                }
                if(selectedDayIdx == 0){
                    if(value % 3 != 0){
                        return "";
                    }
                }
                Log.d(TAG, "getFormattedValue: " + statisticDateMapData.get(selectedDayIdx).get((int)value));
                return statisticDateMapData.get(selectedDayIdx).get((int)value);
            }
        };
        LineData tmpLineData = statisticMapData.get(selectedDayIdx);
        if(selectedVendorIdx > 0){
            String vendorName = vendorList.get(selectedVendorIdx - 1).name;
            for(int i = 0; i< statisticMapData.get(selectedDayIdx).getDataSetCount(); i++){
                if(vendorName.equals(statisticMapData.get(selectedDayIdx).getDataSetByIndex(i).getLabel())){
                    tmpLineData = new LineData(statisticMapData.get(selectedDayIdx)
                            .getDataSetByIndex(i));
                    break;
                }
            }
        }

        lineChart.setData(tmpLineData);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();

//        if(statisticMapData.get(vendorList.get(selectedVendorIdx).id).get(selectedDayIdx) == null) return;
//        if(statisticDateMapData.get(vendorList.get(selectedVendorIdx).id).get(selectedDayIdx).size() == 0) return;
//        lineChart.setData(statisticMapData.get(vendorList.get(selectedVendorIdx).id).get(selectedDayIdx));
//        IAxisValueFormatter formatter = new IAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
////                Log.d(TAG, "getFormattedValue: "+value);
//                if((int)value > statisticDateMapData.get(vendorList.get(selectedVendorIdx).id)
//                        .get(selectedDayIdx).size() - 1){
//                    value = statisticDateMapData.get(vendorList.get(selectedVendorIdx).id)
//                            .get(selectedDayIdx).size() - 1;
//                }
//                if(value == -1){
//                    value = 0;
//                }
//                if(selectedDayIdx == 0){
//                    if(value % 3 != 0){
//                        return "";
//                    }
//                }
//                return statisticDateMapData
//                        .get(vendorList.get(selectedVendorIdx).id)
//                        .get(selectedDayIdx)
//                        .get((int)value);
//            }
//        };
//        XAxis xAxis = lineChart.getXAxis();
//        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
//        xAxis.setValueFormatter(formatter);
//        Description description = lineChart.getDescription();
//        description.setText(vendorList.get(selectedVendorIdx).name);
//        description.setTextSize(10f);
//        lineChart.setDescription(description);
//        lineChart.invalidate();
//        lineChart.notifyDataSetChanged();
    }

    public void createBlankData() {
        statisticMapData = new HashMap<>();
        statisticDateMapData = new HashMap<>();
//        int vendorId = 0;
//        List<LineData> tmpStatisticDataList;
//        List<List<String>> tmpDateTextList;
//        tmpStatisticDataList = new ArrayList<>(3);
//        tmpDateTextList = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            statisticMapData.put(i, null);
            statisticDateMapData.put(i, null);
        }
//        statisticMapData.put(vendorId, tmpStatisticDataList);
//        statisticDateMapData.put(vendorId, tmpDateTextList);
//        for (int i = 0; i < vendorList.size(); i++) {
//            vendorId = vendorList.get(i).id;
//            tmpStatisticDataList = new ArrayList<>(3);
//            tmpDateTextList = new ArrayList<>(3);
//            for (int j = 0; j < 3; j++) {
//                tmpStatisticDataList.add(null);
//                tmpDateTextList.add(null);
//            }
//            statisticMapData.put(vendorId, tmpStatisticDataList);
//            statisticDateMapData.put(vendorId, tmpDateTextList);
//        }
    }

    public void setProductId(int id) {
        this.productId = id;
    }

    public void setVendor(List<Vendor> vendors) {
        this.vendorList = vendors;
        vendorNameList.clear();
        vendorNameList.add("All Vendors");
        for (int i = 0; i < vendors.size(); i++) {
            vendorNameList.add(vendors.get(i).name);
        }
        vendorNameList.notifyDataSetChanged();
        vendorId = vendorList.get(0).id;
        createBlankData();
    }

    public static ProductStatisticFragment newInstance() {

        Bundle args = new Bundle();

        ProductStatisticFragment fragment = new ProductStatisticFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
