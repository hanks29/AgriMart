package com.example.agrimart.ui.Cart;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.agrimart.R;

public class ConfirmDeleteDialogFragment extends DialogFragment {

    public interface OnDeleteConfirmedListener {
        void onDeleteConfirmed();
    }

    private OnDeleteConfirmedListener listener;

    public ConfirmDeleteDialogFragment(OnDeleteConfirmedListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Tạo dialog từ layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_confirm_delete, null);

        Button btnYes = view.findViewById(R.id.btn_yes);
        Button btnNo = view.findViewById(R.id.btn_no);

        // Thiết lập các sự kiện cho các nút
        btnYes.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteConfirmed();  // Gọi callback khi xóa được xác nhận
            }
            dismiss();  // Đóng dialog
        });

        btnNo.setOnClickListener(v -> dismiss());  // Đóng dialog khi nhấn "Không"

        // Tạo và trả về dialog
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(view);
        return dialog;
    }
}
