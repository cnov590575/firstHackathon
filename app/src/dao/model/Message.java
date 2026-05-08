package dao.model;

import java.util.UUID;

public record Message(UUID id, UUID poster, UUID thread, long timestamp, String message, MessageVisibility visible) {
    public Message(UUID id, UUID poster, UUID thread, long timestamp, String message) {
        this(id, poster, thread, timestamp, message, new MessageVisibility(true));
    }
}