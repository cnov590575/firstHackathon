package com.example.comp2100miniproject.dao;

import com.example.comp2100miniproject.dao.model.HasUUID;
import com.example.comp2100miniproject.dao.model.Message;
import com.example.comp2100miniproject.dao.model.Post;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

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
		// TODO: Complete this method using the Iterator design pattern
        ArrayList<Message> allMessages = new ArrayList<>();
        ArrayList<Post> allPosts = new ArrayList<>();
        Iterator<Post> posts = getAll();
        while (posts.hasNext()) {
            Post current = posts.next();
            Iterator<Message> messages = current.messages.getAll();
            while (messages.hasNext()) {
                allMessages.add(messages.next());
            }
        }
        return allMessages.iterator();
	}
}
