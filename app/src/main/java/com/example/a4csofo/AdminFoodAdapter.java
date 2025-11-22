package com.example.a4csofo;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdminFoodAdapter extends RecyclerView.Adapter<AdminFoodAdapter.FoodViewHolder> {

    private Context context;
    private List<FoodItem> foodList;

    public AdminFoodAdapter(Context context, List<FoodItem> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem food = foodList.get(position);

        holder.tvName.setText(food.name);
        holder.tvPrice.setText("₱" + String.format("%.2f", food.price));
        holder.tvDesc.setText(food.description);
        holder.tvCategory.setText(food.category);
        holder.tvPrepTime.setText(food.prepTime + " mins");

        // Use FoodItem's own method to get Bitmap
        Bitmap bitmap = food.getBitmap();
        if (bitmap != null) {
            holder.ivFoodImage.setImageBitmap(bitmap);
        } else {
            holder.ivFoodImage.setImageResource(R.drawable.ic_placeholder);
        }

        // Optional: Add to cart or any click action
        holder.btnAddCart.setOnClickListener(v ->
                Toast.makeText(context, food.name + " added to cart!", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoodImage;
        TextView tvName, tvPrice, tvDesc, tvCategory, tvPrepTime;
        Button btnAddCart;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoodImage = itemView.findViewById(R.id.ivFoodImage);
            tvName = itemView.findViewById(R.id.tvFoodName);
            tvPrice = itemView.findViewById(R.id.tvFoodPrice);
            tvDesc = itemView.findViewById(R.id.tvFoodDesc);
            tvCategory = itemView.findViewById(R.id.tvFoodCategory);
            tvPrepTime = itemView.findViewById(R.id.tvFoodPrepTime);
            btnAddCart = itemView.findViewById(R.id.btnAddCart);
        }
    }

    // FoodItem model class
    public static class FoodItem {
        public String name;
        public double price;
        public String prepTime;
        public String description;
        public String category;
        public String base64Image;

        public FoodItem() {}

        public FoodItem(String name, double price, String prepTime, String description, String category, String base64Image) {
            this.name = name;
            this.price = price;
            this.prepTime = prepTime;
            this.description = description;
            this.category = category;
            this.base64Image = base64Image;
        }

        // Convert base64 to Bitmap
        public Bitmap getBitmap() {
            if (base64Image == null || base64Image.isEmpty()) return null;
            byte[] decodedBytes = android.util.Base64.decode(base64Image, android.util.Base64.DEFAULT);
            return android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        }
    }
}
