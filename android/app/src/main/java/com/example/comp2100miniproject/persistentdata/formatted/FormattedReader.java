package com.example.comp2100miniproject.persistentdata.formatted;

import com.example.comp2100miniproject.persistentdata.PersistentDataException;

import java.io.IOException;

/**
 * Reads some data according to a particular format
 * @param <S> The serialised format to which the data should be parsed
 */
public interface FormattedReader<S> {
	/**
	 * Determines if the data has further serialised entries that have not yet been processed
	 * @return true if further entries exist, false otherwise
	 */
	boolean hasNext() throws IOException;

	/**
	 * Processes the next entry in the data.
	 * @return the serialised entry
	 * @exception PersistentDataException if the entry is malformed
	 * or otherwise illegally formatted, or if no further entries exist
	 */
	S getNext();
}
