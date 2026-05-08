package dao.model;

import dao.MessageComparator;
import dao.PostDAO;
import sorteddata.SortedData;
import sorteddata.SortedDataFactory;
import sorteddata.sortedarraylist.SortedArrayList;

import java.util.Comparator;
import java.util.Iterator;
import java.util.UUID;

public class Post implements HasUUID {
	public final UUID id;
	public final UUID poster;
	public final String topic;
    public final SortedData<Message> messages;

	public Post(UUID id, UUID poster, String topic) {
		this.id = id;
		this.poster = poster;
		this.topic = topic;
		this.messages = SortedDataFactory.makeSortedData(MessageComparator.getInstance());
	}

	public Post(UUID id) {
		this(id, null, null);
	}


    /**
     * Returns a sorted arraylist of all messages that should be visible based on timestamp.
     * If isAdmin is true,
     *
     * @param isAdmin
     * @return
     */
	public SortedData<Message> getVisibleMessages(boolean isAdmin) {
        Iterator<Message> allMessages = PostDAO.getInstance().getAllMessages();
        SortedData<Message> canBeSeen = new SortedArrayList<>(new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                if (o1.timestamp() < o2.timestamp()) return -1;
                if (o1.timestamp() > o2.timestamp()) return 1;
                else return 0;
            }
        });
        while (allMessages.hasNext()){
            Message nextMessage = allMessages.next();
            if (isAdmin) {
                canBeSeen.insert(nextMessage);
            }
            else {
                if (nextMessage.visible().visible) {
                    canBeSeen.insert(nextMessage);
                }
            }
        }

		return canBeSeen;
	}

	public UUID getUUID() { return id; }
}
