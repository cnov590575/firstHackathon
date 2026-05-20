import dao.PostDAO;
import dao.RandomContentGenerator;
import dao.UserDAO;
import dao.model.Message;
import dao.model.Post;
import dao.model.User;
import moderation.ModerationTools;
import org.junit.Test;

import java.util.UUID;

public class carlsTests {
    User user = UserDAO.getInstance().register("User", "password");
    User admin = UserDAO.getInstance().register("Admin", "pswd", 1);

    RandomContentGenerator.populateRandomData();
    Message message = new Message(UUID.randomUUID(), user.getUUID(), UUID.randomUUID(), 1500L, "HEllo");



    @Test
    public void CarlTest() {
        System.out.println(ModerationTools.setHidden(message.id(), user.getUUID(), false));
        System.out.println(message1.visible().visible);
        System.out.println(ModerationTools.setHidden(message.id(), admin.getUUID(), true));
        System.out.println(message1.visible().visible);
    }
}
