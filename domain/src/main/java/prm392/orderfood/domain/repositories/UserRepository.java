package prm392.orderfood.domain.repositories;

import io.reactivex.Single;
import prm392.orderfood.domain.models.users.UserProfile;
import retrofit2.Response;

public interface UserRepository {
    /**
     * Retrieves the user profile for a given user ID.
     *
     * @param userId The ID of the user whose profile is to be retrieved.
     * @return A Single that emits a Response containing the UserProfile object if successful,
     *         or an error if the retrieval fails.
     */
    Single<Response<UserProfile>> getUserProfile();
}
