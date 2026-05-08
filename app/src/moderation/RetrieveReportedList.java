package moderation;

import dao.PostDAO;
import dao.model.Message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RetrieveReportedList {
    public static void getReportedList(List<Report> reportList, List<Message> messageList, Iterator<Message> allMessages) {
        for (Report report : reportList) {
            Message message = null;
            while (allMessages.hasNext()) {
                Message curMessage = allMessages.next();
                if (curMessage.id() == report.message()) {
                    message = curMessage;
                    break;
                }
            }
            if (message != null) messageList.add(message);
        }
    }
}
