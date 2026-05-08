package persistentdata.serialization;

import dao.model.Message;
import dao.model.MessageVisibility;

import java.beans.Visibility;
import java.util.Objects;
import java.util.UUID;

/**
 * Converts between Messages and String[] by converting each field of Post
 * (UUID, poster, thread, timestamp, and message) to a string, which becomes one of the entries
 * within the array
 */
public class MessageSerializer implements Serializer<Message, String[]> {

	String isVisible(Message message){
		if (message.visible().visible){
			return "Visible";
		}
		else {
			return "Hidden";
		}
	}
	Boolean isVisible(String visibility){
		if (Objects.equals(visibility, "Hidden")){
			return  false;
		} else {
			return true;
		}
	}

	@Override
	public String[] serialize(Message object) {
		return new String[] {object.id().toString(), object.poster().toString(), object.thread().toString(), String.valueOf(object.timestamp()), object.message(), isVisible(object)};
	}

	@Override
	public Message deserialize(String[] data) {
		return new Message(UUID.fromString(data[0]), UUID.fromString(data[1]), UUID.fromString(data[2]), Long.valueOf(data[3]), data[4], new MessageVisibility(isVisible(data[5])));
	}
}
