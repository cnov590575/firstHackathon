package persistentdata.serialization;

import java.util.Map;
import java.util.UUID;

public class UserReactionSerializer implements Serializer<Map.Entry<UUID, Map.Entry<UUID, Boolean[]>>, String[]> {
    @Override
    public String[] serialize(Map.Entry<UUID, Map.Entry<UUID, Boolean[]>> entry) {
        Boolean[] b = entry.getValue().getValue();
        return new String[]{
                entry.getKey().toString(),
                entry.getValue().getKey().toString(),
                String.valueOf(b[0]), String.valueOf(b[1]), String.valueOf(b[2]),
                String.valueOf(b[3]), String.valueOf(b[4])
        };
    }

    @Override
    public Map.Entry<UUID, Map.Entry<UUID, Boolean[]>> deserialize(String[] row) {
        UUID userUUID   = UUID.fromString(row[0]);
        UUID targetUUID = UUID.fromString(row[1]);
        Boolean[] bools = new Boolean[]{
                Boolean.parseBoolean(row[2]), Boolean.parseBoolean(row[3]),
                Boolean.parseBoolean(row[4]), Boolean.parseBoolean(row[5]),
                Boolean.parseBoolean(row[6])
        };
        return Map.entry(userUUID, Map.entry(targetUUID, bools));
    }
}