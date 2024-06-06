package com.example.iscg7427groupmobileapp.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iscg7427groupmobileapp.Model.User;
import com.example.iscg7427groupmobileapp.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserIncomeDashboardActivity extends AppCompatActivity {

    ImageButton btn_add;
    Spinner spinner;
    TextView txtViewAll;
    RecyclerView recyclerView;
    String uid;
    NavigationBarView nav;
    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_income_dashboard);

        init();

        btn_add.setOnClickListener(v -> {
            Intent intent = new Intent(UserIncomeDashboardActivity.this, UserAddIncomeActivity.class);
            startActivity(intent);
        });

        txtViewAll.setOnClickListener(v -> {

        });

        NavigationBarView.OnItemSelectedListener listener = new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.item_home) {

                    return true;
                } else if (item.getItemId() == R.id.item_income) {

                    return true;
                } else if (item.getItemId() == R.id.item_expenses) {

                    return true;
                } else if (item.getItemId() == R.id.item_profile) {

                    return true;
                } else return false;
            }
        };
        nav.setOnItemSelectedListener(listener);

        // set spinner
        String[] options = {"Last 3 Month", "Last 6 Month", "Last Year"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedItem = parent.getItemAtPosition(position).toString();

                if (selectedItem.equals("Last 3 Month")) {
                    retrieveUserData(new OnTransactionListener() {
                        @Override
                        public void onDateRetrieved(HashMap<String, User.Transaction> transactions) {
                            calculateIncome(transactions, new onIncomeCalculate() {


                                @Override
                                public void getIncome(double threeMonthSalary, double threeMonthBonus, double threeMonthInvestment, double threeMonthFreelance, double threeMonthOthers, double sixMonthSalary, double sixMonthBonus, double sixMonthInvestment, double sixMonthFreelance, double sixMonthOthers, double oneYearSalary, double oneYearBonus, double oneYearInvestment, double oneYearFreelance, double oneYearOthers) {
                                    populatePieChart("Last 3 Month", threeMonthSalary, threeMonthBonus, threeMonthInvestment, threeMonthFreelance, threeMonthOthers, sixMonthSalary, sixMonthBonus, sixMonthInvestment, sixMonthFreelance, sixMonthOthers, oneYearSalary, oneYearBonus, oneYearInvestment, oneYearFreelance, oneYearOthers);
                                }
                            });
                        }
                    });

                }else if(selectedItem.equals("Last 6 Month")){
                    retrieveUserData(new OnTransactionListener() {
                        @Override
                        public void onDateRetrieved(HashMap<String, User.Transaction> transactions) {

                            calculateIncome(transactions, new onIncomeCalculate() {


                                @Override
                                public void getIncome(double threeMonthSalary, double threeMonthBonus, double threeMonthInvestment, double threeMonthFreelance, double threeMonthOthers, double sixMonthSalary, double sixMonthBonus, double sixMonthInvestment, double sixMonthFreelance, double sixMonthOthers, double oneYearSalary, double oneYearBonus, double oneYearInvestment, double oneYearFreelance, double oneYearOthers) {
                                    populatePieChart("Last 6 Month", threeMonthSalary, threeMonthBonus, threeMonthInvestment, threeMonthFreelance, threeMonthOthers, sixMonthSalary, sixMonthBonus, sixMonthInvestment, sixMonthFreelance, sixMonthOthers, oneYearSalary, oneYearBonus, oneYearInvestment, oneYearFreelance, oneYearOthers);
                                }
                            });

                        }
                    });

                }else{

                    retrieveUserData(new OnTransactionListener() {
                        @Override
                        public void onDateRetrieved(HashMap<String, User.Transaction> transactions) {

                            calculateIncome(transactions, new onIncomeCalculate() {


                                        @Override
                                        public void getIncome(double threeMonthSalary, double threeMonthBonus, double threeMonthInvestment, double threeMonthFreelance, double threeMonthOthers, double sixMonthSalary, double sixMonthBonus, double sixMonthInvestment, double sixMonthFreelance, double sixMonthOthers, double oneYearSalary, double oneYearBonus, double oneYearInvestment, double oneYearFreelance, double oneYearOthers) {
                                            populatePieChart("Last Year", threeMonthSalary, threeMonthBonus, threeMonthInvestment, threeMonthFreelance, threeMonthOthers, sixMonthSalary, sixMonthBonus, sixMonthInvestment, sixMonthFreelance, sixMonthOthers, oneYearSalary, oneYearBonus, oneYearInvestment, oneYearFreelance, oneYearOthers);

                                        }
                                    }
                            );
                        }
                    });

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void init() {

        btn_add = findViewById(R.id.user_income_dashboard_btn_add);
        spinner = findViewById(R.id.user_income_dashboard_spinner);
        txtViewAll = findViewById(R.id.user_income_dashboard_txt_view_all);
        recyclerView = findViewById(R.id.user_income_dashboard_recyclerView);
        nav = findViewById(R.id.bottom_navigation);
        pieChart = findViewById(R.id.user_income_dashboard_pie_chart);
    }

    private void retrieveUserData(OnTransactionListener listener) {

        // uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // hardcode uid for testing
        uid = "jba712jsas";
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                HashMap<String, User.Transaction> transactions = user.getTransactions();
                HashMap<String, User.Transaction> incomeTransactions = new HashMap<>();
                for (Map.Entry<String, User.Transaction> entry : transactions.entrySet()) {
                    User.Transaction transaction = entry.getValue();
                    if (transaction.getType().equals("Income")) {
                        incomeTransactions.put(entry.getKey(), transaction);
                    }
                }

                listener.onDateRetrieved(incomeTransactions);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public interface OnTransactionListener {
        void onDateRetrieved(HashMap<String, User.Transaction> transactions);
    }

    private void populatePieChart(String type, double threeMonthSalary, double threeMonthBonus, double threeMonthInvestment, double threeMonthFreelance, double threeMonthOthers,
                                  double sixMonthSalary, double sixMonthBonus, double sixMonthInvestment, double sixMonthFreelance, double sixMonthOthers,
                                  double oneYearSalary, double oneYearBonus, double oneYearInvestment, double oneYearFreelance, double oneYearOthers) {

        // Define the colors for each entry
        List<Integer> colors = new ArrayList<>();

        colors.add(Color.parseColor("#5E7153"));
        colors.add(Color.parseColor("#F2A943"));
        colors.add(Color.parseColor("#F5BDA8"));
        colors.add(Color.parseColor("#3551A4"));
        colors.add(Color.parseColor("#CDBCDB"));


        pieChart.setUsePercentValues(false); // 不显示百分比
        pieChart.getDescription().setEnabled(false); // 隐藏描述
        pieChart.setDrawEntryLabels(false); // 不绘制条目标签
        pieChart.setDrawCenterText(true); // 启用绘制中心文本
        pieChart.setCenterText("Hello"); // 设置中心文本内容
        pieChart.setCenterTextColor(Color.WHITE); // 设置中心文本颜色
        pieChart.setCenterTextSize(18f); // 设置中心文本大小
        pieChart.setDrawHoleEnabled(true); // 绘制中心空白区域
        pieChart.setHoleColor(Color.TRANSPARENT); // 设置中心空白区域颜色为透明
        pieChart.setTransparentCircleRadius(50f); // 设置中心圆形的半径

// 设置图例
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true); // 启用图例
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM); // 设置图例位置在底部
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER); // 设置图例水平居中
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL); // 设置图例水平方向
        legend.setDrawInside(false); // 图例绘制在外围
        legend.setXEntrySpace(7f); // 设置图例条目之间的间距
        legend.setYEntrySpace(5f);
        legend.setTextSize(30f); // 设置图例文本大小
        legend.setWordWrapEnabled(true);



        if(type.equals("Last 3 Month")){

            List<PieEntry> entries = new ArrayList<>();

            entries.add(new PieEntry((float)threeMonthSalary, "Salary"));
            entries.add(new PieEntry((float)threeMonthBonus, "Bonus"));
            entries.add(new PieEntry((float)threeMonthInvestment, "Investment"));
            entries.add(new PieEntry((float)threeMonthFreelance, "Freelance"));
            entries.add(new PieEntry((float)threeMonthOthers, "Others"));
            PieDataSet dataSet = new PieDataSet(entries, "Percentage");
            PieData data = new PieData(dataSet);
            dataSet.setColors(colors);
            data.setDrawValues(false); // 不显示值

            pieChart.setData(data);
            pieChart.invalidate();
        }


    }

    private void calculateIncome(HashMap<String, User.Transaction> incomeTransactions, onIncomeCalculate listener) {

        Date currentDate = new Date();
        Date threeMonthAgo = getDateBeforeOrAfter(currentDate, Calendar.MONTH, -3);
        Date sixMonthAgo = getDateBeforeOrAfter(currentDate, Calendar.MONTH, -6);
        Date oneYearAgo = getDateBeforeOrAfter(currentDate, Calendar.YEAR, -1);

        double threeMonthSalary = 0;
        double threeMonthBonus = 0;
        double threeMonthInvestment = 0;
        double threeMonthFreelance = 0;
        double threeMonthOthers = 0;

        double sixMonthSalary = 0;
        double sixMonthBonus = 0;
        double sixMonthInvestment = 0;
        double sixMonthFreelance = 0;
        double sixMonthOthers = 0;

        double oneYearSalary = 0;
        double oneYearBonus = 0;
        double oneYearInvestment = 0;
        double oneYearFreelance = 0;
        double oneYearOthers = 0;


        for (User.Transaction transaction : incomeTransactions.values()) {
            if (transaction.getDate().after(threeMonthAgo) && transaction.getDate().before(currentDate)) {

                if (transaction.getCategory().equals("Salary")) {
                    threeMonthSalary += transaction.getAmount();
                } else if (transaction.getCategory().equals("Bonus")) {
                    threeMonthBonus += transaction.getAmount();
                } else if (transaction.getCategory().equals("Investment")) {
                    threeMonthInvestment += transaction.getAmount();
                } else if (transaction.getCategory().equals("Freelance")) {
                    threeMonthFreelance += transaction.getAmount();
                } else {
                    threeMonthOthers += transaction.getAmount();
                }

            }
            }

        for (User.Transaction transaction : incomeTransactions.values()) {
            if (transaction.getDate().after(sixMonthAgo) && transaction.getDate().before(currentDate)) {

                if (transaction.getCategory().equals("Salary")) {
                    sixMonthSalary += transaction.getAmount();
                } else if (transaction.getCategory().equals("Bonus")) {
                    sixMonthBonus += transaction.getAmount();
                } else if (transaction.getCategory().equals("Investment")) {
                    sixMonthInvestment += transaction.getAmount();
                } else if (transaction.getCategory().equals("Freelance")) {
                    sixMonthFreelance += transaction.getAmount();
                } else {
                    sixMonthOthers += transaction.getAmount();
                }
            }

        }

        for (User.Transaction transaction : incomeTransactions.values()) {
            if (transaction.getDate().after(oneYearAgo) && transaction.getDate().before(currentDate)) {

                if (transaction.getCategory().equals("Salary")) {
                    oneYearSalary += transaction.getAmount();
                } else if (transaction.getCategory().equals("Bonus")) {
                    oneYearBonus += transaction.getAmount();
                } else if (transaction.getCategory().equals("Investment")) {
                    oneYearInvestment += transaction.getAmount();
                } else if (transaction.getCategory().equals("Freelance")) {
                    oneYearFreelance += transaction.getAmount();
                } else {
                    oneYearOthers += transaction.getAmount();
                }

            }

            listener.getIncome(threeMonthSalary, threeMonthBonus, threeMonthInvestment, threeMonthFreelance, threeMonthOthers,
                    sixMonthSalary, sixMonthBonus, sixMonthInvestment, sixMonthFreelance, sixMonthOthers,
                    oneYearSalary, oneYearBonus, oneYearInvestment, oneYearFreelance, oneYearOthers);
        }
    }

    public interface onIncomeCalculate {

        void getIncome(double threeMonthSalary, double threeMonthBonus, double threeMonthInvestment, double threeMonthFreelance, double threeMonthOthers,
                       double sixMonthSalary, double sixMonthBonus, double sixMonthInvestment, double sixMonthFreelance, double sixMonthOthers,
                       double oneYearSalary, double oneYearBonus, double oneYearInvestment, double oneYearFreelance, double oneYearOthers);
    }

    public static Date getDateBeforeOrAfter(Date date, int field, int amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(field, amount);
        return cal.getTime();
    }
}