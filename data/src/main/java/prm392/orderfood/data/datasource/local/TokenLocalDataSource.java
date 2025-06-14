package prm392.orderfood.data.datasource.local;

import android.content.SharedPreferences;

import javax.inject.Inject;

public class TokenLocalDataSource {
    private final SharedPreferences sharedPreferences;

    @Inject
    public TokenLocalDataSource(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void saveAccessToken(String token) {
        sharedPreferences.edit().putString("ACCESS_TOKEN", token).apply();
    }

    public String getAccessToken() {
        return sharedPreferences.getString("ACCESS_TOKEN", null);
    }

    public void clearToken() {
        sharedPreferences.edit().remove("ACCESS_TOKEN").apply();
    }
}
