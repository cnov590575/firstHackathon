package persistentdata.formatted;

import persistentdata.PersistentDataException;

import java.io.IOException;
import java.io.Reader;

public class CSVReader implements FormattedReader<String[]> {
	private final CSVFormat format;
	private final Reader reader;

	public CSVReader(CSVFormat format, Reader reader) {
		this.format = format;
		this.reader = reader;
	}

	public boolean hasNext() {
        try {
            reader.mark(2); // Mark the current position
            int nextChar = reader.read();
            reader.reset();
            return nextChar != -1;
        } catch (IOException e) {
            return false;
        }
	}

	// These format strings are provided to give you some ideas about what error cases might be encountered,
	// but they aren't complete. If you haven't seen these before, you can fill in the %s with .formatted:
	// for example, "hello %s".formatted("Bernardo") returns "hello Bernardo"
	private static final String LINE_TOO_SHORT_MESSAGE = "Line was too short: expected %s fields but found %s";
	private static final String LINE_TOO_LONG_MESSAGE = "Line was too long: expected %s fields";
	private static final String IMPROPER_ESCAPE_MESSAGE = "EOF reached unexpectedly while escaped";
	private static final String REACHED_EOF_MESSAGE = "Already reached end of file while reading";

	public String[] getNext() {
		if (!hasNext()) return null;
        String[] nextItem = new String[format.COLUMN_COUNT];
        nextItem[0]="";
        boolean withinEscape = false;
        int curField = 0;
        try {
            int curchar = reader.read();
            while ((((char) curchar) != format.LINE_SEPARATOR || withinEscape) && curchar != -1) {
                if (((char) curchar) == format.ESCAPE_MARKER && withinEscape){
                    try {
                        reader.mark(2); // Mark the current position
                        int nextChar = reader.read();
                        reader.reset();
                        if ((char) nextChar == format.ESCAPE_MARKER) {
                            nextItem[curField]=nextItem[curField]+(char) curchar;
                            reader.read();
                        }
                        else withinEscape = false;
                    } catch (IOException e) {
                        throw new CSVIOException(IMPROPER_ESCAPE_MESSAGE);
                    }
                }
                else if (((char) curchar) == format.ESCAPE_MARKER) withinEscape = true;
                else {
                    if (withinEscape) {
                        nextItem[curField]=nextItem[curField]+(char) curchar;
                        reader.mark(2);
                        int nextChar = reader.read();
                        reader.reset();
                        if (nextChar == -1) {
                            throw new CSVIOException(IMPROPER_ESCAPE_MESSAGE);
                        }

                    }
                    else {
                        if ((char) curchar == format.FIELD_SEPARATOR) {
                            curField = curField + 1;
                            if (curField >= format.COLUMN_COUNT) throw new CSVIOException(LINE_TOO_LONG_MESSAGE.formatted(format.COLUMN_COUNT));
                            nextItem[curField] = "";
                        }
                        else nextItem[curField]=nextItem[curField]+(char) curchar;
                    }
                }
                curchar=reader.read();
            }
            if (withinEscape) throw new CSVIOException(REACHED_EOF_MESSAGE);
        } catch (IOException e) {
            throw new CSVIOException(LINE_TOO_LONG_MESSAGE.formatted(format.COLUMN_COUNT));
        }

        if (nextItem[format.COLUMN_COUNT-1]==null) throw new CSVIOException(LINE_TOO_SHORT_MESSAGE.formatted(format.COLUMN_COUNT, curField));
        return nextItem;
	}

	public static class CSVIOException extends PersistentDataException {
		public CSVIOException(String message) {
			super(message);
		}
	}
}
