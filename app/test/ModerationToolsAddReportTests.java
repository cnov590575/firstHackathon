import dao.PostDAO;
import dao.model.MessageVisibility;
import dao.model.Post;
import dao.model.User;
import moderation.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import dao.model.Message;
import java.util.Iterator;
import java.util.UUID;
import dao.UserDAO;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class ModerationToolsAddReportTests {
    @Test(timeout=1000)
    public void testAddReport() {
        UUID messageUUID = UUID.randomUUID();
        UUID userUUID = UUID.randomUUID();
        assertNull(AllReports.allReports);
        ModerationTools.addReport(messageUUID, userUUID, 1);
        assertNotNull(AllReports.allReports);

    }
    @Test(timeout=1000)
    public void testAddDuplicateReport() {
        UUID messageUUID = UUID.randomUUID();
        UUID userUUID = UUID.randomUUID();
        System.out.println(ModerationTools.getReportedMessages("MOST", 1));
        ModerationTools.addReport(messageUUID, userUUID, 1);
        assertFalse(ModerationTools.addReport(messageUUID, userUUID, 1));

    }
}
