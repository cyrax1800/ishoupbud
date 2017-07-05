package com.project.ishoupbud.view.fragment;

import android.graphics.Color;
import android.os.Bundle;
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
import com.project.ishoupbud.api.model.Product;
import com.project.ishoupbud.api.model.Statistic;
import com.project.ishoupbud.api.model.Vendor;
import com.project.ishoupbud.api.repositories.ProductRepo;
import com.project.michael.base.api.APICallback;
import com.project.michael.base.api.APIManager;
import com.project.michael.base.models.GenericResponse;
import com.project.michael.base.utils.DateUtils;
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

    HashMap<Integer, List<LineData>> statisticMapData;
    HashMap<Integer, List<List<String>>> statisticDateMapData;
    List<LineData> statisticDataList;
    List<List<String>> dateTextList;

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
                    if (statisticMapData.get(vendorId).get(selectedDayIdx) == null) {
                        getStatistic();
                    } else {
                        setChart();
                    }
                }
            });
            sgDayFilter.setPosition(0, 0);

            vendorNameList = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
            spinnerVendor.setAdapter(vendorNameList);
            spinnerVendor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedVendorIdx = position;
                    vendorId = vendorList.get(selectedVendorIdx).id;

                    if (statisticMapData.get(vendorId).get(selectedDayIdx) == null) {
                        getStatistic();
                    } else {
                        setChart();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        lineChart.setScaleEnabled(false);

        return _rootView;
    }

    public void initialRequest() {
        if (alreadyInitiateRequest) return;
        alreadyInitiateRequest = true;
        getStatistic();
    }

    public void getStatistic() {
        Call<GenericResponse<List<Statistic>>> getStatisticCall = APIManager.getRepository(ProductRepo.class)
                .getStatistic(productId, vendorList.get(selectedVendorIdx).id, day);
        getStatisticCall.enqueue(new APICallback<GenericResponse<List<Statistic>>>() {
            @Override
            public void onSuccess(Call<GenericResponse<List<Statistic>>> call, Response<GenericResponse<List<Statistic>>> response) {
                super.onSuccess(call, response);
//                if(response.body().data.size() == 0)return;;
                int vendorId = Integer.valueOf(call.request().url().queryParameter("vendor_id"));
                int range = Integer.valueOf(call.request().url().queryParameter("range"));
                int rangeIdx = 0;
                if (range == 7) rangeIdx = 0;
                else if (range == 30) rangeIdx = 1;
                else if (range == 90) rangeIdx = 2;
                List<Entry> statisticData = new ArrayList<>();
                List<String> dateList = new ArrayList<>();
                if(rangeIdx == 0){
                    for (int i = 0; i < response.body().data.size(); i++) {
                        dateList.add(DateUtils.getDate(response.body().data.get(i).date.getTime()));
                        statisticData.add(new Entry(i, response.body().data.get(i).value));
                    }
                }else if(rangeIdx == 1){
                    int count = response.body().data.size();
                    int start = 0;
                    dateList.add(DateUtils.getDate(response.body().data.get(start).date.getTime()));
                    statisticData.add(new Entry(0, response.body().data.get(start).value));
                    if(start + 7 < count){
                        start = start +7;
                        dateList.add(DateUtils.getDate(response.body().data.get(start).date.getTime()));
                        statisticData.add(new Entry(1, response.body().data.get(start).value));
                    }
                    if(start + 7 < count){
                        start = start + 7;
                        dateList.add(DateUtils.getDate(response.body().data.get(start).date.getTime()));
                        statisticData.add(new Entry(2, response.body().data.get(start).value));
                    }
                    if(start + 7 < count){
                        start = start + 7;
                        dateList.add(DateUtils.getDate(response.body().data.get(start).date.getTime()));
                        statisticData.add(new Entry(3, response.body().data.get(start).value));
                    }
                }else if(rangeIdx == 2){
                    for (int i = 0; i < response.body().data.size(); i++) {
                        if(dateList.indexOf(DateUtils.getMonth(
                                response.body().data.get(i).date.getMonth())) > -1) continue;
                        dateList.add(DateUtils.getMonth(response.body().data.get(i).date.getMonth()));
                        statisticData.add(new Entry(i, response.body().data.get(i).value));
                    }
                    if(dateList.size() > 3){
                        while (dateList.size() > 3){
                            dateList.remove(dateList.size()-1);
                            statisticData.remove(statisticData.size()-1);
                        }
                    }
                }
                LineDataSet lineDataSet = new LineDataSet(statisticData, "harga");
                lineDataSet.setCircleRadius(5f);
                lineDataSet.setCircleColor(Color.RED);
                lineDataSet.setLineWidth(2f);
                lineDataSet.setValueTextSize(10f);
                lineDataSet.setColor(Color.RED);
                LineData lineData = new LineData(lineDataSet);
                statisticMapData.get(vendorId).set(rangeIdx, lineData);
                statisticDateMapData.get(vendorId).set(rangeIdx, dateList);
                if (selectedDayIdx == rangeIdx && vendorList.get(selectedVendorIdx).id == vendorId) {
                    setChart();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Statistic>>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    public void setChart() {
        if(statisticMapData.get(vendorList.get(selectedVendorIdx).id).get(selectedDayIdx) == null) return;
        if(statisticDateMapData.get(vendorList.get(selectedVendorIdx).id).get(selectedDayIdx).size() == 0) return;
        lineChart.setData(statisticMapData.get(vendorList.get(selectedVendorIdx).id).get(selectedDayIdx));
        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
//                Log.d(TAG, "getFormattedValue: "+value);
                if((int)value > statisticDateMapData.get(vendorList.get(selectedVendorIdx).id)
                        .get(selectedDayIdx).size() - 1){
                    value = statisticDateMapData.get(vendorList.get(selectedVendorIdx).id)
                            .get(selectedDayIdx).size() - 1;
                }
                if(value == -1){
                    value = 0;
                }
                if(selectedDayIdx == 0){
                    if(value % 3 != 0){
                        return "";
                    }
                }
                return statisticDateMapData
                        .get(vendorList.get(selectedVendorIdx).id)
                        .get(selectedDayIdx)
                        .get((int)value);
            }
        };
//        Legend legend = lineChart.getLegend();
//        legend.setFormSize(10f);
//        legend.setExtra(new int[]{Color.RED}, new String[]{"harga"});
//        List<LegendEntry> legendEntries = new ArrayList<>();
//        legendEntries.add(new LegendEntry("harga",));
//        legendEntries.get(0).label = "Harga";
//        legend.setCustom();
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);
        Description description = lineChart.getDescription();
        description.setText(vendorList.get(selectedVendorIdx).name);
        description.setTextSize(10f);
        lineChart.setDescription(description);
        lineChart.invalidate();
        lineChart.notifyDataSetChanged();
    }

    public void createBlankData() {
        statisticMapData = new HashMap<>();
        statisticDateMapData = new HashMap<>();
        int vendorId;
        List<LineData> tmpStatisticDataList;
        List<List<String>> tmpDateTextList;
        for (int i = 0; i < vendorList.size(); i++) {
            vendorId = vendorList.get(i).id;
            tmpStatisticDataList = new ArrayList<>(3);
            tmpDateTextList = new ArrayList<>(3);
            for (int j = 0; j < 3; j++) {
                tmpStatisticDataList.add(null);
                tmpDateTextList.add(null);
            }
            statisticMapData.put(vendorId, tmpStatisticDataList);
            statisticDateMapData.put(vendorId, tmpDateTextList);
        }
    }

    public void setProductId(int id) {
        this.productId = id;
    }

    public void setVendor(List<Vendor> vendors) {
        this.vendorList = vendors;
        vendorNameList.clear();
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
