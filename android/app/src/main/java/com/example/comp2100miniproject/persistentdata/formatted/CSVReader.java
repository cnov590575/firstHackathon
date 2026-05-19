package com.example.comp2100miniproject.persistentdata.formatted;

import com.example.comp2100miniproject.persistentdata.PersistentDataException;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

public class CSVReader implements FormattedReader<String[]> {
    private final CSVFormat format;
    private final Reader reader;
    private String nextline;

    public CSVReader(CSVFormat format, Reader reader) {
        this.format = format;
        this.reader = reader;

        try {
            this.nextline = readNextLine();
        } catch (IOException e) {
            throw new CSVIOException(e.getMessage());
        }

    }

    private String readNextLine() throws IOException {
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        int c;
        while ((c = reader.read()) != -1) {
            char ch = (char) c;

            if (ch == format.ESCAPE_MARKER) {
                sb.append(ch);

                if (inQuotes) {
                    reader.mark(1);
                    int next = reader.read();

                    if (next == format.ESCAPE_MARKER) {
                        sb.append((char) next);
                    } else {
                        inQuotes = false;
                        if (next != -1) reader.reset();
                    }
                } else {
                    inQuotes = true;
                }
            }
            else if (ch == format.LINE_SEPARATOR && !inQuotes) {
                break;
            }
            else {
                sb.append(ch);
            }
        }

        if (c == -1 && sb.length() == 0) {
            return null;
        }

        if (inQuotes) {
            throw new CSVIOException(IMPROPER_ESCAPE_MESSAGE);
        }

        return sb.toString();
    }

    public boolean hasNext() {
        // TODO: Complete this method according to the CSV specification, without using dedicated libraries
        return nextline != null;
    }

    // These format strings are provided to give you some ideas about what error cases might be encountered,
    // but they aren't complete. If you haven't seen these before, you can fill in the %s with .formatted:
    // for example, "hello %s".formatted("Bernardo") returns "hello Bernardo"
    private static final String LINE_TOO_SHORT_MESSAGE = "Line was too short: expected %s fields but found %s";
    private static final String LINE_TOO_LONG_MESSAGE = "Line was too long: expected %s fields";
    private static final String IMPROPER_ESCAPE_MESSAGE = "EOF reached unexpectedly while escaped";
    private static final String REACHED_EOF_MESSAGE = "Already reached end of file while reading";

    public String[] getNext() {
        if (!hasNext()) throw new CSVIOException(REACHED_EOF_MESSAGE);

        ArrayList<String> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < nextline.length(); i++) {
            char ch = nextline.charAt(i);

            if (ch == format.ESCAPE_MARKER) {
                if (inQuotes) {
                    if (i + 1 < nextline.length() && nextline.charAt(i + 1) == format.ESCAPE_MARKER) {
                        sb.append(format.ESCAPE_MARKER);
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    inQuotes = true;
                }
            }
            else if (ch == format.FIELD_SEPARATOR && !inQuotes) {
                list.add(sb.toString());
                sb.setLength(0);
            }
            else {
                sb.append(ch);
            }
        }

        if (inQuotes) {
            throw new CSVIOException(IMPROPER_ESCAPE_MESSAGE);
        }

        list.add(sb.toString());

        String[] current = list.toArray(new String[0]);

        if (current.length < format.COLUMN_COUNT) {
            throw new CSVIOException(
                    LINE_TOO_SHORT_MESSAGE.formatted(format.COLUMN_COUNT, current.length)
            );
        }

        if (current.length > format.COLUMN_COUNT) {
            throw new CSVIOException(
                    LINE_TOO_LONG_MESSAGE.formatted(format.COLUMN_COUNT)
            );
        }

        try {
            nextline = readNextLine();
        } catch (IOException e) {
            throw new CSVIOException(e.getMessage());
        }

        return current;
    }

    public static class CSVIOException extends PersistentDataException {
        public CSVIOException(String message) {
            super(message);
        }
    }
}