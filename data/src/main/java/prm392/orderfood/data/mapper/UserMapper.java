package prm392.orderfood.data.mapper;

import prm392.orderfood.data.datasource.remote.modelResponse.user.GetUserResponse;
import prm392.orderfood.domain.models.users.UserProfile;

public class UserMapper {
    public static UserProfile mapToUserProfileDomain(GetUserResponse response) {
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(response.getUserId());
        userProfile.setFullName(response.getFullName());
        userProfile.setEmail(response.getEmail());
        userProfile.setPhone(response.getPhone());
        userProfile.setAddress(response.getAddress());
        userProfile.setAvatar(response.getAvatar());
        userProfile.setDob(response.getDob());
        userProfile.setRoleId(response.getRoleId());
        userProfile.setRoleName(response.getRoleName());
        return userProfile;
    }
}
