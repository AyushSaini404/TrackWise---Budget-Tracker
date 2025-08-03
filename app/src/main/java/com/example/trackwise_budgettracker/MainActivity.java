package com.example.trackwise_budgettracker;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    LinearLayout layoutHome, layoutGraph, layoutSettings;
    BottomNavigationView bottomNavigationView;

    HashMap<String, String> amountMap = new HashMap<>();
    ArrayList<CategoryAmount> categoryAmountList = new ArrayList<>();
    CategoryAmountAdapter adapter;

    RecyclerView recyclerView;

    int[] imageIds = {
            R.id.grocery, R.id.education, R.id.bills, R.id.food,
            R.id.travel, R.id.repairs, R.id.vacations, R.id.pets,
            R.id.gifts, R.id.others
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layoutHome = findViewById(R.id.layout_home);
        layoutGraph = findViewById(R.id.layout_graph);
        layoutSettings = findViewById(R.id.layout_settings);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        recyclerView = findViewById(R.id.graphRecyclerView);

        adapter = new CategoryAmountAdapter(categoryAmountList, new CategoryAmountAdapter.OnItemDeleteListener() {
            @Override
            public void onItemDeleted(int position, CategoryAmount item) {
                categoryAmountList.remove(position);

                SharedPreferences prefs = getSharedPreferences("BudgetPrefs", MODE_PRIVATE);
                prefs.edit().remove(item.getCategory()).apply();

                adapter.notifyItemRemoved(position);
                Toast.makeText(MainActivity.this, "Deleted: " + item.getCategory(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        setupImageViewClickListeners();
        loadSavedData();
        showHome();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                showHome();
                return true;
            } else if (itemId == R.id.nav_graph) {
                showGraph();
                return true;
            } else if (itemId == R.id.nav_settings) {
                showSettings();
                return true;
            }

            return false;
        });
    }

    private void showHome() {
        layoutHome.setVisibility(View.VISIBLE);
        layoutGraph.setVisibility(View.GONE);
        layoutSettings.setVisibility(View.GONE);
    }

    private void showGraph() {
        layoutHome.setVisibility(View.GONE);
        layoutGraph.setVisibility(View.VISIBLE);
        layoutSettings.setVisibility(View.GONE);
    }

    private void showSettings() {
        layoutHome.setVisibility(View.GONE);
        layoutGraph.setVisibility(View.GONE);
        layoutSettings.setVisibility(View.VISIBLE);
    }

    private void setupImageViewClickListeners() {
        View.OnClickListener listener = view -> {
            String categoryName = "";

            int viewId = view.getId();

            if (viewId == R.id.grocery) categoryName = "Groceries";
            else if (viewId == R.id.education) categoryName = "Education";
            else if (viewId == R.id.bills) categoryName = "Bills";
            else if (viewId == R.id.food) categoryName = "Food";
            else if (viewId == R.id.travel) categoryName = "Travel";
            else if (viewId == R.id.repairs) categoryName = "Repairs";
            else if (viewId == R.id.vacations) categoryName = "Vacations";
            else if (viewId == R.id.pets) categoryName = "Pets";
            else if (viewId == R.id.gifts) categoryName = "Gifts";
            else if (viewId == R.id.others) categoryName = "Others";

            showAmountBottomSheet(categoryName);
        };

        for (int id : imageIds) {
            ImageView image = findViewById(id);
            image.setOnClickListener(listener);
        }
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void showAmountBottomSheet(String categoryName) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        TextView categoryTitle = bottomSheetView.findViewById(R.id.categoryTitle);
        EditText amountInput = bottomSheetView.findViewById(R.id.amountInput);
        View saveButton = bottomSheetView.findViewById(R.id.saveButton);

        categoryTitle.setText("Enter amount for: " + categoryName);

        saveButton.setOnClickListener(v -> {
            String amount = amountInput.getText().toString().trim();
            if (!amount.isEmpty()) {
                amountMap.put(categoryName, amount);

                SharedPreferences prefs = getSharedPreferences("BudgetPrefs", MODE_PRIVATE);
                prefs.edit().putString(categoryName, amount).apply();

                boolean updated = false;
                for (int i = 0; i < categoryAmountList.size(); i++) {
                    if (categoryAmountList.get(i).getCategory().equals(categoryName)) {
                        categoryAmountList.set(i, new CategoryAmount(categoryName, amount));
                        updated = true;
                        break;
                    }
                }
                if (!updated) {
                    categoryAmountList.add(new CategoryAmount(categoryName, amount));
                }

                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Saved ₹" + amount + " for " + categoryName, Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            } else {
                amountInput.setError("Please enter an amount");
            }
        });

        bottomSheetDialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadSavedData() {
        SharedPreferences prefs = getSharedPreferences("BudgetPrefs", MODE_PRIVATE);

        String[] categories = {
                "Groceries", "Education", "Bills", "Food", "Travel",
                "Repairs", "Vacations", "Pets", "Gifts", "Others"
        };

        for (String category : categories) {
            String amount = prefs.getString(category, null);
            if (amount != null) {
                amountMap.put(category, amount);
                categoryAmountList.add(new CategoryAmount(category, amount));
            }
        }

        if (adapter != null) adapter.notifyDataSetChanged();
    }
}
