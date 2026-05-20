package dao;

import dao.model.HasUUID;
import dao.model.Message;
import dao.model.Post;
import dao.model.User;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.UUID;

public class PostDAO extends DAO<Post> {
	/**
	 * Generates a PostDAO by automatically building a Comparator that
	 * checks just that the UUID fields match. If you don't understand
	 * this syntax, don't worry. It's an advanced Java technique.
	 */
	private PostDAO() {
		super(Comparator.comparing(HasUUID::getUUID));
	}
	private static PostDAO instance;

	/**
	 * Gets a singleton instance of PostDAO, creating one if necessary.
	 * @return the instance
	 */
	public static PostDAO getInstance() {
		if (instance == null) instance = new PostDAO();
		return instance;
	}

	/**
	 * Gets the ith post, in order of timestamp
	 * @param i the index of the post to search for
	 * @return the post
	 */
	public Post getAtIndex(int i) {
		return data.getAtIndex(i);
	}

	/**
	 * Returns an Iterator that iterates through every message given as a reply to
	 * every post stored within the DAO, in no particular order.
	 * @return the iterator
	 */
	public Iterator<Message> getAllMessages() {
		Iterator<Message> result = new Iterator<Message>() {
            int postIndex = -1;
            int messageIndex = 0;
            public int newReplyorPost(int postindex, int messageindex) {
                try {
                    getAtIndex(postindex).messages.getAtIndex(messageindex+1);
                    return 0;
                } catch (Exception e) {
                    try {
                        getAtIndex(postindex+1).messages.getAtIndex(0);
                        return 1;
                    } catch (Exception e1) {
                        try {
                            getAtIndex(postindex+1);
                            return 1+newReplyorPost(postindex+1, 0);
                        } catch (Exception e2) {
                            return -1;
                        }
                    }
                }
            }

            @Override
            public boolean hasNext() {
                switch (newReplyorPost(postIndex, messageIndex)){
                    case -1 -> {
                        return false;
                    }
                    default -> {
                        return true;
                    }
                }
            }

            @Override
            public Message next() {
                if (hasNext()) {
                    switch (newReplyorPost(postIndex, messageIndex)) {
                        case 0 -> {
                            messageIndex = messageIndex + 1;
                            return getAtIndex(postIndex).messages.getAtIndex(messageIndex);
                        }
                        case -1 -> {
                            System.out.println("ASADASDAS");
                            return null;
                        }
                        default -> {
                            postIndex = postIndex + newReplyorPost(postIndex, messageIndex);
                            messageIndex = 0;
                            return getAtIndex(postIndex).messages.getAtIndex(messageIndex);
                        }
                    }
                }
                throw new NoSuchElementException();
            }
	    };
    return result;
    }

    public Post getByUUID(UUID id) {
        for (Iterator<Post> it = data.getAll(); it.hasNext(); ) {
            Post user = it.next();
            if (user.getUUID().equals(id)) return user;
        }
        return null;
    }
}
