package moderation;

import dao.model.HasUUID;

import java.util.UUID;

public record Report(UUID message, UUID user, long timestamp) implements HasUUID {

    public UUID getUUID() {
        return message;
    }
}