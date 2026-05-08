package moderation;

import dao.model.Message;
import sorteddata.sortedarraylist.SortedArrayList;

import java.util.Iterator;
import java.util.List;

public class IteratorFactory {
    public static Iterator<Message> messageIterator(SortedArrayList<Message> result, List<Message> messageList) {
        for (Message message : messageList) {
            result.insert(message);
        }
        return result.getAll();
    }
}
