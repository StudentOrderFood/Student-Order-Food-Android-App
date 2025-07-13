package prm392.orderfood.data.datasource.remote.modelRequest.auth;

public class EmailRegisterRequest {
    private String idToken;
    private String phoneNumber;

    public EmailRegisterRequest(String idToken, String phoneNumber) {
        this.idToken = idToken;
        this.phoneNumber = phoneNumber;
    }

    public String getIdToken() {
        return idToken;
    }
    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
