package persistentdata;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dao.AllReactions;
import dao.PostDAO;
import dao.UserDAO;
import dao.model.Message;
import dao.model.Post;
import dao.model.User;
import persistentdata.formatted.CSVFormat;
import persistentdata.formatted.CSVFormattedFactory;
import persistentdata.io.ComputerIOFactory;
import persistentdata.io.IOFactory;
import persistentdata.serialization.MessageSerializer;
import persistentdata.serialization.PostSerializer;
import persistentdata.serialization.ReactionSerializer;
import persistentdata.serialization.UserReactionSerializer;
import persistentdata.serialization.UserSerializer;

public class DataManager {
	private static DataManager instance;

	public static void init(IOFactory ioFactory) {
		if (instance == null)
			instance = new DataManager(ioFactory);
	}

	public static DataManager getInstance() {
		if (instance == null)
			throw new IllegalStateException("DataManager not initialized — call init() first");
		return instance;
	}

	private final DataPipeline<User, String[]> userPipeline;
	private final DataPipeline<Post, String[]> postPipeline;
	private final DataPipeline<Message, String[]> messagePipeline;

	private final DataPipeline<Map.Entry<UUID, int[]>, String[]> reactionPipeline;
	private final DataPipeline<Map.Entry<UUID, Map.Entry<UUID, Boolean[]>>, String[]> userReactionPipeline;

	private final UserDAO users = UserDAO.getInstance();
	private final PostDAO posts = PostDAO.getInstance();

	private DataManager(IOFactory io) {
		userPipeline    = new DataPipeline<>(io, new CSVFormattedFactory(new CSVFormat(4)), new UserSerializer(),    "users");
		postPipeline    = new DataPipeline<>(io, new CSVFormattedFactory(new CSVFormat(3)), new PostSerializer(),    "posts");
		messagePipeline = new DataPipeline<>(io, new CSVFormattedFactory(new CSVFormat(5)), new MessageSerializer(), "messages");
		reactionPipeline = new DataPipeline<>(io, new CSVFormattedFactory(new CSVFormat(6)), new ReactionSerializer(), "reactions");
		userReactionPipeline =  new DataPipeline<>(io, new CSVFormattedFactory(new CSVFormat(7)), new UserReactionSerializer(), "userReactions");
	}



	public void readAll() {
		users.clear();
		posts.clear();
		AllReactions.getAllReactions().clear();
		AllReactions.getAllUserReactions().clear();

		userPipeline.readTo(users::add);
		postPipeline.readTo(posts::add);
			messagePipeline.readTo((message) -> {
			Post parent = posts.get(new Post(message.thread()));
			Log.d("Persistence", "message thread: " + message.thread() + " parent found: " + (parent != null));
			if (parent == null) return;
			parent.messages.insert(message);
		});
		Log.d("Persistence", "messages loaded");

		reactionPipeline.readTo(e ->
				AllReactions.reactions.put(e.getKey(), e.getValue()));


		userReactionPipeline.readTo(e -> {
			Log.d("Persistence", "loading userReaction: user=" + e.getKey() +
					" target=" + e.getValue().getKey() +
					" bools=" + java.util.Arrays.toString(e.getValue().getValue()));
			AllReactions.userReactions
					.computeIfAbsent(e.getKey(), k -> new HashMap<>())
					.put(e.getValue().getKey(), e.getValue().getValue());
		});

	}

	public void writeAll() {
		Log.d("Persistence", "writeAll called from: " + Arrays.toString(Thread.currentThread().getStackTrace()));

		Log.d("Persistence", "writeAll started");
		userPipeline.writeFrom(users.getAll());
		Log.d("Persistence", "users written");
		postPipeline.writeFrom(posts.getAll());
		Log.d("Persistence", "posts written");
		List<Message> allMessages = new ArrayList<>();
		Iterator<Post> postIterator = posts.getAll();
		while (postIterator.hasNext()) {
			Post post = postIterator.next();
			Iterator<Message> msgIterator = post.messages.getAll();
			while (msgIterator.hasNext()) {
				Message m = msgIterator.next();
				if (m != null) allMessages.add(m);
			}
		}
		Log.d("Persistence", "total messages to write: " + allMessages.size());
		messagePipeline.writeFrom(allMessages.iterator());
		Log.d("Persistence", "messages written");


		Log.d("Persistence", "reactions map size: " + AllReactions.getAllReactions().size());
		Log.d("Persistence", "userReactions map size: " + AllReactions.getAllUserReactions().size());
		reactionPipeline.writeFrom(AllReactions.getAllReactions().entrySet().iterator());
		Log.d("Persistence", "reactions written");



		List<Map.Entry<UUID, Map.Entry<UUID, Boolean[]>>> flatUserReactions = new ArrayList<>();
		for (Map.Entry<UUID, HashMap<UUID, Boolean[]>> outer : AllReactions.userReactions.entrySet()) {
			for (Map.Entry<UUID, Boolean[]> inner : outer.getValue().entrySet()) {
				flatUserReactions.add(Map.entry(outer.getKey(), Map.entry(inner.getKey(), inner.getValue())));
			}
		}
		userReactionPipeline.writeFrom(flatUserReactions.iterator());
		Log.d("Persistence", "writeAll complete");

	}

}
