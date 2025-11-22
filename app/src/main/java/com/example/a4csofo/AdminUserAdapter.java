package com.example.a4csofo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.UserViewHolder> implements Filterable {

    private List<AdminUserModel> userList;
    private List<AdminUserModel> userListFull;
    private OnUserDeleteListener deleteListener;

    public interface OnUserDeleteListener {
        void onDelete(String userUid);
    }

    public AdminUserAdapter(List<AdminUserModel> userList, OnUserDeleteListener deleteListener) {
        this.userList = userList;
        this.userListFull = new ArrayList<>(userList);
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        AdminUserModel user = userList.get(position);
        holder.tvName.setText(user.getName());
        holder.tvEmail.setText(user.getEmail());
        holder.tvRole.setText(user.getRole());

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(user.getUid());
            }
        });
    }

    @Override
    public int getItemCount() { return userList.size(); }

    @Override
    public Filter getFilter() { return userFilter; }

    private final Filter userFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<AdminUserModel> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(userListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (AdminUserModel user : userListFull) {
                    if (user.getName().toLowerCase().contains(filterPattern) ||
                            user.getEmail().toLowerCase().contains(filterPattern) ||
                            user.getRole().toLowerCase().contains(filterPattern)) {
                        filteredList.add(user);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            userList.clear();
            userList.addAll((List<AdminUserModel>) results.values);
            notifyDataSetChanged();
        }
    };

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvRole;
        ImageView btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.txtName);
            tvEmail = itemView.findViewById(R.id.txtEmail);
            tvRole = itemView.findViewById(R.id.txtRole);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
