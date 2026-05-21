package persistentdata.io;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
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
        return new BufferedReader(new FileReader(file)); // ✅ BufferedReader supports mark/reset
    }

    @Override
    public Writer writer(String filename) throws IOException {
        File file = new File(storageDir, filename + ".csv");
        Log.d("Persistence", "Writing to: " + file.getAbsolutePath());
        // Use OutputStreamWriter to force \n instead of system line separator
        return new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8);
    }
}
