package prm392.orderfood.domain.models;

public class User {
    private String id;
    private String fullName;
    private String userName;
    private String password;
    private String email;
    private String phone;
    private String address;
    private String avatar;
    private String dob;
    private String roleId;

    public User() {
    }

    public User(String id, String fullName, String userName, String password, String email, String phone, String address, String avatar, String dob, String roleId) {
        this.id = id;
        this.fullName = fullName;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.avatar = avatar;
        this.dob = dob;
        this.roleId = roleId;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getDob() {
        return dob;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
}
