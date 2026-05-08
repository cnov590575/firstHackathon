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

import java.io.IOException;
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

    @Before
    public void createReports() {
        UUID carlUser = UUID.randomUUID();
        UUID threadUUID = UUID.randomUUID();
        UUID oldestMessageUUID = UUID.randomUUID();
        UUID youngestMessageUUID = UUID.randomUUID();
        UUID offensiveMessageUUID = UUID.randomUUID();
        UUID kindMessageUUID = UUID.randomUUID();
        PostDAO newPostDAO = PostDAO.getInstance();
        newPostDAO.add(new Post(threadUUID,carlUser,"Hello"));
        Message oldestMessage = new Message(oldestMessageUUID, carlUser, threadUUID, 100, "I am the oldest message", new MessageVisibility(true));
        ModerationTools.addReport(oldestMessageUUID, carlUser, 200);
        Message youngestMessage = new Message(youngestMessageUUID, carlUser, threadUUID, 500, "I am the youngest message", new MessageVisibility(true));
        ModerationTools.addReport(youngestMessageUUID, carlUser, 150);
        Message offensiveMessage = new Message(offensiveMessageUUID, carlUser, threadUUID, 200, "Cats are better than dogs", new MessageVisibility(true));
        ModerationTools.addReport(offensiveMessageUUID, carlUser, 150);
        ModerationTools.addReport(offensiveMessageUUID, carlUser, 200);
        ModerationTools.addReport(offensiveMessageUUID, carlUser, 200);
        Message kindMessage = new Message(kindMessageUUID, carlUser, threadUUID, 300, "Dogs are better than cats", new MessageVisibility(true));
    }

    // OLDEST and MOST are valid strategies, anything else should return exception
    @Test(expected = IOException.class, timeout=1000)
    public void testInvalidStrategies1() throws IOException {
        Iterator<Message> test = ModerationTools.getReportedMessages("NOTOLDEST", 1);
    }
    @Test(expected = IOException.class, timeout=1000)
    public void testInvalidStrategies2() throws IOException {
        Iterator<Message> test = ModerationTools.getReportedMessages("MOSTEST", 1);
    }
    @Test(expected = IOException.class, timeout=1000)
    public void testInvalidStrategies3() throws IOException {
        Iterator<Message> test = ModerationTools.getReportedMessages("MOST ", 1);
    }
    @Test(expected = IOException.class, timeout=1000)
    public void testInvalidStrategies4() throws IOException {
        Iterator<Message> test = ModerationTools.getReportedMessages(" OLDEST", 1);
    }
    @Test(expected = IOException.class, timeout=1000)
    public void testInvalidAmounts1() throws IOException {
        Iterator<Message> test = ModerationTools.getReportedMessages("OLDEST", 0);
    }
    @Test(expected = IOException.class, timeout=1000)
    public void testInvalidAmounts2() throws IOException {
        Iterator<Message> test = ModerationTools.getReportedMessages("MOST", (-1));
    }
    @Test(timeout=1000)
    public void returnOldestEntry() {
        try {
            assertEquals(ModerationTools.getReportedMessages("OLDEST", 1).next().message(), "I am the oldest message");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test(timeout=1000)
    public void returnMostEntry() {
        try {
            assertEquals(ModerationTools.getReportedMessages("MOST", 1).next().message(), "Cats are better than dogs");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
