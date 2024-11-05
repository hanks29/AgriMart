package com.example.agrimart.viewmodel;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.agrimart.R;
import com.example.agrimart.data.model.User;
import com.example.agrimart.ui.MyProfile.MyAccount.MyAccountActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MyAccountViewModel extends AndroidViewModel {
    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;
    private final FirebaseStorage storage;
    private final StorageReference storageReference;
    private String urlImage;

    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> changePassword = new MutableLiveData<Boolean>();
    private final MutableLiveData<String> genderUpdateStatus = new MutableLiveData<>();
    private MutableLiveData<String> imageUrlLiveData = new MutableLiveData<>();

    public LiveData<String> getImageUrlLiveData() {
        return imageUrlLiveData;
    }

    public LiveData<String> getGenderUpdateStatus() {
        return genderUpdateStatus;
    }

    public LiveData<Boolean> getIsChangePassword() {
        return changePassword;
    }

    public MyAccountViewModel(@NonNull Application application) {
        super(application);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("userImages");
        loadUserInfo();
    }

    public LiveData<User> getUser() {
        return userLiveData;
    }

    public void loadUserInfo() {
        String userId = auth.getCurrentUser().getUid();
        if (auth.getCurrentUser() != null) {
            for (UserInfo profile : auth.getCurrentUser().getProviderData()) {
                String providerId = profile.getProviderId();
                if (providerId.equals("google.com") || providerId.equals("facebook.com")) {
                    changePassword.setValue(true);
                }
            }
        }

        firestore.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String name = document.getString("fullName");
                            String phone = document.getString("phoneNumber");
                            String email = document.getString("email");
                            String sex = document.getString("sex");
                            String birthDate = document.getString("birthDate");
                            urlImage = document.getString("userImage");

                            User user = new User();
                            user.setFullName(name);
                            user.setPhoneNumber(phone);
                            user.setEmail(email);
                            user.setSex(sex);
                            user.setBirthDate(birthDate);
                            user.setUserImage(urlImage);
                            userLiveData.postValue(user);
                        }
                    }
                });
    }

    public void loadImage(Context context, ImageView userImage) {
        if (urlImage != null && !urlImage.isEmpty()) {
            Glide.with(context)
                    .load(urlImage)
                    .apply(RequestOptions.circleCropTransform()) // Bo tròn ảnh khi tải lên
                    .placeholder(R.drawable.user_img) // Ảnh mặc định khi đang tải
                    .error(R.drawable.user_img) // Ảnh mặc định nếu URL không tồn tại hoặc tải ảnh lỗi
                    .into(userImage);
        } else {
            // Hiển thị ảnh mặc định nếu URL rỗng
            Glide.with(context)
                    .load(R.drawable.user_img)
                    .apply(RequestOptions.circleCropTransform()) // Bo tròn ảnh mặc định
                    .into(userImage);
        }
    }

    public void saveGenderToFirestore(String gender) {
        String userId = auth.getCurrentUser().getUid();
        firestore.collection("users").document(userId)
                .update("sex", gender)
                .addOnSuccessListener(aVoid -> {
                    // Cập nhật thành công
                    showToast("Đã cập nhật giới tính");
                })
                .addOnFailureListener(e -> {
                    // Cập nhật thất bại
                    showToast("Lỗi khi cập nhật giới tính: " + e.getMessage());
                });
    }

    public void saveBirthDateToFirestore(String date) {
        String userId = auth.getCurrentUser().getUid();
        firestore.collection("users").document(userId)
                .update("birthDate", date)
                .addOnSuccessListener(aVoid ->
                {showToast("Đã cập nhật ngày sinh");})
                .addOnFailureListener(e ->
                {
                    // Cập nhật thất bại
                    showToast("Lỗi khi cập nhật ngày sinh: " + e.getMessage());
                });
    }

    public void uploadImageToFirebase(Context context, Uri imageUri, ImageUploadCallback callback) {
        String userId = auth.getCurrentUser().getUid();
        StorageReference fileReference = FirebaseStorage.getInstance().getReference("users/" + userId + "/profile.jpg");

        try {
            // Giảm kích thước và chất lượng ảnh
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); // 50 là mức nén chất lượng
            byte[] data = baos.toByteArray();

            // Tải lên ảnh nén
            UploadTask uploadTask = fileReference.putBytes(data);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    saveImageUrlToFirestore(uri.toString());
                    imageUrlLiveData.setValue(uri.toString());

                    // Gọi callback.onSuccess với URL ảnh nếu tải lên thành công
                    callback.onSuccess(uri.toString());
                });
            }).addOnFailureListener(e -> {
                showToast("Upload failed: " + e.getMessage());
                // Gọi callback.onFailure nếu có lỗi xảy ra
                callback.onFailure();
            });
        } catch (IOException e) {
            e.printStackTrace();
            showToast("Error getting image: " + e.getMessage());
            // Gọi callback.onFailure nếu có lỗi trong quá trình đọc ảnh
            callback.onFailure();
        }
    }


    public void saveImageUrlToFirestore(String imageUrl) {
        String userId = auth.getCurrentUser().getUid();
        firestore.collection("users").document(userId)
                .update("userImage", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    // Đã cập nhật thành công
                    showToast("Đã cập nhật hình ảnh thành công");
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi khi lưu URL
                    showToast("Error updating image URL: " + e.getMessage());
                });
    }

    private void showToast(String message) {
        // Use the activity context to show the toast
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show();
    }

    public interface ImageUploadCallback {
        void onSuccess(String imageUrl);
        void onFailure();
    }

}
