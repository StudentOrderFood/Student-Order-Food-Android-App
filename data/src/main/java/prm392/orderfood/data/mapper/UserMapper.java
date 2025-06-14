package prm392.orderfood.data.mapper;

import com.google.firebase.auth.FirebaseUser;

import prm392.orderfood.data.datasource.remote.modelResponse.LoginResponse;
import prm392.orderfood.domain.models.User;

public class UserMapper {
    public static User mapToDomain(LoginResponse response) {
        User user = new User();
        user.setId(response.getId());
        user.setEmail(response.getEmail());
        user.setFullName(response.getFullName());
        user.setAvatar(response.getAvatar());
        user.setRoleId(response.getRoleId()); // default role
        user.setPhone(response.getPhone());
        user.setAddress(response.getAddress());
        user.setDob(response.getDob());
        return user;
    }
    public static User mapToDomain(FirebaseUser firebaseUser) {
        User user = new User();
        user.setId(firebaseUser.getUid());
        user.setEmail(firebaseUser.getEmail());
        user.setFullName(firebaseUser.getDisplayName());
        user.setAvatar(firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null);
        // Note: FirebaseUser does not provide role, phone, address, or dob directly
        return user;
    }
}
