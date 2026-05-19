package persistentdata.serialization;

import dao.model.User;

import java.util.UUID;

/**
 * TODO: Document your schema here
 */
public class UserSerializer implements Serializer<User, String[]> {
	@Override
	public String[] serialize(User object) {
		String[] fieldsArray = new String[4];
        fieldsArray[0] = object.getUUID().toString();
        fieldsArray[1] = object.role().toString();
        fieldsArray[2] = object.username();
        fieldsArray[3] = object.password();
		return fieldsArray;
	}

    private User.Role roleFromString(String str){
        for(User.Role role : User.Role.values()){
            if (role.toString().equals(str)) return role;
        }
        return null;
    }
	@Override
	public User deserialize(String[] data) {
        return new User(UUID.fromString(data[0]), roleFromString(data[1]), data[2], data[3]);
	}
}
