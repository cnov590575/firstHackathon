package com.example.comp2100miniproject.persistentdata;

import com.example.comp2100miniproject.dao.PostDAO;
import com.example.comp2100miniproject.persistentdata.formatted.FormattedFactory;
import com.example.comp2100miniproject.persistentdata.serialization.Serializer;
import com.example.comp2100miniproject.persistentdata.io.IOFactory;

import java.io.*;
import java.util.Iterator;

public class DataPipeline<T, S> {
	private final IOFactory ioFactory;
	private final com.example.comp2100miniproject.persistentdata.formatted.FormattedFactory<S> formattedFactory;
	private final com.example.comp2100miniproject.persistentdata.serialization.Serializer<T, S> serializer;
	private final String filename;

	public DataPipeline(IOFactory ioFactory, FormattedFactory<S> formattedFactory, Serializer<T, S> serializer, String filename) {
		this.ioFactory = ioFactory;
		this.formattedFactory = formattedFactory;
		this.serializer = serializer;
		this.filename = filename;
	}


	private static final com.example.comp2100miniproject.dao.UserDAO users = com.example.comp2100miniproject.dao.UserDAO.getInstance();
	private static final com.example.comp2100miniproject.dao.PostDAO posts = PostDAO.getInstance();

	public void writeFrom(Iterator<T> iterator) {
		try {
			Writer writer = ioFactory.writer(filename);
			if (writer == null) return;
			com.example.comp2100miniproject.persistentdata.formatted.FormattedWriter<S> formattedWriter = formattedFactory.writer(writer);

			while (iterator.hasNext()) {
				formattedWriter.putNext(serializer.serialize(iterator.next()));
			}

			writer.close();
		} catch (IOException e) {
			throw new PersistentDataException(e.getMessage());
		}
	}

	public interface AddToDAO<T> {
		void run(T item);
	}

	public void readTo(AddToDAO<T> callback) {
		try {
			Reader reader = ioFactory.reader(filename);
			if (reader == null) return;
			com.example.comp2100miniproject.persistentdata.formatted.FormattedReader<S> formattedReader = formattedFactory.reader(reader);

			while (formattedReader.hasNext()) {
				callback.run(serializer.deserialize(formattedReader.getNext()));
			}

			reader.close();
		} catch (IOException e) {
			throw new PersistentDataException(e.getMessage());
		}
	}
}
