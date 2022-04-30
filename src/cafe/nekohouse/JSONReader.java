package cafe.nekohouse;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JSONReader {
    public static JSONObject readJSON(String fileName) throws IOException {
        File f = new File(fileName);
        String content = new String(Files.readAllBytes(Paths.get(f.getAbsolutePath())));
        return new JSONObject(content);
    }
}
