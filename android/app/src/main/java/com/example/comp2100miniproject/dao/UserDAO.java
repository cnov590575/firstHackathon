package com.example.comp2100miniproject.dao;

import com.example.comp2100miniproject.dao.model.User;

import java.util.Iterator;
import java.util.UUID;

public class UserDAO extends DAO<User> {
	// TODO: apply the Singleton design pattern to this class.
	// You may modify the existing constructor, add new constructors,
	// and add new helper method and private fields.
	/**
	 * Generates a UserDAO. We enforce uniqueness in usernames (but not in passwords),
	 * and further two usernames are considered identical if they are equal, ignoring case
	 */
    private static UserDAO instance = null;

	private UserDAO() {
		super((o1, o2) -> o1.username().compareToIgnoreCase(o2.username()));
	}
	public static UserDAO getInstance() {
            if (instance == null) {
            System.out.println("Instance Created!!!");
            instance = new UserDAO();
            } else {
                System.out.println("Instance has already been created!!!");
            }
        return instance;
	}

	/**
	 * Attempts to authenticate as a particular user. If the user exists
	 * and their passwords match, the login is considered successful.
	 * @param username the username
	 * @param password the password
	 * @return the User if successful, null otherwise
	 */
	public User login(String username, String password) {
		// TODO: Complete this method, interfacing with the DAO pattern, to the specification in the javadoc
		User user = new User(username);
        User actualUser = get(user);
        if (actualUser == null) return null;
        if (actualUser.password().equals(password)) {
            return actualUser;
        }
        return null;
	}

	/**
	 * Attempts to register a new user. Users must have unique usernames,
	 * and their usernames must contain only alphanumeric characters.
	 * Usernames can be between 4 and 20 characters long.
	 * Passwords must be at least four characters long, and can include
	 * any codepoints.
	 * @param username the desired username
	 * @param password the desired password
	 * @return the newly-created User if successful, null otherwise
	 */
	public User register(String username, String password) {
        if (username.length() < 4) return null;
        if (username.length() > 20) return null;
        if (password.length() < 4) return null;

        for (int i = 0; i < username.length(); i++) {
            if (username.charAt(i) > 'z') return null;
            if (username.charAt(i) < '0') return null;
        }

        User user = new User(username);
        User checkUser = get(user);
        if (checkUser == null) {
            User newUser = new User(UUID.randomUUID(), User.Role.Member, username, password);
            super.add(newUser);
            return newUser;
        }
        else return null;
	}

	/**
	 * Fetches a User by just a UUID
	 * @param id the UUID to search for
	 * @return the user if they exist, else null
	 */
	public User getByUUID(UUID id) {
        for (Iterator<User> it = data.getAll(); it.hasNext(); ) {
            User user = it.next();
            if (user.getUUID().equals(id)) return user;
        }
		return null;
	}
}
