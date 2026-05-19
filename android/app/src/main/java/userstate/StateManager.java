package userstate;

import dao.UserDAO;
import dao.model.Post;
import dao.model.User;

import java.util.Objects;

public class StateManager {
	private static UserState state = new GuestState();

	public static UserState getState() {
		return state;
	}

	public static boolean login(String username, String password) {
        // TODO: Complete this method in accordance with the State design pattern
        // For this task, you may modify the other classes and interfaces within
        //  the userstate package by adding methods, including public ones
        User checkUser = new User(username);
        User realuser = UserDAO.getInstance().get(checkUser);
        if (Objects.requireNonNull(getState()) instanceof GuestState) {
            if (realuser == null) return false;
            else if (password.equals(realuser.password())) {
                switch (realuser.role()) {
                    case Member -> {
                        state = new MemberState(realuser);
                    }
                    case Admin -> {
                        state = new AdminState(realuser);
                    }
                }
                return true;
            } else {
                return false;
            }
        }
        else return false;
    }

    public static boolean register(String username, String password) {
        UserState newState = state.register(username, password);
        boolean success = newState != state;
        state = newState;
        return success;
    }

	public static boolean logout() {
		UserState newState = state.logout();
		boolean success = newState != state;
		state = newState;
		return success;
	}

	public static boolean post(Post post, String content) {
		return state.addReply(post, content);
	}

	public static boolean isLoggedIn() {
		return state.isLoggedIn();
	}
}
