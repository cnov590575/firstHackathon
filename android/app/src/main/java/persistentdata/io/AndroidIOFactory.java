package persistentdata.io;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class AndroidIOFactory implements IOFactory {

    private final File storageDir;

    public AndroidIOFactory(Context context) {
        this.storageDir = context.getFilesDir();
    }

    @Override
    public Reader reader(String filename) throws FileNotFoundException {
        File file = new File(storageDir, filename + ".csv");
        Log.d("Persistence", "Reading from: " + file.getAbsolutePath() + " exists: " + file.exists());
        if (!file.exists()) return null;
        return new BufferedReader(new FileReader(file));
    }

    @Override
    public Writer writer(String filename) throws IOException {
        File finalFile = new File(storageDir, filename + ".csv");
        File tempFile  = new File(storageDir, filename + ".tmp");
        Log.d("Persistence", "Writing to: " + finalFile.getAbsolutePath());
        // Return a writer that writes to .tmp, then renames to .csv on close
        return new Writer() {
            private final Writer inner = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(tempFile, false), StandardCharsets.UTF_8));

            @Override public void write(char[] buf, int off, int len) throws IOException { inner.write(buf, off, len); }
            @Override public void flush() throws IOException { inner.flush(); }

            @Override
            public void close() throws IOException {
                inner.close();
                if (!tempFile.renameTo(finalFile)) {
                    Files.copy(tempFile.toPath(), finalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    tempFile.delete();
                }
            }
        };
    }
}
