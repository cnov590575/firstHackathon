package com.example.comp2100miniproject.persistentdata;

import com.example.comp2100miniproject.dao.PostDAO;
import com.example.comp2100miniproject.dao.UserDAO;
import com.example.comp2100miniproject.dao.model.Message;
import com.example.comp2100miniproject.dao.model.Post;
import com.example.comp2100miniproject.dao.model.User;
import com.example.comp2100miniproject.persistentdata.formatted.CSVFormat;
import com.example.comp2100miniproject.persistentdata.formatted.CSVFormattedFactory;
import com.example.comp2100miniproject.persistentdata.io.ComputerIOFactory;
import com.example.comp2100miniproject.persistentdata.io.IOFactory;
import com.example.comp2100miniproject.persistentdata.serialization.MessageSerializer;
import com.example.comp2100miniproject.persistentdata.serialization.PostSerializer;
import com.example.comp2100miniproject.persistentdata.serialization.UserSerializer;

public class DataManager {
	private static DataManager instance;
	public static DataManager getInstance() {
		if (instance == null)
			instance = new DataManager();
		return instance;
	}

	private final IOFactory IO = new ComputerIOFactory();

	// We have assumed that most solutions to the serialization task in week-5 will
	// use a 4-column schema for Users. If this is not the case, you may need to
	// change the number below.
	private final DataPipeline<User, String[]> userPipeline = new DataPipeline<>(
			IO, new CSVFormattedFactory(new CSVFormat(4)), new UserSerializer(), "users");

	private final DataPipeline<Post, String[]> postPipeline = new DataPipeline<>(
			IO, new CSVFormattedFactory(new CSVFormat(3)), new PostSerializer(), "posts");

	private final DataPipeline<Message, String[]> messagePipeline = new DataPipeline<>(
			IO, new CSVFormattedFactory(new CSVFormat(5)), new MessageSerializer(), "messages");

	private final UserDAO users = UserDAO.getInstance();
	private final PostDAO posts = PostDAO.getInstance();

	public void readAll() {
		users.clear();
		posts.clear();
		userPipeline.readTo(users::add);
		postPipeline.readTo(posts::add);
		messagePipeline.readTo((message) -> posts.get(new Post(message.thread())).messages.insert(message));
	}

	public void writeAll() {
		userPipeline.writeFrom(users.getAll());
		postPipeline.writeFrom(posts.getAll());
		messagePipeline.writeFrom(posts.getAllMessages());
	}
}
