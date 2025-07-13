package prm392.orderfood.androidapp.ui.fragment.auth;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import dagger.hilt.android.AndroidEntryPoint;
import prm392.orderfood.androidapp.R;
import prm392.orderfood.androidapp.databinding.FragmentLoginBinding;
import prm392.orderfood.androidapp.ui.states.SignInState;
import prm392.orderfood.androidapp.viewModel.AuthViewModel;
import prm392.orderfood.androidapp.viewModel.UserViewModel;
import prm392.orderfood.domain.models.auth.Token;

@AndroidEntryPoint
public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private FragmentLoginBinding binding;
    private AuthViewModel authViewModel;
    private UserViewModel mUserViewModel;
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> signInActivityResultLauncher;

    private NavController navController;


    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Google Sign-In client
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // IMPORTANT!
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        // ActivityResultLauncher for handling result of Google Sign-In intent
        signInActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    handleSignInResult(task);
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        mUserViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        navController = Navigation.findNavController(requireView());
        setupObservers();
        setupListeners();
    }

    private void setupListeners() {
        binding.btnSignIn.setOnClickListener(v -> {
            signIn();
        });

        binding.btnLogin.setOnClickListener(v -> {
            String identifier = binding.etIdentifier.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            if (identifier.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter both identifier and password", Toast.LENGTH_SHORT).show();
                return;
            }
            authViewModel.loginShopOwner(identifier, password);
        });

        setupTVSignUpListener();
    }

    private void setupObservers() {
        authViewModel.getSignInState().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof SignInState.Loading) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnSignIn.setEnabled(false);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnSignIn.setEnabled(true);
            }

            if (state instanceof SignInState.Success) {
                Token token = ((SignInState.Success) state).getToken();
//                Log.d(TAG, "Login success: " + token.getAccessToken());
                Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show();
                // Navigate to home fragment
                navController.navigate(R.id.action_loginFragment_to_homeFragment);

                mUserViewModel.fetchUserProfile(); // Fetch user profile after login
            } else if (state instanceof SignInState.Error) {
                String error = ((SignInState.Error) state).getErrorMessage();
//                Log.e(TAG, "Login error: " + error);
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        authViewModel.getNavigateTo().observe(getViewLifecycleOwner(), destination -> {
            if (destination != null) {
                switch (destination) {
                    case "phone_verification":
                        navController.navigate(R.id.action_loginFragment_to_phoneInputFragment);
                        break;
                    case "home":
                        navController.navigate(R.id.action_loginFragment_to_homeFragment);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void setupTVSignUpListener() {
        binding.tvSignUp.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_loginFragment_to_registerFragment);
        });
        // Underline the TextView and set color
        binding.tvSignUp.setPaintFlags(binding.tvSignUp.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        // Make the TextView clickable and focusable
        binding.tvSignUp.setClickable(true);
        binding.tvSignUp.setFocusable(true);
    }

    private void signIn() {
        mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            signInActivityResultLauncher.launch(signInIntent);
        });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            signInWithGoogleAccount(account);
        } catch (ApiException e) {
            Log.w(TAG, "Google sign in failed", e);
            authViewModel.handleSignInError(e.getLocalizedMessage());
        }
    }

    private void signInWithGoogleAccount(GoogleSignInAccount account) {
        String idToken = account.getIdToken();
        String email = account.getEmail();

        if (email == null || !email.endsWith(".edu.vn")) {
            Toast.makeText(requireContext(), "Please use a valid .edu.vn email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idToken != null) {
            authViewModel.loginWithGoogle(email, idToken);
        } else {
            authViewModel.handleSignInError("ID Token is null");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}