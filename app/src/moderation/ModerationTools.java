package moderation;

import dao.PostDAO;
import dao.UserDAO;
import dao.model.Message;
import sorteddata.sortedarraylist.SortedArrayList;

import java.util.ArrayList;
import java.util.HashMap;
import dao.model.User;

import java.util.Iterator;
import java.util.UUID;



public class ModerationTools {
	public static boolean addReport(UUID message, UUID user, long timestamp) {
		Report report = new Report(message, user, timestamp);
		ReportList list = new ReportList(new ArrayList<Report>(),new HashMap<>());
		list.addReport(report);
	}
	
	public static boolean removeReport(UUID message, UUID user, long timestamp) {
		// TODO: task 1
		return false;
	}
	
	public static boolean hasReported(UUID message, UUID user) {
		// TODO: task 1
		return false;
	}
	
	public static boolean setHidden(UUID message, UUID user, boolean hidden) {
        User realUser = UserDAO.getInstance().getByUUID(user);
        Iterator<Message> allMessages = PostDAO.getInstance().getAllMessages();
        Message realMessage = null;

        if (realUser==null) return false;
        if (!realUser.role().equals(User.Role.Admin)) return false;
        while (allMessages.hasNext()) {
            Message curMessage = allMessages.next();
            if (curMessage.id() == message) {
                realMessage = curMessage;
                break;
            }
        }
        if (realMessage == null) return false;

        realMessage.visible().setVisible(false);

        return true;
	}
	
	public static Iterator<Message> getReportedMessages(String strategy, int amount) {
		// TODO: task 4
		return null;
	}
}
