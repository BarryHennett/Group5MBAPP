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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iscg7427groupmobileapp.Adapter.TransactionAdapter;
import com.example.iscg7427groupmobileapp.Model.User;
import com.example.iscg7427groupmobileapp.R;
import com.example.iscg7427groupmobileapp.UserAllTransactions;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
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

public class UserExpenseDashboardActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_user_expense_dashboard);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        init();
        btn_add.setOnClickListener(v -> {
            Intent intent = new Intent(UserExpenseDashboardActivity.this, UserAddExpenseActivity.class);
            startActivity(intent);
        });

        txtViewAll.setOnClickListener(v -> {

            Intent intent = new Intent(UserExpenseDashboardActivity.this, UserAllTransactions.class);
            startActivity(intent);

        });

        NavigationBarView.OnItemSelectedListener listener = new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.item_home) {

                    Intent intent = new Intent(UserExpenseDashboardActivity.this, UserDashboardActivity_1.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.item_income) {

                    Intent intent = new Intent(UserExpenseDashboardActivity.this, UserIncomeDashboardActivity.class);
                    startActivity(intent);

                    return true;
                } else if (item.getItemId() == R.id.item_expenses) {

                    Intent intent = new Intent(UserExpenseDashboardActivity.this, UserExpenseDashboardActivity.class);
                    startActivity(intent);

                    return true;
                } else if (item.getItemId() == R.id.item_profile) {

                    Intent intent = new Intent(UserExpenseDashboardActivity.this, UserProfileActivity.class);
                    startActivity(intent);
                    return true;
                } else return false;
            }
        };
        nav.setOnItemSelectedListener(listener);

        // set spinner
        String[] options = {"Last 3 Month", "Last 6 Month", "Last Year"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner, options);
        adapter.setDropDownViewResource(R.layout.spinner;
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedItem = parent.getItemAtPosition(position).toString();

                if (selectedItem.equals("Last 3 Month")) {
                    retrieveUserData(new OnTransactionListener() {
                        @Override
                        public void onDateRetrieved(HashMap<String, User.Transaction> transactions) {

                            calculateExpense(transactions, new onExpenseCalculate() {
                                @Override
                                public void getExpense(double threeMonthTransportation, double threeMonthHousing, double threeMonthDining, double threeMonthEntertainment, double threeMonthOthers, double sixMonthTransportation, double sixMonthHousing, double sixMonthDining, double sixMonthEntertainment, double sixMonthOthers, double oneYearTransportation, double oneYearHousing, double oneYearDining, double oneYearEntertainment, double oneYearOthers) {
                                    populatePieChart("Last 3 Month", threeMonthTransportation, threeMonthHousing, threeMonthDining, threeMonthEntertainment, threeMonthOthers,
                                            sixMonthTransportation, sixMonthHousing, sixMonthDining, sixMonthEntertainment, sixMonthOthers,
                                            oneYearTransportation, oneYearHousing, oneYearDining, oneYearEntertainment, oneYearOthers);
                                }
                            });


                        }
                    });
                } else if (selectedItem.equals("Last 6 Month")) {
                    retrieveUserData(new OnTransactionListener() {
                        @Override
                        public void onDateRetrieved(HashMap<String, User.Transaction> transactions) {

                            calculateExpense(transactions, new onExpenseCalculate() {
                                @Override
                                public void getExpense(double threeMonthTransportation, double threeMonthHousing, double threeMonthDining, double threeMonthEntertainment, double threeMonthOthers, double sixMonthTransportation, double sixMonthHousing, double sixMonthDining, double sixMonthEntertainment, double sixMonthOthers, double oneYearTransportation, double oneYearHousing, double oneYearDining, double oneYearEntertainment, double oneYearOthers) {
                                    populatePieChart("Last 6 Month", threeMonthTransportation, threeMonthHousing, threeMonthDining, threeMonthEntertainment, threeMonthOthers,
                                            sixMonthTransportation, sixMonthHousing, sixMonthDining, sixMonthEntertainment, sixMonthOthers,
                                            oneYearTransportation, oneYearHousing, oneYearDining, oneYearEntertainment, oneYearOthers);
                                }
                            });

                        }
                    });
                } else {
                    retrieveUserData(new OnTransactionListener() {
                        @Override
                        public void onDateRetrieved(HashMap<String, User.Transaction> transactions) {

                            calculateExpense(transactions, new onExpenseCalculate() {
                                @Override
                                public void getExpense(double threeMonthTransportation, double threeMonthHousing, double threeMonthDining, double threeMonthEntertainment, double threeMonthOthers, double sixMonthTransportation, double sixMonthHousing, double sixMonthDining, double sixMonthEntertainment, double sixMonthOthers, double oneYearTransportation, double oneYearHousing, double oneYearDining, double oneYearEntertainment, double oneYearOthers) {
                                    populatePieChart("Last Year", threeMonthTransportation, threeMonthHousing, threeMonthDining, threeMonthEntertainment, threeMonthOthers,
                                            sixMonthTransportation, sixMonthHousing, sixMonthDining, sixMonthEntertainment, sixMonthOthers,
                                            oneYearTransportation, oneYearHousing, oneYearDining, oneYearEntertainment, oneYearOthers);
                                }
                            });


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

                recyclerView.setLayoutManager(new LinearLayoutManager(UserExpenseDashboardActivity.this));
                recyclerView.setAdapter(new TransactionAdapter(recentTransactions, UserExpenseDashboardActivity.this, uid));


            };
        });

    }

    private void init() {

        btn_add = findViewById(R.id.user_expense_dashboard_btn_add);
        spinner = findViewById(R.id.user_expense_dashboard_spinner);
        txtViewAll = findViewById(R.id.user_expense_dashboard_txt_view_all);
        recyclerView = findViewById(R.id.user_expense_dashboard_recyclerView);
        nav = findViewById(R.id.bottom_navigation);
        pieChart = findViewById(R.id.user_expense_dashboard_pie_chart);
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
                    if (transaction.getType().equals("Expense")) {
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

    private void populatePieChart(String type, double threeMonthTransportation, double threeMonthHousing, double threeMonthEntertainment, double threeMonthDining, double threeMonthOthers,
                                  double sixMonthTransportation, double sixMonthHousing, double sixMonthEntertainment, double sixMonthDining, double sixMonthOthers,
                                  double oneYearTransportation, double oneYearHousing, double oneYearEntertainment, double oneYearDining, double oneYearOthers) {


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

            entries.add(new PieEntry((float) threeMonthTransportation, "Transportation"));
            entries.add(new PieEntry((float) threeMonthHousing, "Housing"));
            entries.add(new PieEntry((float) threeMonthDining, "Dining"));
            entries.add(new PieEntry((float) threeMonthEntertainment, "Entertainment"));
            entries.add(new PieEntry((float) threeMonthOthers, "Others"));
            PieDataSet dataSet = new PieDataSet(entries, "");
            PieData data = new PieData(dataSet);
            dataSet.setColors(colors);
            data.setDrawValues(false); // 不显示值
            pieChart.setData(data);
            pieChart.setCenterText( Double.toString(threeMonthDining + threeMonthEntertainment + threeMonthHousing +
                    threeMonthOthers + threeMonthTransportation));
            pieChart.invalidate();
        } else if (type.equals("Last 6 Month")) {
            List<PieEntry> entries = new ArrayList<>();
            entries.add(new PieEntry((float) sixMonthTransportation, "Transportation"));
            entries.add(new PieEntry((float) sixMonthHousing, "Housing"));
            entries.add(new PieEntry((float) sixMonthDining, "Dining"));
            entries.add(new PieEntry((float) sixMonthEntertainment, "Entertainment"));
            entries.add(new PieEntry((float) sixMonthOthers, "Others"));
            PieDataSet dataSet = new PieDataSet(entries, "");
            PieData data = new PieData(dataSet);
            dataSet.setColors(colors);
            data.setDrawValues(false); // 不显示值
            pieChart.setData(data);
            pieChart.setCenterText( Double.toString(sixMonthDining + sixMonthEntertainment + sixMonthHousing +
                    sixMonthOthers + sixMonthTransportation));
            pieChart.invalidate();
        } else if (type.equals("Last Year")) {
            List<PieEntry> entries = new ArrayList<>();
            entries.add(new PieEntry((float) oneYearTransportation, "Transportation"));
            entries.add(new PieEntry((float) oneYearHousing, "Housing"));
            entries.add(new PieEntry((float) oneYearDining, "Dining"));
            entries.add(new PieEntry((float) oneYearEntertainment, "Entertainment"));
            entries.add(new PieEntry((float) oneYearOthers, "Others"));
            PieDataSet dataSet = new PieDataSet(entries, "");
            PieData data = new PieData(dataSet);
            dataSet.setColors(colors);
            data.setDrawValues(false); // 不显示值
            pieChart.setData(data);
            pieChart.setCenterText( Double.toString(oneYearDining + oneYearEntertainment + oneYearHousing +
                    oneYearOthers + oneYearTransportation));
            pieChart.invalidate();

        }
    }

    private void calculateExpense(HashMap<String, User.Transaction> expenseTransactions, onExpenseCalculate listener) {

        Date currentDate = new Date();
        Date threeMonthAgo = getDateBeforeOrAfter(currentDate, Calendar.MONTH, -3);
        Date sixMonthAgo = getDateBeforeOrAfter(currentDate, Calendar.MONTH, -6);
        Date oneYearAgo = getDateBeforeOrAfter(currentDate, Calendar.YEAR, -1);

        double threeMonthTransportation = 0;
        double threeMonthHousing = 0;
        double threeMonthDining = 0;
        double threeMonthEntertainment = 0;
        double threeMonthOthers = 0;

        double sixMonthTransportation = 0;
        double sixMonthHousing = 0;
        double sixMonthDining = 0;
        double sixMonthEntertainment = 0;
        double sixMonthOthers = 0;

        double oneYearTransportation = 0;
        double oneYearHousing = 0;
        double oneYearDining = 0;
        double oneYearEntertainment = 0;
        double oneYearOthers = 0;


        for (User.Transaction transaction : expenseTransactions.values()) {

            if (transaction.getDate().after(threeMonthAgo) && transaction.getDate().before(currentDate)) {
                if (transaction.getCategory().equals("Transportation")) {
                    threeMonthTransportation += transaction.getAmount();
                } else if (transaction.getCategory().equals("Housing")) {
                    threeMonthHousing += transaction.getAmount();
                } else if (transaction.getCategory().equals("Dining")) {
                    threeMonthDining += transaction.getAmount();
                } else if (transaction.getCategory().equals("Entertainment")) {
                    threeMonthEntertainment += transaction.getAmount();
                } else {
                    threeMonthOthers += transaction.getAmount();
                }
            }
        }

        for (User.Transaction transaction : expenseTransactions.values()) {

            if (transaction.getDate().after(sixMonthAgo) && transaction.getDate().before(currentDate)) {
                if (transaction.getCategory().equals("Transportation")) {
                    sixMonthTransportation += transaction.getAmount();
                } else if (transaction.getCategory().equals("Housing")) {
                    sixMonthHousing += transaction.getAmount();
                } else if (transaction.getCategory().equals("Dining")) {
                    sixMonthDining += transaction.getAmount();
                } else if (transaction.getCategory().equals("Entertainment")) {
                    sixMonthEntertainment += transaction.getAmount();
                } else {
                    sixMonthOthers += transaction.getAmount();
                }
            }
        }

        for (User.Transaction transaction : expenseTransactions.values()) {

            if (transaction.getDate().after(oneYearAgo) && transaction.getDate().before(currentDate)) {
                if (transaction.getCategory().equals("Transportation")) {
                    oneYearTransportation += transaction.getAmount();
                } else if (transaction.getCategory().equals("Housing")) {
                    oneYearHousing += transaction.getAmount();
                } else if (transaction.getCategory().equals("Dining")) {
                    oneYearDining += transaction.getAmount();
                } else if (transaction.getCategory().equals("Entertainment")) {
                    oneYearEntertainment += transaction.getAmount();
                } else {
                    oneYearOthers += transaction.getAmount();
                }
            }


            listener.getExpense(threeMonthTransportation, threeMonthHousing, threeMonthDining, threeMonthEntertainment, threeMonthOthers,
                    sixMonthTransportation, sixMonthHousing, sixMonthDining, sixMonthEntertainment, sixMonthOthers,
                    oneYearTransportation, oneYearHousing, oneYearDining, oneYearEntertainment, oneYearOthers);
        }
    }

    public interface onExpenseCalculate {
        void getExpense(double threeMonthTransportation, double threeMonthHousing, double threeMonthDining, double threeMonthEntertainment,
                       double threeMonthOthers, double sixMonthTransportation, double sixMonthHousing, double sixMonthDining,
                       double sixMonthEntertainment, double sixMonthOthers, double oneYearTransportation, double oneYearHousing,
                       double oneYearDining, double oneYearEntertainment, double oneYearOthers);
    }

    public static Date getDateBeforeOrAfter(Date date, int field, int amount) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(field, amount);
        return cal.getTime();
    }

}