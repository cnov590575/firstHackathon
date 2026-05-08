package moderation;
import dao.MostReportedComparator;
import dao.PostDAO;
import dao.UserDAO;
import dao.MessageComparator;
import dao.model.Message;
import sorteddata.sortedarraylist.SortedArrayList;

import java.io.IOException;
import java.util.ArrayList;
import dao.model.User;
import java.util.Iterator;
import java.util.UUID;
import java.util.*;

import static moderation.IteratorFactory.messageIterator;
import static moderation.RetrieveReportedList.getReportedList;


public class ModerationTools {
	public static boolean addReport(UUID message, UUID user, long timestamp) {
		Report report = new Report(message, user, timestamp);
        if (AllReports.allReports == null) {
            AllReports.allReports = new ArrayList<Report>();
        }
        ArrayList<Report> currentreports = AllReports.allReports;

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
	
	public static Iterator<Message> getReportedMessages(String strategy, int amount) throws IOException {
        IOException IOException = new IOException();
        if (amount < 0) throw IOException;
        List<Report> reportList = AllReports.allReports;
        List<Message> messageList = new ArrayList<>();
        Iterator<Message> allMessages = PostDAO.getInstance().getAllMessages();

        getReportedList(reportList, messageList, allMessages);
        switch (strategy) {
            case "OLDEST" -> {
                SortedArrayList<Message> sorted = new SortedArrayList<>(MessageComparator.getInstance());
                return messageIterator(sorted, messageList);
            }
            case "MOST" -> {
                SortedArrayList<Message> sorted = new SortedArrayList<>(MostReportedComparator.getInstance());
                return messageIterator(sorted, messageList);
            }
            default -> throw IOException;
        }
    }
}
