import dao.PostDAO;
import dao.model.MessageVisibility;
import dao.model.Post;
import dao.model.User;
import moderation.ModerationTools;
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
public class ModerationToolsGetReportsTests {
    @Before
    public void reinitialise() {
        PostDAO.getInstance().clear();
    }

    @Test(timeout=1000)
    public void testSingle() {
        Post post = new Post(UUID.randomUUID());
        PostDAO.getInstance().add(post);

    }
    public void createReports() {
        UserDAO newInstance = UserDAO.getInstance();
        User carlUser = newInstance.register("Carl", "password123");
        UUID threadUUID = UUID.randomUUID();
        UUID oldestMessageUUID = UUID.randomUUID();
        UUID youngestMessageUUID = UUID.randomUUID();
        UUID offensiveMessageUUID = UUID.randomUUID();
        UUID kindMessageUUID = UUID.randomUUID();
        Message oldestMessage = new Message(oldestMessageUUID, carlUser.getUUID(), threadUUID, 100, "I am the oldest message", new MessageVisibility(true));
        ModerationTools.addReport(oldestMessageUUID, carlUser.getUUID(), 200);
        Message youngestMessage = new Message(youngestMessageUUID, carlUser.getUUID(), threadUUID, 500, "I am the youngest message", new MessageVisibility(true));
        ModerationTools.addReport(youngestMessageUUID, carlUser.getUUID(), 150);
        Message offensiveMessage = new Message(offensiveMessageUUID, carlUser.getUUID(), threadUUID, 200, "Cats are better than dogs", new MessageVisibility(true));
        ModerationTools.addReport(offensiveMessageUUID, carlUser.getUUID(), 150);
        ModerationTools.addReport(offensiveMessageUUID, carlUser.getUUID(), 200);
        ModerationTools.addReport(offensiveMessageUUID, carlUser.getUUID(), 200);
        Message kindMessage = new Message(kindMessageUUID, carlUser.getUUID(), threadUUID, 300, "Dogs are better than cats", new MessageVisibility(true));
    }

    // OLDEST and MOST are valid strategies, anything else should return exception
    @Test(expected = NullPointerException.class, timeout=1000)
    public void testInvalidStrategies1() {
        Iterator<Message> test = ModerationTools.getReportedMessages("NOTOLDEST", 1);
    }
    @Test(expected = NullPointerException.class, timeout=1000)
    public void testInvalidStrategies2() {
        Iterator<Message> test = ModerationTools.getReportedMessages("MOSTEST", 1);
    }
    @Test(expected = NullPointerException.class, timeout=1000)
    public void testInvalidStrategies3() {
        Iterator<Message> test = ModerationTools.getReportedMessages("MOST ", 1);
    }
    @Test(expected = NullPointerException.class, timeout=1000)
    public void testInvalidStrategies4() {
        Iterator<Message> test = ModerationTools.getReportedMessages(" OLDEST", 1);
    }
    @Test(expected = NullPointerException.class, timeout=1000)
    public void testInvalidAmounts1() {
        Iterator<Message> test = ModerationTools.getReportedMessages("OLDEST", 0);
    }
    public void testInvalidAmounts2() {
        Iterator<Message> test = ModerationTools.getReportedMessages("MOST", (-1));
    }
    @Test(timeout=100)
    public void returnOldestEntry() {
        assertEquals(ModerationTools.getReportedMessages("OLDEST", 1).next().message(), "I am the oldest message");
    }

    @Test(timeout=100)
    public void returnMostEntry() {
        assertEquals(ModerationTools.getReportedMessages("MOST", 1).next().message(), "Cats are better than dogs");
    }
}
