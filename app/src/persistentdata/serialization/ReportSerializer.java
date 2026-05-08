package persistentdata.serialization;

import dao.model.Post;
import dao.model.User;
import moderation.Report;

import java.util.UUID;

public class ReportSerializer implements Serializer<Report, String[]>{

    @Override
    public String[] serialize(Report object) {
        return new String[] {object.message().toString(), object.user().toString(), Long.toString(object.timestamp())};
    }

    @Override
    public Report deserialize(String[] data) {
        return new Report(UUID.fromString(data[0]), UUID.fromString(data[1]), new Long(data[2]));
    }
}
