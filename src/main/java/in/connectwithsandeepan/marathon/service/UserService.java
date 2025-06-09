package in.connectwithsandeepan.marathon.service;

import in.connectwithsandeepan.marathon.dto.UserRequestDto;
import in.connectwithsandeepan.marathon.dto.UserResponseDto;

import java.util.List;
import java.util.Set;

public interface UserService {
    /**
     * Saves a user to the database.
     *
     * @param user the user to save
     * @return the saved user
     */
    UserResponseDto saveUser(UserRequestDto user);

    /**
     * Finds a user by their username.
     *
     * @param username the username of the user
     * @return an Optional containing the user if found, or empty if not found
     */
    UserResponseDto findByUsername(String username);

    /**
     * Finds a user by their email.
     *
     * @param email the email of the user
     * @return an Optional containing the user if found, or empty if not found
     */
    List<UserResponseDto> findAllByEmail(String email);


    /**
     * Finds a user by their id.
     *
     * @param id the email of the user
     * @return the found user, or null if no user was found
     */
    UserResponseDto findById(Long id);

    /**
     * Retrieves all users.
     *
     * @return a list of all users
     */
    List<UserResponseDto> findAllUsers();


    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     */
    void deleteUserById(Long id);

    /**
     * Updates the enabled status of a user by their ID.
     *
     * @param id      the ID of the user
     * @param enabled the new enabled status to set
     */
    void updateUserEnabledStatus(Long id, boolean enabled);

    /**
     * Updates the roles of a user by their ID.
     *
     * @param id    the ID of the user
     * @param roles the new list of roles to set for the user
     */
    void updateUserRoles(Long id, Set<String> roles);

}
