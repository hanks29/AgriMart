package com.example.agrimart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.agrimart.R;
import com.example.agrimart.data.model.Rating;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RatingShopAdapter extends RecyclerView.Adapter<RatingShopAdapter.ViewHolder> {

    private Context context;
    private final List<Rating> ratings;

    public RatingShopAdapter(Context context, List<Rating> ratings) {
        this.context = context;
        this.ratings = ratings;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rating_shop, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Rating rating = ratings.get(position);


        holder.itemName.setText(rating.getFullName());

        if (rating.getUserImage() != null && !rating.getUserImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(rating.getUserImage())
                    .apply(RequestOptions.circleCropTransform()) // Bo tròn ảnh khi tải lên
                    .placeholder(R.drawable.user_img) // ảnh mặc định khi đang tải
                    .error(R.drawable.user_img) // ảnh mặc định nếu URL không tồn tại hoặc tải ảnh lỗi
                    .into(holder.userImage);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.error_image)
                    .apply(RequestOptions.circleCropTransform()) // Bo tròn ảnh mặc định
                    .into(holder.userImage);
        }


        if (rating.getProductImg() != null && !rating.getProductImg().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(rating.getProductImg())
                    .placeholder(R.drawable.img) // ảnh mặc định khi đang tải
                    .error(R.drawable.error_image) // ảnh mặc định nếu URL không tồn tại hoặc tải ảnh lỗi
                    .into(holder.productImage);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.error_image)
                    .apply(RequestOptions.circleCropTransform()) // Bo tròn ảnh mặc định
                    .into(holder.productImage);
        }

        if (rating.getRating() != null) {
            holder.itemRatingBar.setRating(Float.parseFloat(rating.getRating()));
        } else {
            holder.itemRatingBar.setRating(0);
        }
        // Set date
        if (rating.getUpdatedAt() != null) {
            com.google.firebase.Timestamp timestamp = (com.google.firebase.Timestamp) rating.getUpdatedAt();
            Date date = timestamp.toDate();

            // Định dạng ngày theo kiểu dd-MM-yyyy
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDate = sdf.format(date);

            holder.itemDate.setText(formattedDate);  // Hiển thị ngày đã định dạng
        }


        // Set review
        holder.itemReview.setText(rating.getReview());

        holder.productName.setText(rating.getProductName());

    }

    @Override
    public int getItemCount() {
        return ratings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView userImage, productImage;
        private TextView itemName, itemDate, itemReview, productName;
        private RatingBar itemRatingBar;

        public ViewHolder(View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemRatingBar = itemView.findViewById(R.id.itemRatingBar);
            itemDate = itemView.findViewById(R.id.itemDate);
            itemReview = itemView.findViewById(R.id.itemReview);
            productImage = itemView.findViewById(R.id.img_product);
            productName = itemView.findViewById(R.id.tv_product_name);
        }
    }
}

