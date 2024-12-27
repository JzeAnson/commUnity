package com.example.community;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PastOrdersFragment extends Fragment {

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.fragment_past_orders, container, false);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String customerName = documentSnapshot.getString("userName");
                    fetchOrders(customerName);
                })
                .addOnFailureListener(e -> Log.e("PastOrders", "Error fetching user data", e));

        // Initialize navigation buttons
        Button btnFoodListing = view.findViewById(R.id.btn_food_listing);
        Button btnPastOrders = view.findViewById(R.id.btn_past_orders);

        // Set click listeners for navigation
        btnFoodListing.setOnClickListener(v -> {
            replaceFragment(new FoodListingFragment());
        });

        btnPastOrders.setOnClickListener(v -> {
            Log.i("PastOrdersFragment", "Already on Past Orders fragment");
        });

        return view;
    }

    private void replaceFragment(Fragment fragment) {
        if (fragment == null) {
            Log.e("PastOrdersFragment", "Fragment is null. Cannot replace.");
            return;
        }
        Log.i("PastOrdersFragment", "Replacing fragment with: " + fragment.getClass().getSimpleName());
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void fetchOrders(String customerName) {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        ordersRef.orderByChild("customerName").equalTo(customerName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<OrderItem> orderList = new ArrayList<>();
                        for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                            OrderItem order = orderSnapshot.getValue(OrderItem.class);
                            if (order != null) orderList.add(order);
                        }
                        // Sort and bind data to RecyclerView
                        bindOrders(orderList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("PastOrders", "Error fetching orders", error.toException());
                    }
                });
    }

    private void bindOrders(List<OrderItem> orderList) {
        // Sort orders by date and time
        Collections.sort(orderList, (o1, o2) -> {
            int dateCompare = o2.getOrderDate().compareTo(o1.getOrderDate());
            if (dateCompare != 0) return dateCompare;
            return o2.getOrderTime().compareTo(o1.getOrderTime());
        });

        OrderAdapter adapter = new OrderAdapter(getContext(), orderList);
        recyclerView.setAdapter(adapter);
    }

}