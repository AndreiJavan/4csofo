package com.example.a4csofo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private EditText searchBox;
    private BottomNavigationView bottomNavigation;

    private AdminUserAdapter userAdapter;
    private ArrayList<AdminUserModel> userList;

    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Find views
        recyclerView = findViewById(R.id.recyclerUsers);
        progressBar = findViewById(R.id.progressBar);
        searchBox = findViewById(R.id.editSearch);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // RecyclerView setup
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        userAdapter = new AdminUserAdapter(userList, this::deleteUser);
        recyclerView.setAdapter(userAdapter);

        // Firebase reference
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        loadUsers();

        // Search functionality
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                userAdapter.getFilter().filter(s);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Bottom Navigation click listener
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_orders) {
                startActivity(new Intent(this, ManageOrdersActivity.class));
            } else if (id == R.id.nav_users) {
                startActivity(new Intent(this, UsersActivity.class));
            } else if (id == R.id.nav_categories) {
                startActivity(new Intent(this, AdminCategoriesActivity.class));
            } else if (id == R.id.nav_menu_items) {
                startActivity(new Intent(this, AdminMenuItemsActivity.class));
            }
            return true;
        });
    }

    private void loadUsers() {
        progressBar.setVisibility(ProgressBar.VISIBLE);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                progressBar.setVisibility(ProgressBar.GONE);
                userList.clear();

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String uid = userSnapshot.getKey();
                    String name = userSnapshot.child("name").getValue(String.class);
                    String email = userSnapshot.child("email").getValue(String.class);
                    String role = userSnapshot.child("role").getValue(String.class);

                    if (uid != null) {
                        userList.add(new AdminUserModel(uid, name, email, role));
                    }
                }

                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                progressBar.setVisibility(ProgressBar.GONE);
                Toast.makeText(AdminDashboardActivity.this,
                        "Failed to load users: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteUser(String userUid) {
        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete this user?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    usersRef.child(userUid).removeValue()
                            .addOnSuccessListener(e ->
                                    Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Failed to delete user", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
