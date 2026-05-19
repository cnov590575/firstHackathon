package com.example.comp2100miniproject.persistentdata.serialization;

import com.example.comp2100miniproject.dao.model.User;

import java.util.UUID;

/**
 * TODO: Document your schema here
 */
public class UserSerializer implements Serializer<User, String[]> {
	@Override
	public String[] serialize(User object) {
		// TODO: Complete this method according to the schema you have designed
        return new String[] {object.id().toString(), object.role().toString(), object.username(), object.password()};
	}

	@Override
	public User deserialize(String[] data) {
		// TODO: Complete this method according to the schema you have designed
        User.Role role = null;
        if (data[1].equals("Member")) role = User.Role.Member;
        if (data[1].equals("Admin")) role = User.Role.Admin;
		return new User(UUID.fromString(data[0]), role, data[2], data[3]);
	}
}
