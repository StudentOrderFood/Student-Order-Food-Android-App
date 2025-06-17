package prm392.orderfood.androidapp.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import dagger.hilt.android.AndroidEntryPoint;
import prm392.orderfood.androidapp.R;
import prm392.orderfood.androidapp.databinding.ActivityMainBinding;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private NavController navController;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHost != null) {
            navController = navHost.getNavController();
        }
        bottomNavigationView = binding.bottomNavigationView;
//        bottomNavigationView.setOnItemSelectedListener(item -> {
//            Log.d(TAG, "Selected item: " + item.getItemId());
//            return false;
//        });
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            Log.d(TAG, "Destination changed: " + destination.getLabel());
            if (destination.getId() == R.id.loginFragment || destination.getId() == R.id.loginFragment) {
                bottomNavigationView.setVisibility(View.GONE);
            } else {
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });

    }
}