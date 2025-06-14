package prm392.orderfood.domain.repositories.auth;

import com.google.android.gms.tasks.Task;

import io.reactivex.Single;
import prm392.orderfood.domain.models.Token;
import prm392.orderfood.domain.models.User;
import retrofit2.Response;
/**
 * Interface representing the authentication repository.
 * Provides methods for handling user authentication and session management.
 */
public interface AuthRepository {

    /**
     * Logs in the user by sending the Firebase ID token to the server.
     *
     * @return A Single that emits a Response containing the Token object if successful,
     *         or an error if the login fails.
     */
    Single<Response<Token>> logIn();

    /**
     * Retrieves the currently authenticated user.
     *
     * @return The current User object, or null if no user is authenticated.
     */
    User getCurrentUser();

    /**
     * Signs out the currently authenticated user.
     *
     * @return A Task containing a Response object indicating the result of the sign-out operation.
     */
    Task<Response<Void>> logOut();

    /**
     * Retrieves the ID token of the currently authenticated user.
     * Useful for authenticating with backend API
     *
     * @return A Task containing the ID token as a String.
     */
    Task<String> getIdToken();
}
