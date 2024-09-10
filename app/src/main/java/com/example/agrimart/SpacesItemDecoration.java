package com.example.agrimart;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private final int space;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view); // Vị trí của item hiện tại
        int spanCount = ((GridLayoutManager) parent.getLayoutManager()).getSpanCount();

        if (position >= spanCount) {
            outRect.top = space;
        }
        if(position%spanCount==0){
            outRect.left = space;
            outRect.right = space / 2;
        }
        else {
            outRect.left = space / 2;
            outRect.right = space;
        }

    }
}

