package com.example.a4csofo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class AdminCategoriesActivity extends AppCompatActivity {

    private EditText etCategoryName;
    private Button btnAddCategory;
    private ListView listViewCategories;

    private DatabaseReference categoriesRef;
    private List<String> categoriesList = new ArrayList<>();
    private ArrayAdapter<String> categoriesAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_categories);

        etCategoryName = findViewById(R.id.etCategoryName);
        btnAddCategory = findViewById(R.id.btnAddCategory);
        listViewCategories = findViewById(R.id.listViewCategories);

        categoriesRef = FirebaseDatabase.getInstance().getReference("categories");

        categoriesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categoriesList);
        listViewCategories.setAdapter(categoriesAdapter);

        loadCategories();

        btnAddCategory.setOnClickListener(v -> addCategory());

        // Open FoodsByCategoryActivity when a category is tapped
        listViewCategories.setOnItemClickListener((parent, view, position, id) -> {
            String category = categoriesList.get(position);
            Intent intent = new Intent(AdminCategoriesActivity.this, FoodsByCategoryActivity.class);
            intent.putExtra("category", category);
            startActivity(intent);
        });
    }

    private void loadCategories() {
        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                categoriesList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String cat = data.getValue(String.class);
                    if (cat != null) categoriesList.add(cat);
                }
                categoriesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(AdminCategoriesActivity.this, "Failed to load categories: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addCategory() {
        String category = etCategoryName.getText().toString().trim();
        if (category.isEmpty()) {
            Toast.makeText(this, "Enter category name", Toast.LENGTH_SHORT).show();
            return;
        }

        String key = categoriesRef.push().getKey();
        if (key != null) {
            categoriesRef.child(key).setValue(category)
                    .addOnSuccessListener(aVoid -> etCategoryName.setText(""))
                    .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}
