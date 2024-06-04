package com.example.iscg7427groupmobileapp.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iscg7427groupmobileapp.R;
import com.example.iscg7427groupmobileapp.RecyclerAdapterTransaction;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class UserDashboardActivity_1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard_1);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecyclerAdapterTransaction());

        Spinner spinner = findViewById(R.id.spinner);
        String[] options = {"Option 1", "Option 2", "Option 3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                // 执行相应操作
                if(selectedItem.equals("Option 1")){
                    Toast.makeText(UserDashboardActivity_1.this, "Option 1 selected", Toast.LENGTH_SHORT).show();
                }

                else if(selectedItem.equals("Option 2")){
                    Toast.makeText(UserDashboardActivity_1.this, "Option 2 selected", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(UserDashboardActivity_1.this, "Option 3 selected", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 未选择任何项目时的逻辑
            }
        });

        //grouped barchart
        BarChart barChart = findViewById(R.id.chart);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // 设置 X 轴位置在底部
        xAxis.setDrawGridLines(false); // 不绘制网格线
        xAxis.setDrawAxisLine(true); // 绘制轴线
        xAxis.setAxisLineWidth(1f); // 设置轴线宽度
        xAxis.setAxisLineColor(Color.BLACK); // 设置轴线颜色为白色
        xAxis.setDrawLabels(false); // 不显示 X 轴标签
        //     xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"A", "B", "C"}));

        YAxis leftAxis = barChart.getAxisLeft();
        //      leftAxis.setDrawGridLines(false); // 不绘制网格线
        //     leftAxis.setDrawAxisLine(false); // 不绘制轴线
        //   leftAxis.setAxisMinimum(0f); // 设置 Y 轴最小值为 0
        leftAxis.setEnabled(false); // 禁用左侧 Y 轴

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false); // 禁用右侧 Y 轴

        List<BarEntry> entriesGroup1 = new ArrayList<>();
        List<BarEntry> entriesGroup2 = new ArrayList<>();

        entriesGroup1.add(new BarEntry(0f, 30f));
        entriesGroup1.add(new BarEntry(1f, 80f));
        entriesGroup1.add(new BarEntry(2f, 60f));

        entriesGroup2.add(new BarEntry(0f, 60f));
        entriesGroup2.add(new BarEntry(1f, 70f));
        entriesGroup2.add(new BarEntry(2f, 80f));

        BarDataSet set1 = new BarDataSet(entriesGroup1, "Group 1");
        BarDataSet set2 = new BarDataSet(entriesGroup2, "Group 2");
        set1.setColor(Color.parseColor("#88BBD8")); // 设置 Group 1 的柱形颜色为蓝色
        set2.setColor(Color.parseColor("#EB5F2A")); // 设置 Group 2 的柱形颜色为红色

        set1.setDrawValues(false); // 不显示 Group 1 的值
        set2.setDrawValues(false); // 不显示 Group 2 的值

        float groupSpace = 0.3f; // 增大组间距
        float barSpace = 0.05f; // 增大柱间距
        float barWidth = 0.15f; // 减小柱形宽度
        float startValue = 0f - (barWidth + barSpace + groupSpace) / 2f; // 调整起始位置

        barChart.setDescription(null);

        BarData data = new BarData(set1, set2);
        data.setBarWidth(barWidth);

        barChart.getLegend().setEnabled(false); // 隐藏图例

        barChart.setData(data);
        barChart.groupBars(startValue, groupSpace, barSpace);
        barChart.invalidate();

        NavigationBarView nav = findViewById(R.id.bottom_navigation);

        NavigationBarView.OnItemSelectedListener listener = new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.item_home){

                    // type code here!
                    Toast.makeText(UserDashboardActivity_1.this, "Item 1 Clicked", Toast.LENGTH_SHORT).show();
                    return true;
                }

                else if (item.getItemId() == R.id.item_income){

                    //type code here!
                    Toast.makeText(UserDashboardActivity_1.this, "Item 2 Clicked", Toast.LENGTH_SHORT).show();
                    return true;
                }

                    else if (item.getItemId() == R.id.item_expenses){

                        //type code here!
                        Toast.makeText(UserDashboardActivity_1.this, "Item 3 Clicked", Toast.LENGTH_SHORT).show();
                        return true;
                    }

                    else if (item.getItemId() == R.id.item_profile){

                        //type code here!
                        Toast.makeText(UserDashboardActivity_1.this, "Item 4 Clicked", Toast.LENGTH_SHORT).show();
                        return true;
                    }

                else return false;
            }
        };

        nav.setOnItemSelectedListener(listener);

    }

    }
