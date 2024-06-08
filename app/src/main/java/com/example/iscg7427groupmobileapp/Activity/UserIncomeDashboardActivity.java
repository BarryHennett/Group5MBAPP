package com.example.iscg7427groupmobileapp.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iscg7427groupmobileapp.Adapter.TransactionAdapter;
import com.example.iscg7427groupmobileapp.Model.User;
import com.example.iscg7427groupmobileapp.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserIncomeDashboardActivity extends AppCompatActivity {

    LinearLayout btn_add;
    Spinner spinner;
    TextView txtViewAll;
    RecyclerView recyclerView;
    String uid;
    private BottomNavigationView bottomNavigation;
    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_income_dashboard);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        init();
        setupBottomNavigation();

        btn_add.setOnClickListener(v -> {
            Intent intent = new Intent(UserIncomeDashboardActivity.this, UserAddIncomeActivity.class);
            startActivity(intent);
        });

        txtViewAll.setOnClickListener(v -> {

        });


        // set spinner
        String[] options = {"Last 3 Month", "Last 6 Month", "Last Year"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner, options);
        adapter.setDropDownViewResource(R.layout.spinner);
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
                                public void getIncome(double threeMonthSalary, double threeMonthBonus, double threeMonthInvestment,
                                                      double threeMonthFreelance, double threeMonthOthers, double sixMonthSalary,
                                                      double sixMonthBonus, double sixMonthInvestment, double sixMonthFreelance,
                                                      double sixMonthOthers, double oneYearSalary, double oneYearBonus,
                                                      double oneYearInvestment, double oneYearFreelance, double oneYearOthers) {
                                    populatePieChart("Last 3 Month", threeMonthSalary, threeMonthBonus, threeMonthInvestment,
                                            threeMonthFreelance, threeMonthOthers, sixMonthSalary, sixMonthBonus, sixMonthInvestment,
                                            sixMonthFreelance, sixMonthOthers, oneYearSalary, oneYearBonus, oneYearInvestment,
                                            oneYearFreelance, oneYearOthers);
                                }
                            });
                        }
                    });
                } else if (selectedItem.equals("Last 6 Month")) {
                    retrieveUserData(new OnTransactionListener() {
                        @Override
                        public void onDateRetrieved(HashMap<String, User.Transaction> transactions) {
                            calculateIncome(transactions, new onIncomeCalculate() {
                                @Override
                                public void getIncome(double threeMonthSalary, double threeMonthBonus, double threeMonthInvestment,
                                                      double threeMonthFreelance, double threeMonthOthers, double sixMonthSalary,
                                                      double sixMonthBonus, double sixMonthInvestment, double sixMonthFreelance,
                                                      double sixMonthOthers, double oneYearSalary, double oneYearBonus,
                                                      double oneYearInvestment, double oneYearFreelance, double oneYearOthers) {
                                    populatePieChart("Last 6 Month", threeMonthSalary, threeMonthBonus, threeMonthInvestment,
                                            threeMonthFreelance, threeMonthOthers, sixMonthSalary, sixMonthBonus, sixMonthInvestment,
                                            sixMonthFreelance, sixMonthOthers, oneYearSalary, oneYearBonus, oneYearInvestment,
                                            oneYearFreelance, oneYearOthers);
                                }
                            });
                        }
                    });
                } else {
                    retrieveUserData(new OnTransactionListener() {
                        @Override
                        public void onDateRetrieved(HashMap<String, User.Transaction> transactions) {
                            calculateIncome(transactions, new onIncomeCalculate() {
                                        @Override
                                        public void getIncome(double threeMonthSalary, double threeMonthBonus,
                                                              double threeMonthInvestment, double threeMonthFreelance,
                                                              double threeMonthOthers, double sixMonthSalary, double sixMonthBonus,
                                                              double sixMonthInvestment, double sixMonthFreelance, double sixMonthOthers,
                                                              double oneYearSalary, double oneYearBonus, double oneYearInvestment,
                                                              double oneYearFreelance, double oneYearOthers) {
                                            populatePieChart("Last Year", threeMonthSalary, threeMonthBonus, threeMonthInvestment,
                                                    threeMonthFreelance, threeMonthOthers, sixMonthSalary, sixMonthBonus,
                                                    sixMonthInvestment, sixMonthFreelance, sixMonthOthers, oneYearSalary, oneYearBonus,
                                                    oneYearInvestment, oneYearFreelance, oneYearOthers);
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

        retrieveUserData(new OnTransactionListener() {
            @Override
            public void onDateRetrieved(HashMap<String, User.Transaction> transactions) {

                // sort 10 recent transactions by date in recyclerview
                List<Map.Entry<String, User.Transaction>> list = new ArrayList<>(transactions.entrySet());
                list.sort(Comparator.comparing(o -> o.getValue().getDate(), Comparator.reverseOrder()));
                LinkedHashMap<String, User.Transaction> recentTransactions = new LinkedHashMap<>();
                int count = 0;
                for (Map.Entry<String, User.Transaction> entry : list) {
                    recentTransactions.put(entry.getKey(), entry.getValue());
                    count++;
                    if (count >= 10) {
                        break;
                    }
                }

                recyclerView.setLayoutManager(new LinearLayoutManager(UserIncomeDashboardActivity.this));
                recyclerView.setAdapter(new TransactionAdapter(recentTransactions, UserIncomeDashboardActivity.this, uid));

            }
        } );

        txtViewAll.setOnClickListener(v -> {
            Intent intent = new Intent(UserIncomeDashboardActivity.this, UserAllTransactions.class);
            startActivity(intent);
        });
    }

    private void init() {

        btn_add = findViewById(R.id.user_income_dashboard_btn_add);
        spinner = findViewById(R.id.user_income_dashboard_spinner);
        txtViewAll = findViewById(R.id.user_income_dashboard_txt_view_all);
        recyclerView = findViewById(R.id.user_income_dashboard_recyclerView);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        pieChart = findViewById(R.id.user_income_dashboard_pie_chart);
    }
    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                Log.d("BottomNav", "Selected Item ID: " + itemId);

                if (itemId == R.id.item_home) {
                    Log.d("BottomNav", "Home selected");
                    startActivity(new Intent(UserIncomeDashboardActivity.this, UserDashboardActivity_1.class));
                    return true;
                } else if (itemId == R.id.item_income) {
                    Log.d("BottomNav", "Income selected");
                    return true;
                } else if (itemId == R.id.item_expenses) {
                    Log.d("BottomNav", "Expenses selected");
                    startActivity(new Intent(UserIncomeDashboardActivity.this, UserExpenseDashboardActivity.class));
                    return true;
                } else if (itemId == R.id.item_profile) {
                    Log.d("BottomNav", "Profile selected");
                    startActivity(new Intent(UserIncomeDashboardActivity.this, UserProfileActivity.class));
                    return true;
                } else {
                    Log.d("BottomNav", "Unknown item selected");
                    return false;
                }
            }
        });

        // Set the selected item as item_profile
        bottomNavigation.post(new Runnable() {
            @Override
            public void run() {
                bottomNavigation.setSelectedItemId(R.id.item_income);
            }
        });
    }

    private void retrieveUserData(OnTransactionListener listener) {

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


        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#5E7153"));
        colors.add(Color.parseColor("#F2A943"));
        colors.add(Color.parseColor("#F5BDA8"));
        colors.add(Color.parseColor("#3551A4"));
        colors.add(Color.parseColor("#CDBCDB"));

        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.setDrawCenterText(true);
        pieChart.setCenterText("Total Expense");
        pieChart.setCenterTextColor(Color.WHITE);
        pieChart.setCenterTextSize(20f);
        pieChart.setHoleRadius(60f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setTransparentCircleRadius(100f);

// 设置图例
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true); // 启用图例
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM); // 设置图例位置在底部
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER); // 设置图例水平居中
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL); // 设置图例水平方向
        legend.setDrawInside(false); // 图例绘制在外围
        legend.setXEntrySpace(10f); // 设置图例条目之间的间距
        legend.setYEntrySpace(5f);
        legend.setTextSize(10f); // 设置图例文本大小
        legend.setTextColor(Color.WHITE); // 设置图例文本颜色
        legend.setWordWrapEnabled(true);

        if (type.equals("Last 3 Month")) {

            List<PieEntry> entries = new ArrayList<>();
            entries.add(new PieEntry((float) threeMonthSalary, "Salary"));
            entries.add(new PieEntry((float) threeMonthBonus, "Bonus"));
            entries.add(new PieEntry((float) threeMonthInvestment, "Investment"));
            entries.add(new PieEntry((float) threeMonthFreelance, "Freelance"));
            entries.add(new PieEntry((float) threeMonthOthers, "Others"));
            PieDataSet dataSet = new PieDataSet(entries, "");
            PieData data = new PieData(dataSet);
            dataSet.setColors(colors);
            data.setDrawValues(false); // 不显示值
            pieChart.setData(data);
            pieChart.setCenterText(Double.toString(threeMonthSalary + threeMonthBonus + threeMonthInvestment +
                    threeMonthFreelance + threeMonthOthers));
            pieChart.invalidate();
        } else if (type.equals("Last 6 Month")) {
            List<PieEntry> entries = new ArrayList<>();
            entries.add(new PieEntry((float) sixMonthSalary, "Salary"));
            entries.add(new PieEntry((float) sixMonthBonus, "Bonus"));
            entries.add(new PieEntry((float) sixMonthInvestment, "Investment"));
            entries.add(new PieEntry((float) sixMonthFreelance, "Freelance"));
            entries.add(new PieEntry((float) sixMonthOthers, "Others"));
            PieDataSet dataSet = new PieDataSet(entries, "");
            PieData data = new PieData(dataSet);
            dataSet.setColors(colors);
            data.setDrawValues(false); // 不显示值
            pieChart.setData(data);
            pieChart.setCenterText(Double.toString(sixMonthSalary + sixMonthBonus + sixMonthInvestment +
                    sixMonthFreelance + sixMonthOthers));
            pieChart.invalidate();
        } else if (type.equals("Last Year")) {
            List<PieEntry> entries = new ArrayList<>();
            entries.add(new PieEntry((float) oneYearSalary, "Salary"));
            entries.add(new PieEntry((float) oneYearBonus, "Bonus"));
            entries.add(new PieEntry((float) oneYearInvestment, "Investment"));
            entries.add(new PieEntry((float) oneYearFreelance, "Freelance"));
            entries.add(new PieEntry((float) oneYearOthers, "Others"));
            PieDataSet dataSet = new PieDataSet(entries, "");
            PieData data = new PieData(dataSet);
            dataSet.setColors(colors);
            data.setDrawValues(false); // 不显示值
            pieChart.setData(data);
            pieChart.setCenterText(Double.toString(oneYearSalary + oneYearBonus + oneYearInvestment +
                    oneYearFreelance + oneYearOthers));
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
        void getIncome(double threeMonthSalary, double threeMonthBonus, double threeMonthInvestment, double threeMonthFreelance,
                       double threeMonthOthers, double sixMonthSalary, double sixMonthBonus, double sixMonthInvestment,
                       double sixMonthFreelance, double sixMonthOthers, double oneYearSalary, double oneYearBonus,
                       double oneYearInvestment, double oneYearFreelance, double oneYearOthers);
    }

    public static Date getDateBeforeOrAfter(Date date, int field, int amount) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(field, amount);
        return cal.getTime();
    }
}