package persistentdata.serialization;

import java.util.Map;
import java.util.UUID;

public class ReactionSerializer implements Serializer<Map.Entry<UUID, int[]>, String[]> {
    @Override
    public String[] serialize(Map.Entry<UUID, int[]> entry) {
        int[] r = entry.getValue();
        return new String[]{
                entry.getKey().toString(),
                String.valueOf(r[0]), String.valueOf(r[1]), String.valueOf(r[2]),
                String.valueOf(r[3]), String.valueOf(r[4])
        };
    }

    @Override
    public Map.Entry<UUID, int[]> deserialize(String[] row) {
        UUID uuid = UUID.fromString(row[0]);
        int[] counts = new int[]{
                Integer.parseInt(row[1]), Integer.parseInt(row[2]), Integer.parseInt(row[3]),
                Integer.parseInt(row[4]), Integer.parseInt(row[5])
        };
        return Map.entry(uuid, counts);
    }
}