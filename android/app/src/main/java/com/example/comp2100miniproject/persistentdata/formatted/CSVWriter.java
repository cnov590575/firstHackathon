package com.example.comp2100miniproject.persistentdata.formatted;

import java.io.IOException;
import java.io.Writer;

public class CSVWriter implements FormattedWriter<String[]> {
	private final CSVFormat format;
	private final Writer writer;
    private boolean first = true;

	public CSVWriter(CSVFormat format, Writer writer) {
		this.format = format;
		this.writer = writer;
	}

	@Override
	public void putHeader() {
		// TODO: Complete this method according to the CSV specification, without using dedicated libraries
	}

    @Override
    public void putNext(String[] data) {
        // TODO: Complete this method according to the CSV specification, without using dedicated libraries
        try {
            if (!first) {
                writer.write(format.LINE_SEPARATOR);
            }
            first = false;

            for (int i = 0; i < data.length; i++) {
                String field = data[i];

                if (field.contains(Character.toString(format.FIELD_SEPARATOR)) ||
                        field.contains(Character.toString(format.LINE_SEPARATOR)) ||
                        field.contains(Character.toString(format.ESCAPE_MARKER))) {

                    writer.write(format.ESCAPE_MARKER);

                    for (int j = 0; j < field.length(); j++) {
                        char ch = field.charAt(j);
                        if (ch == format.ESCAPE_MARKER) {
                            writer.write(format.ESCAPE_MARKER);
                        }
                        writer.write(ch);
                    }

                    writer.write(format.ESCAPE_MARKER);

                } else {
                    writer.write(field);
                }

                if (i != data.length - 1) {
                    writer.write(format.FIELD_SEPARATOR);
                }
            }

        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

	@Override
	public void putFooter() {
		// TODO: Complete this method according to the CSV specification, without using dedicated libraries
	}

}
