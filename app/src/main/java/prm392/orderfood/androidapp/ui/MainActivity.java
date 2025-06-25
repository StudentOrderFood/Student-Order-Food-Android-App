package prm392.orderfood.androidapp.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
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
        bottomNavigationView.setOnItemSelectedListener(item -> onNavigationItemSelected(item.getItemId()));
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
//            Log.d(TAG, "Destination changed: " + destination.getLabel());
            if (destination.getId() == R.id.loginFragment || destination.getId() == R.id.loginFragment) {
                bottomNavigationView.setVisibility(View.GONE);
            } else if (destination.getId() == R.id.introFragment) {
                bottomNavigationView.setVisibility(View.GONE);
            } else {
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });
    }

    // Func handle bottom navigation item selection
    private boolean onNavigationItemSelected(int itemId) {
        NavOptions navOptions = new NavOptions.Builder()
                .setLaunchSingleTop(true)            // Không tạo fragment mới nếu đã ở đó
//                .setPopUpTo(R.id.nav_graph, false)  // Xoá fragment cũ trong stack, giữ lại main_graph
                .build();
        if (itemId == R.id.navigation_home) {
            navController.navigate(R.id.action_global_homeFragment, null, navOptions);
            return true;
        } else if (itemId == R.id.navigation_profile) {
            navController.navigate(R.id.action_global_profileFragment, null, navOptions);
            return true;
        }
        return false;
    }
}