package prm392.orderfood.data.di;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import prm392.orderfood.data.datasource.local.TokenLocalDataSource;
import prm392.orderfood.data.datasource.remote.api.AuthApiService;
import prm392.orderfood.data.network.AuthInterceptor;
import prm392.orderfood.data.network.RetrofitClient;
import retrofit2.Retrofit;


@Module
@InstallIn(SingletonComponent.class)
public class AppModule {
    @Provides
    @Singleton
    public SharedPreferences provideSharedPreferences(@ApplicationContext Context context) {
        return context.getSharedPreferences("auth", Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    public AuthInterceptor provideAuthInterceptor(TokenLocalDataSource tokenLocalDataSource) {
        return new AuthInterceptor(tokenLocalDataSource);
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(AuthInterceptor authInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .build();
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return RetrofitClient.create(okHttpClient);
    }

    @Provides
    @Singleton
    public FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Singleton
    @Provides
    public AuthApiService provideRetrofitAPI(Retrofit retrofit) {
        return retrofit.create(AuthApiService.class);
    }
    // Nếu có các ApiService khác, cung cấp chúng tương tự như AuthApiService
}