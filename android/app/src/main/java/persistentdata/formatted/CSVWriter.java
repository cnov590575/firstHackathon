package persistentdata.formatted;

import persistentdata.PersistentDataException;

import java.io.IOException;
import java.io.Writer;

public class CSVWriter implements FormattedWriter<String[]> {
	private final CSVFormat format;
	private final Writer writer;
    private boolean firstLine = true;

	public CSVWriter(CSVFormat format, Writer writer) {
		this.format = format;
		this.writer = writer;
	}

	@Override
	public void putHeader() {
        try {
            writer.write("");
        } catch (Exception e) {
            throw new PersistentDataException("Writer improperly instantiated");
        }
	}

	@Override
	public void putNext(String[] data) {
        if (data == null) throw new PersistentDataException("Cannot write Null data");
        else if  (data.length != format.COLUMN_COUNT) throw new PersistentDataException("Given data columns do not match CSV format specification");
        try {
            if (!firstLine) {
                writer.write(format.LINE_SEPARATOR);
            }
            for (int i = 0; i<data.length; i++) {
                boolean containsSpecialChar = false;
                String str = data[i];
                for (char ch : str.toCharArray()) {
                    if (ch == format.ESCAPE_MARKER || ch == format.FIELD_SEPARATOR || ch == format.LINE_SEPARATOR) {
                        containsSpecialChar = true;
                        break;
                    }
                }
                if (containsSpecialChar) writer.write(format.ESCAPE_MARKER);
                for (char ch : str.toCharArray()) {
                    if (ch == format.ESCAPE_MARKER) {
                        writer.write(format.ESCAPE_MARKER);
                        writer.write(format.ESCAPE_MARKER);
                    }
                    else writer.write(ch);
                }
                if (containsSpecialChar) writer.write(format.ESCAPE_MARKER);
                if (i< data.length-1) writer.write(format.FIELD_SEPARATOR);
            }
            firstLine=false;
        } catch (Exception e) {
            throw new PersistentDataException("Writer improperly instantiated");
        }
	}

	@Override
	public void putFooter() {
        try {
            writer.write("");
        } catch (Exception e) {
            throw new PersistentDataException("Writer improperly instantiated");
        }
	}

}
