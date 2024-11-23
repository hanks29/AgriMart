package com.example.agrimart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.agrimart.R;
import com.example.agrimart.data.model.ProductReview;

import java.util.List;

import per.wsj.library.AndRatingBar;

public class ProductReviewAdapter extends RecyclerView.Adapter<ProductReviewAdapter.ViewHolder> {

    private Context context;
    private List<ProductReview> productReviewList;


    public ProductReviewAdapter(Context context, List<ProductReview> productReviewList) {
        this.context = context;
        this.productReviewList = productReviewList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_rating, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductReview productReview = productReviewList.get(position);

        Glide.with(holder.itemView.getContext())
                .load(productReview.getImageResId())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(holder.productImage);

        // Set product name
        holder.productName.setText(productReview.getProductName());

        // Set product quantity
        holder.productQuantity.setText(String.valueOf(productReview.getQuantity()));

        // Set rating
        holder.ratingBar.setRating(productReview.getRating());

        if (position == productReviewList.size() - 1) {
            // Increase the height of the last item
            holder.main.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 2500));  // Set desired height for last item

            holder.itemView.post(() -> {
                RecyclerView recyclerView = (RecyclerView) holder.itemView.getParent();
                if (recyclerView != null) {
                    recyclerView.smoothScrollToPosition(getItemCount() - 1);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return productReviewList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView productName;
        TextView productQuantity;
        AndRatingBar ratingBar;
        TextView statusText;
        EditText reviewEditText;
        LinearLayout main;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.txt_productName);
            productQuantity = itemView.findViewById(R.id.txt_quantity);
            ratingBar = itemView.findViewById(R.id.rating);
            statusText = itemView.findViewById(R.id.Satus);
            reviewEditText = itemView.findViewById(R.id.edtReview);
            main = itemView.findViewById(R.id.main);


        }

    }


}
