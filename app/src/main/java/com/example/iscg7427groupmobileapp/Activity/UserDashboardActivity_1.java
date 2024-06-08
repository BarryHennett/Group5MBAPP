package com.example.iscg7427groupmobileapp.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.util.Log;
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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.navigation.NavigationBarView;
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
import java.util.Locale;
import java.util.Map;

public class UserDashboardActivity_1 extends AppCompatActivity {

    ImageButton btnIncome, btnExpense;
    TextView txtName, txtBalance, txtIncome, txtExpense, txtViewAll, extraText;
    Spinner spinner;
    RecyclerView recyclerView;
    BarChart barChart;
    String uid;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard_1);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        init();
        setupBottomNavigation();

        // initialize page
        retrieveUserData(new OnTransactionListener() {
            @Override
            public void onDateRetrieved(HashMap<String, User.Transaction> transactions) {
                // calculate income, expense and balance
                double income = 0;
                double expense = 0;
                for (User.Transaction transaction : transactions.values()) {
                    if (transaction.getType().equals("Income")) {
                        income += transaction.getAmount();
                    } else {
                        expense += transaction.getAmount();
                    }
                }
                double net = income - expense;
                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
                txtIncome.setText(String.valueOf(currencyFormat.format(income)));
                txtExpense.setText(String.valueOf(currencyFormat.format(expense)));
                txtBalance.setText(String.valueOf(currencyFormat.format(net)));
                if ( income > expense) {
                    extraText.setText("Great job, keep it up!");
                } else {
                    extraText.setText("Oops, keep trying!");
                }

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
                recyclerView.setLayoutManager(new LinearLayoutManager(UserDashboardActivity_1.this));
                recyclerView.setAdapter(new TransactionAdapter(recentTransactions, UserDashboardActivity_1.this, uid));
            }
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
                            calculateIncomeAndExpense(transactions, new onIncomeAndExpenseCalculate() {
                                @Override
                                public void getIncomeAndExpense(double oneMonthIncome, double oneMonthExpense, double twoMonthIncome, double twoMonthExpense, double threeMonthIncome, double threeMonthExpense, double fourMonthIncome, double fourMonthExpense, double fiveMonthIncome, double fiveMonthExpense, double sixMonthIncome, double sixMonthExpense, double nineMonthIncome, double nineMonthExpense, double oneYearIncome, double oneYearExpense) {

                                    populateBarchart("Last 3 Month", oneMonthIncome, oneMonthExpense, twoMonthIncome, twoMonthExpense,
                                            threeMonthIncome, threeMonthExpense, fourMonthIncome, fourMonthExpense,
                                            fiveMonthIncome, fiveMonthExpense, sixMonthIncome, sixMonthExpense,
                                            nineMonthIncome, nineMonthExpense, oneYearIncome, oneYearExpense);
                                }
                            });
                        }
                    });

                } else if (selectedItem.equals("Last 6 Month")) {
                    retrieveUserData(new OnTransactionListener() {
                        @Override
                        public void onDateRetrieved(HashMap<String, User.Transaction> transactions) {
                            calculateIncomeAndExpense(transactions, new onIncomeAndExpenseCalculate() {
                                @Override
                                public void getIncomeAndExpense(double oneMonthIncome, double oneMonthExpense, double twoMonthIncome, double twoMonthExpense, double threeMonthIncome, double threeMonthExpense, double fourMonthIncome, double fourMonthExpense, double fiveMonthIncome, double fiveMonthExpense, double sixMonthIncome, double sixMonthExpense, double nineMonthIncome, double nineMonthExpense, double oneYearIncome, double oneYearExpense) {

                                    populateBarchart("Last 6 Month", oneMonthIncome, oneMonthExpense, twoMonthIncome, twoMonthExpense,
                                            threeMonthIncome, threeMonthExpense, fourMonthIncome, fourMonthExpense,
                                            fiveMonthIncome, fiveMonthExpense, sixMonthIncome, sixMonthExpense,
                                            nineMonthIncome, nineMonthExpense, oneYearIncome, oneYearExpense);
                                }
                            });
                        }
                    });
                } else {
                    retrieveUserData(new OnTransactionListener() {
                        @Override
                        public void onDateRetrieved(HashMap<String, User.Transaction> transactions) {
                            calculateIncomeAndExpense(transactions, new onIncomeAndExpenseCalculate() {
                                @Override
                                public void getIncomeAndExpense(double oneMonthIncome, double oneMonthExpense, double twoMonthIncome, double twoMonthExpense, double threeMonthIncome, double threeMonthExpense, double fourMonthIncome, double fourMonthExpense, double fiveMonthIncome, double fiveMonthExpense, double sixMonthIncome, double sixMonthExpense, double nineMonthIncome, double nineMonthExpense, double oneYearIncome, double oneYearExpense) {

                                    populateBarchart("Last Year", oneMonthIncome, oneMonthExpense, twoMonthIncome, twoMonthExpense,
                                            threeMonthIncome, threeMonthExpense, fourMonthIncome, fourMonthExpense,
                                            fiveMonthIncome, fiveMonthExpense, sixMonthIncome, sixMonthExpense,
                                            nineMonthIncome, nineMonthExpense, oneYearIncome, oneYearExpense);
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


        btnIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserDashboardActivity_1.this, UserIncomeDashboardActivity.class);
                startActivity(intent);

            }
        });

        btnExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserDashboardActivity_1.this, UserExpenseDashboardActivity.class);
                startActivity(intent);
            }
        });

        txtViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserDashboardActivity_1.this, UserAllTransactions.class);
                startActivity(intent);
            }
        });
    }

    private void init() {

        btnIncome = findViewById(R.id.user_dashboard_btn_income);
        btnExpense = findViewById(R.id.user_dashboard_btn_expense);
        txtName = findViewById(R.id.user_dashboard_txt_name);
        txtBalance = findViewById(R.id.user_dashboard_txt_balance);
        txtIncome = findViewById(R.id.user_dashboard_txt_income);
        txtExpense = findViewById(R.id.user_dashboard_txt_expense);
        spinner = findViewById(R.id.user_dashboard_spinner);
        recyclerView = findViewById(R.id.user_dashboard_rec);
        barChart = findViewById(R.id.user_dashboard_chart);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        txtViewAll = findViewById(R.id.user_dashboard_txt_view_all);
        extraText = findViewById(R.id.extraText);
    }

    // set bottom navigation bar
    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                Log.d("BottomNav", "Selected Item ID: " + itemId);
                if (itemId == R.id.item_home) {
                    Log.d("BottomNav", "Home selected");
                    return true;
                } else if (itemId == R.id.item_income) {
                    Log.d("BottomNav", "Income selected");
                    startActivity(new Intent(UserDashboardActivity_1.this, UserIncomeDashboardActivity.class));
                    return true;
                } else if (itemId == R.id.item_expenses) {
                    Log.d("BottomNav", "Expenses selected");
                    startActivity(new Intent(UserDashboardActivity_1.this, UserExpenseDashboardActivity.class));
                    return true;
                } else if (itemId == R.id.item_profile) {
                    Log.d("BottomNav", "Profile selected");
                    startActivity(new Intent(UserDashboardActivity_1.this, UserProfileActivity.class));
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
                bottomNavigation.setSelectedItemId(R.id.item_home);
            }
        });
    }

    private void retrieveUserData(OnTransactionListener listener) {

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                HashMap<String, User.Transaction> transactions = user.getTransactions();
                listener.onDateRetrieved(transactions);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public interface OnTransactionListener {
        void onDateRetrieved(HashMap<String, User.Transaction> transactions);
    }

    private void populateBarchart(String type, double oneMonthIncomeGap, double oneMonthExpenseGap, double twoMonthIncomeGap,
                                  double twoMonthExpenseGap, double threeMonthIncomeGap, double threeMonthExpenseGap,
                                  double fourMonthIncomeGap, double fourMonthExpenseGap, double fiveMonthIncomeGap,
                                  double fiveMonthExpenseGap, double sixMonthIncomeGap, double sixMonthExpenseGap,
                                  double nineMonthIncomeGap, double nineMonthExpenseGap, double oneYearIncomeGap,
                                  double oneYearExpenseGap) {

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setAxisLineWidth(1f);
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setDrawLabels(false);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setEnabled(false);
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);

        List<BarEntry> entriesGroup1 = new ArrayList<>();
        List<BarEntry> entriesGroup2 = new ArrayList<>();

        float barWidth = 0.20f;

        if (type.equals("Last 3 Month")) {

            entriesGroup1.add(new BarEntry(0f, (float) oneMonthIncomeGap));
            entriesGroup1.add(new BarEntry(1f, (float) twoMonthIncomeGap));
            entriesGroup1.add(new BarEntry(2f, (float) threeMonthIncomeGap));

            entriesGroup2.add(new BarEntry(0f, (float) oneMonthExpenseGap));
            entriesGroup2.add(new BarEntry(1f, (float) twoMonthExpenseGap));
            entriesGroup2.add(new BarEntry(2f, (float) threeMonthExpenseGap));

        } else if (type.equals("Last 6 Month")) {

            entriesGroup1.add(new BarEntry(0f, (float) oneMonthIncomeGap));
            entriesGroup1.add(new BarEntry(1f, (float) twoMonthIncomeGap));
            entriesGroup1.add(new BarEntry(2f, (float) threeMonthIncomeGap));
            entriesGroup1.add(new BarEntry(3f, (float) fourMonthIncomeGap));
            entriesGroup1.add(new BarEntry(4f, (float) fiveMonthIncomeGap));
            entriesGroup1.add(new BarEntry(5f, (float) sixMonthIncomeGap));

            entriesGroup2.add(new BarEntry(0f, (float) oneMonthExpenseGap));
            entriesGroup2.add(new BarEntry(1f, (float) twoMonthExpenseGap));
            entriesGroup2.add(new BarEntry(2f, (float) threeMonthExpenseGap));
            entriesGroup2.add(new BarEntry(3f, (float) fourMonthExpenseGap));
            entriesGroup2.add(new BarEntry(4f, (float) fiveMonthExpenseGap));
            entriesGroup2.add(new BarEntry(5f, (float) sixMonthExpenseGap));

            barWidth = 0.30f;

        } else if (type.equals("Last Year")) {

            float incomeOneSeasonGp = (float) (oneMonthIncomeGap + twoMonthIncomeGap + threeMonthIncomeGap);
            float incomeTwoSeasonGp = (float) (fourMonthIncomeGap + fiveMonthIncomeGap + sixMonthIncomeGap);
            float expenseOneSeasonGp = (float) (oneMonthExpenseGap + twoMonthExpenseGap + threeMonthExpenseGap);
            float expenseTwoSeasonGp = (float) (fourMonthExpenseGap + fiveMonthExpenseGap + sixMonthExpenseGap);

            entriesGroup1.add(new BarEntry(0f, incomeOneSeasonGp));
            entriesGroup1.add(new BarEntry(1f, incomeTwoSeasonGp));
            entriesGroup1.add(new BarEntry(2f, (float) nineMonthIncomeGap));
            entriesGroup1.add(new BarEntry(3f, (float) oneYearIncomeGap));
            entriesGroup2.add(new BarEntry(0f, expenseOneSeasonGp));
            entriesGroup2.add(new BarEntry(1f, expenseTwoSeasonGp));
            entriesGroup2.add(new BarEntry(2f, (float) nineMonthExpenseGap));
            entriesGroup2.add(new BarEntry(3f, (float) oneYearExpenseGap));

            barWidth = 0.30f;
        }

        float groupSpace = 0.3f;
        float barSpace = 0.05f;
        float startValue = 0f - (barWidth + barSpace + groupSpace) / 2f;

        BarDataSet set1 = new BarDataSet(entriesGroup1, "Group 1");
        BarDataSet set2 = new BarDataSet(entriesGroup2, "Group 2");
        set1.setColor(Color.parseColor("#88BBD8"));
        set2.setColor(Color.parseColor("#EB5F2A"));
        set1.setDrawValues(false);
        set2.setDrawValues(false);
        barChart.setDescription(null);
        BarData data = new BarData(set1, set2);
        data.setBarWidth(barWidth);
        barChart.getLegend().setEnabled(false);
        barChart.setData(data);
        barChart.groupBars(startValue, groupSpace, barSpace);
        barChart.invalidate();
    }

    private void calculateIncomeAndExpense(HashMap<String, User.Transaction> transactions, onIncomeAndExpenseCalculate listener) {

        Date currentDate = new Date();
        Date oneMonthAgo = getDateBeforeOrAfter(currentDate, Calendar.MONTH, -1);
        Date twoMonthAgo = getDateBeforeOrAfter(currentDate, Calendar.MONTH, -2);
        Date threeMonthAgo = getDateBeforeOrAfter(currentDate, Calendar.MONTH, -3);
        Date fourMonthAgo = getDateBeforeOrAfter(currentDate, Calendar.MONTH, -4);
        Date fiveMonthAgo = getDateBeforeOrAfter(currentDate, Calendar.MONTH, -5);
        Date sixMonthAgo = getDateBeforeOrAfter(currentDate, Calendar.MONTH, -6);
        Date nineMonthAgo = getDateBeforeOrAfter(currentDate, Calendar.MONTH, -9);
        Date oneYearAgo = getDateBeforeOrAfter(currentDate, Calendar.YEAR, -1);

        double oneMonthIncomeGap = 0;
        double oneMonthExpenseGap = 0;
        double twoMonthIncomeGap = 0;
        double twoMonthExpenseGap = 0;
        double threeMonthIncomeGap = 0;
        double threeMonthExpenseGap = 0;
        double fourMonthIncomeGap = 0;
        double fourMonthExpenseGap = 0;
        double fiveMonthIncomeGap = 0;
        double fiveMonthExpenseGap = 0;
        double sixMonthIncomeGap = 0;
        double sixMonthExpenseGap = 0;
        double nineMonthIncomeGap = 0;
        double nineMonthExpenseGap = 0;
        double oneYearIncomeGap = 0;
        double oneYearExpenseGap = 0;

        for (User.Transaction transaction : transactions.values()) {
            if (transaction.getDate().after(oneMonthAgo) && transaction.getDate().before(currentDate)) {
                if (transaction.getType().equals("Income")) {
                    oneMonthIncomeGap += transaction.getAmount();
                } else {
                    oneMonthExpenseGap += transaction.getAmount();
                }
            }
        }
        for (User.Transaction transaction : transactions.values()) {
            if (transaction.getDate().after(twoMonthAgo) && transaction.getDate().before(oneMonthAgo)) {
                if (transaction.getType().equals("Income")) {
                    twoMonthIncomeGap += transaction.getAmount();
                } else {
                    twoMonthExpenseGap += transaction.getAmount();
                }
            }
        }
        for (User.Transaction transaction : transactions.values()) {
            if (transaction.getDate().after(threeMonthAgo) && transaction.getDate().before(twoMonthAgo)) {
                if (transaction.getType().equals("Income")) {
                    threeMonthIncomeGap += transaction.getAmount();
                } else {
                    threeMonthExpenseGap += transaction.getAmount();
                }
            }
        }
        for (User.Transaction transaction : transactions.values()) {
            if (transaction.getDate().after(fourMonthAgo) && transaction.getDate().before(threeMonthAgo)) {
                if (transaction.getType().equals("Income")) {
                    fourMonthIncomeGap += transaction.getAmount();
                } else {
                    fourMonthExpenseGap += transaction.getAmount();
                }
            }
        }
        for (User.Transaction transaction : transactions.values()) {
            if (transaction.getDate().after(fiveMonthAgo) && transaction.getDate().before(fourMonthAgo)) {
                if (transaction.getType().equals("Income")) {
                    fiveMonthIncomeGap += transaction.getAmount();
                } else {
                    fiveMonthExpenseGap += transaction.getAmount();
                }
            }
        }
        for (User.Transaction transaction : transactions.values()) {
            if (transaction.getDate().after(sixMonthAgo) && transaction.getDate().before(fiveMonthAgo)) {
                if (transaction.getType().equals("Income")) {
                    sixMonthIncomeGap += transaction.getAmount();
                } else {
                    sixMonthExpenseGap += transaction.getAmount();
                }
            }
        }
        for (User.Transaction transaction : transactions.values()) {
            if (transaction.getDate().after(nineMonthAgo) && transaction.getDate().before(sixMonthAgo)) {
                if (transaction.getType().equals("Income")) {
                    nineMonthIncomeGap += transaction.getAmount();
                } else {
                    nineMonthExpenseGap += transaction.getAmount();
                }
            }
        }
        for (User.Transaction transaction : transactions.values()) {
            if (transaction.getDate().after(oneYearAgo) && transaction.getDate().before(nineMonthAgo)) {
                if (transaction.getType().equals("Income")) {
                    oneYearIncomeGap += transaction.getAmount();
                } else {
                    oneYearExpenseGap += transaction.getAmount();
                }
            }
        }

        listener.getIncomeAndExpense(oneMonthIncomeGap, oneMonthExpenseGap, twoMonthIncomeGap, twoMonthExpenseGap,
                threeMonthIncomeGap, threeMonthExpenseGap, fourMonthIncomeGap, fourMonthExpenseGap,
                fiveMonthIncomeGap, fiveMonthExpenseGap, sixMonthIncomeGap, sixMonthExpenseGap,
                nineMonthIncomeGap, nineMonthExpenseGap, oneYearIncomeGap, oneYearExpenseGap);
    }

    public interface onIncomeAndExpenseCalculate {

        void getIncomeAndExpense(double oneMonthIncomeGap, double oneMonthExpenseGap, double twoMonthIncomeGap, double twoMonthExpenseGap,
                                 double threeMonthIncomeGap, double threeMonthExpenseGap, double fourMonthIncomeGap, double fourMonthExpenseGap,
                                 double fiveMonthIncomeGap, double fiveMonthExpenseGap, double sixMonthIncomeGap, double sixMonthExpenseGap,
                                 double nineMonthIncomeGap, double nineMonthExpenseGap, double oneYearIncomeGap, double oneYearExpenseGap);

    }

    public static Date getDateBeforeOrAfter(Date date, int field, int amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(field, amount);
        return cal.getTime();
    }
}


