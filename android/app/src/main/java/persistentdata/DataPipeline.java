package persistentdata;

import android.util.Log;

import persistentdata.formatted.*;
import persistentdata.io.IOFactory;
import persistentdata.serialization.*;
import dao.*;

import java.io.*;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DataPipeline<T, S> {
	private final IOFactory ioFactory;
	private final FormattedFactory<S> formattedFactory;
	private final Serializer<T, S> serializer;
	private final String filename;

	public DataPipeline(IOFactory ioFactory, FormattedFactory<S> formattedFactory, Serializer<T, S> serializer, String filename) {
		this.ioFactory = ioFactory;
		this.formattedFactory = formattedFactory;
		this.serializer = serializer;
		this.filename = filename;
	}


	private static final UserDAO users = UserDAO.getInstance();
	private static final PostDAO posts = PostDAO.getInstance();

	public void writeFrom(Iterator<T> iterator) {
		try {
			Writer writer = ioFactory.writer(filename);
			if (writer == null) return;
			FormattedWriter<S> formattedWriter = formattedFactory.writer(writer);

			while (iterator.hasNext()) {
				try {
					T item = iterator.next();
					if (item == null) continue;
					S serialized = serializer.serialize(item);
					if (serialized == null) continue;
					formattedWriter.putNext(serialized);
				} catch (EmptyStackException | NoSuchElementException e) {
					Log.w("Persistence", "Iterator exhausted early — stopping write");
					break;
				}
			}

			writer.close();
		} catch (EmptyStackException | NoSuchElementException | IOException e) {
			Log.w("Persistence", "Iterator exhausted early in " + filename + " — stopping write");
		}

	}

	public interface AddToDAO<T> {
		void run(T item);
	}

	public void readTo(AddToDAO<T> callback) {
		Log.d("Persistence", "readTo called for: " + filename);
		try {
			Reader reader = ioFactory.reader(filename);
			Log.d("Persistence", "reader is null: " + (reader == null));
			if (reader == null) return;
			FormattedReader<S> formattedReader = formattedFactory.reader(reader);
			Log.d("Persistence", "hasNext: " + formattedReader.hasNext());

			while (formattedReader.hasNext()) {
				try {
					callback.run(serializer.deserialize(formattedReader.getNext()));
				} catch (Exception e) {
					Log.e("Persistence", "Failed to deserialize row in " + filename + ": " + e.getMessage());
				}
			}

			reader.close();
		} catch (Exception e) { // catch all, not just IOException
			Log.e("Persistence", "Failed to read file " + filename + ": " + e.getMessage());
		}
	}
}
