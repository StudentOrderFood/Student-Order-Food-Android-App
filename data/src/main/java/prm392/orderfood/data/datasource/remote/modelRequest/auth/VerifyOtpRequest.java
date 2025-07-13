package prm392.orderfood.data.datasource.remote.modelRequest.auth;

public class VerifyOtpRequest {
    private String phone;
    private String code;

    public VerifyOtpRequest(String phone, String code) {
        this.phone = phone;
        this.code = code;
    }

    public String getPhone() {
        return phone;
    }

    public String getCode() {
        return code;
    }
}
