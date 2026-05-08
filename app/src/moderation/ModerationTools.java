package moderation;

import dao.MessageComparator;
import dao.PostDAO;
import dao.model.Message;
import sorteddata.sortedarraylist.SortedArrayList;

import java.util.*;

import static com.sun.org.apache.xml.internal.serializer.utils.Utils.messages;

public class ModerationTools {
	public static boolean addReport(UUID message, UUID user, long timestamp) {
		// TODO: task 1
		return false;
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
		// TODO: task 2
		return false;
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
