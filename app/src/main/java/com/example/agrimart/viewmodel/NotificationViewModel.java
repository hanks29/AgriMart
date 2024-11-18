package com.example.agrimart.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.agrimart.data.model.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class NotificationViewModel extends ViewModel {
    private final MutableLiveData<List<Notification>> notificationsLiveData = new MutableLiveData<>();
    private final List<Notification> notificationList = new ArrayList<>();

    public NotificationViewModel() {
        fetchNotificationsFromFirestore();
    }

    public LiveData<List<Notification>> getNotificationsLiveData() {
        return notificationsLiveData;
    }

    private void fetchNotificationsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("orders")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        return;
                    }

                    if (snapshots != null && !snapshots.isEmpty()) {
                        notificationList.clear();

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.MODIFIED) {
                                String status = dc.getDocument().getString("status");
                                String orderCode = dc.getDocument().getString("order_code");
                                String imageUrl = dc.getDocument().getString("image_url");
                                long timestamp = System.currentTimeMillis();

                                String title = "";
                                String message = "";

                                switch (status) {
                                    case "Pending":
                                        title = "Đặt hàng thành công!";
                                        message = "Đơn hàng của bạn đang được xử lý và sẽ sớm chuyển đến đơn vị giao hàng.";
                                        break;
                                    case "Delivered":
                                        title = "Đơn hàng đã được giao thành công!";
                                        message = "Đơn hàng của bạn đã được giao thành công. Bạn có hài lòng với sản phẩm đã nhận? Để lại đánh giá của bạn để giúp chúng tôi phục vụ bạn tốt hơn trong tương lai!";
                                        break;
                                    case "Failed":
                                        title = "Giao hàng không thành công";
                                        message = "Chúng tôi đã thử giao hàng nhưng không thành công. Đơn hàng của bạn sẽ được giao lại vào ngày tiếp theo hoặc bạn có thể liên hệ với chúng tôi để hỗ trợ.";
                                        break;
                                    case "On the way":
                                        title = "Đơn hàng đang trên đường đến bạn!";
                                        message = "Đơn hàng của bạn đã được giao cho đơn vị vận chuyển và đang trên đường đến địa chỉ của bạn. Vui lòng theo dõi để biết thời gian giao hàng dự kiến.";
                                        break;
                                    case "Shipping":
                                        title = "Đơn hàng đang trong quá trình vận chuyển";
                                        message = "Đơn hàng của bạn hiện đang trong quá trình vận chuyển. Bạn có thể theo dõi tình trạng giao hàng trong mục Theo dõi đơn hàng.";
                                        break;
                                    case "Preparing":
                                        title = "Đơn hàng đang được chuẩn bị!";
                                        message = "Đơn hàng của bạn đang được đóng gói và chuẩn bị để gửi đi. Chúng tôi sẽ thông báo cho bạn khi đơn hàng được giao cho đơn vị vận chuyển.";
                                        break;
                                    case "Success":
                                        title = "Đặt hàng thành công!";
                                        message = "Đơn hàng của bạn đang được xử lý và sẽ sớm chuyển đến đơn vị giao hàng.";
                                        break;
                                }

                                if (!title.isEmpty() && !message.isEmpty()) {
                                    Notification notification = new Notification(title, message, timestamp, imageUrl);
                                    notificationList.add(notification);
                                    saveNotificationToFirestore(notification);
                                }
                            }
                        }

                        notificationsLiveData.setValue(notificationList);
                    }
                });
    }

    private void saveNotificationToFirestore(Notification notification) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("notifications").add(notification)
                .addOnSuccessListener(documentReference -> {})
                .addOnFailureListener(e -> {});
    }
}