package com.example.agrimart.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.agrimart.databinding.ItemImageProductBinding;

import java.util.List;

public class ImageProductAdapter extends RecyclerView.Adapter<ImageProductAdapter.MyViewHolder> {

    public List<Uri> imageUris;

    public ImageProductAdapter(List<Uri> imageUris) {
        this.imageUris = imageUris;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemImageProductBinding binding = ItemImageProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bindData(imageUris.get(position));
        holder.binding.imageButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageUris.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ItemImageProductBinding binding;
        public MyViewHolder(@NonNull ItemImageProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindData(Uri uri) {
            Glide.with(binding.getRoot())
                    .load(uri)
                    .into(binding.imageView11);
        }
    }
}
