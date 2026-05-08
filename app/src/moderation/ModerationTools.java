package moderation;
import dao.PostDAO;
import dao.UserDAO;
import dao.MessageComparator;
import dao.model.Message;
import sorteddata.sortedarraylist.SortedArrayList;
import java.util.ArrayList;
import dao.model.User;
import java.util.Iterator;
import java.util.UUID;
import java.util.*;


public class ModerationTools {
	public static boolean addReport(UUID message, UUID user, long timestamp) {
		Report report = new Report(message, user, timestamp);
        ArrayList<Report> currentreports = AllReports.allReports;
        if (AllReports.allReports == null) {
            AllReports.allReports = new ArrayList<Report>();
        }

        for (Report curReport : currentreports) {
            if (report.equals(curReport)) {
                return false;
            }
        }
		AllReports.allReports.add(report);
        return true;
	}
	
	public static boolean removeReport(UUID message, UUID user, long timestamp) {
        if (AllReports.allReports == null) {
            AllReports.allReports = new ArrayList<Report>();
        }
        Report report = new Report(message, user, timestamp);
        for (Report curReport : AllReports.allReports) {
            if (report.equals(curReport)) {
                AllReports.allReports.remove(report);
                return true;
            }
        }
		return false;
	}
	
	public static boolean hasReported(UUID message, UUID user) {
        Iterator<Message> allMessages = PostDAO.getInstance().getAllMessages();

        for (Report curReport : AllReports.allReports) {
            if (curReport.message().equals(message)&&curReport.user().equals(user)) {
                return true;
            }
        }
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

        realMessage.visible().setVisible(!hidden);

        return true;
	}
	
	public static Iterator<Message> getReportedMessages(String strategy, int amount) {
        List<Report> reportList = AllReport.getAll();
        List<Message> messageList = new ArrayList<>();
        for (Report report : reportList) {
            messageList.add(report.message);
        }
        Map<Report, Integer> reportCount = reportList.getAll();
        switch (strategy) {
            case "OLDEST" -> {
                SortedArrayList<Message> sorted = new SortedArrayList<>(MessageComparator.getInstance());
                for (Message message : messageList) {
                    sorted.insert(message);
                }
                return sorted.getAll();
            }
            case "MOST" -> {
                SortedArrayList<Message> sorted = new SortedArrayList<>(MessageComparator.getInstance());
            };
            default -> ;
        }
    }
}
