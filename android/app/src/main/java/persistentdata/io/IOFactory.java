package persistentdata.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public interface IOFactory {
	public Writer writer(String filename) throws IOException;
	public Reader reader(String filename) throws FileNotFoundException;
}
