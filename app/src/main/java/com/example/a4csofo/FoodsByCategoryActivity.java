package com.example.a4csofo;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class FoodsByCategoryActivity extends AppCompatActivity {

    private TextView tvCategoryTitle;
    private RecyclerView rvFoodsByCategory;
    private AdminFoodAdapter adapter;
    private List<AdminFoodAdapter.FoodItem> foodsList = new ArrayList<>();

    private DatabaseReference foodsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_foodcategories);

        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        rvFoodsByCategory = findViewById(R.id.rvFoodsByCategory);

        String category = getIntent().getStringExtra("category");
        tvCategoryTitle.setText(category);

        foodsRef = FirebaseDatabase.getInstance().getReference("foods");

        rvFoodsByCategory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminFoodAdapter(this, foodsList);
        rvFoodsByCategory.setAdapter(adapter);

        loadFoodsByCategory(category);
    }

    private void loadFoodsByCategory(String category) {
        foodsRef.orderByChild("category").equalTo(category)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        foodsList.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            AdminFoodAdapter.FoodItem food = data.getValue(AdminFoodAdapter.FoodItem.class);
                            if (food != null) foodsList.add(food);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(FoodsByCategoryActivity.this, "Failed to load foods: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
