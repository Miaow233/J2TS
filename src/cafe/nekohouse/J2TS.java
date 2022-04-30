package cafe.nekohouse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import org.bsc.java2typescript.TSType;
import org.bsc.java2typescript.TypescriptConverter;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class J2TS {
    static Map<String, TSType> declaredTypeMap = new HashMap<>();

    static final String base = "G:\\IntelliJIDEAProjects\\J2TS\\src\\resources";

    /* formatting: Packages.%s */
    public final TypescriptConverter converter;

    J2TS() {
        this.converter = new TypescriptConverter(TypescriptConverter.Compatibility.RHINO);
    }

    public static void main(String[] args) throws IOException, JSONException {
        J2TS j2ts = new J2TS();
        j2ts.addPackage("java.lang");
        j2ts.addPackage("java.lang.reflect");
        j2ts.addPackage("java.io");
        j2ts.addPackage("java.net");
        j2ts.addPackage("java.util");
        j2ts.addPackage("java.util.stream");

        j2ts.convertPackage(j2ts, "java.lang");
        j2ts.convertPackage(j2ts, "java.lang.reflect");
        j2ts.convertPackage(j2ts, "java.io");
        j2ts.convertPackage(j2ts, "java.net");
        j2ts.convertPackage(j2ts, "java.util");
        j2ts.convertPackage(j2ts, "java.util.stream");
    }

    private void convert(String cl) throws IOException {
        writeFile(base + "\\types\\" + cl + ".d.ts",
                converter.processClass(3, declaredTypeMap.get(cl), declaredTypeMap));
        writeFile(base + "\\" + cl + ".ts",
                converter.processStatic(declaredTypeMap.get(cl), declaredTypeMap));
    }

    private static void addType(String className) throws ClassNotFoundException {
        Class<?> cl = Class.forName(className);
        declaredTypeMap.put(className, TSType.of(cl));
    }

    private void addPackage(String packageName) throws IOException, JSONException {

        JSONObject json = JSONReader.readJSON(base + "\\" + packageName + ".json");

        JSONArray interfaces = json.getJSONArray("Interfaces");

        for (Object interfaceName : interfaces) {
            try {
                addType(packageName + "." + interfaceName.toString());
            } catch (ClassNotFoundException e) {
                System.out.println(packageName + "." + interfaceName + "not found");
            }
        }

        JSONArray classes = json.getJSONArray("Classes");

        classes.forEach(key -> {
            try {
                addType(packageName + "." + key);
            } catch (ClassNotFoundException e) {
                System.out.println(packageName + "." + key + "not found");
            }
        });
    }

    public static void marge(String packageName) throws IOException {
        {
            File typeDir = new File(base + "\\types");
            File[] files = typeDir.listFiles((dir1, name) -> name.startsWith(packageName) && name.endsWith(".d.ts") && !name.toLowerCase().equals(packageName + ".d.ts"));
            StringBuilder sb = new StringBuilder();
            for (File file : files) {
                sb.append(readFile(file.getAbsolutePath()));
                file.delete();
            }
            writeFile(base + "\\types\\" + packageName + ".d.ts", sb.toString());
        }
        {
            File staticDir = new File(base);
            File[] files = staticDir.listFiles((dir1, name) -> name.startsWith(packageName) && name.endsWith(".ts") && !name.toLowerCase().equals(packageName + ".ts"));
            StringBuilder sb = new StringBuilder();
            for (File file : files) {
                sb.append(readFile(file.getAbsolutePath()));
                file.delete();
            }
            writeFile(base + "\\" + packageName + ".ts", sb.toString());
        }
    }

    public static void writeFile(String fileName, String content) throws IOException {
        FileWriter out = new FileWriter(fileName);
        out.write(content);
        out.close();
    }

    public static String readFile(String fileName) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(fileName));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        in.close();
        return sb.toString();
    }

    public void convertPackage(J2TS j2ts, String packageName) throws IOException, JSONException {

        JSONObject json = JSONReader.readJSON(base + "\\" + packageName + ".json");
        JSONArray classes = json.getJSONArray("Classes");
        classes.forEach(key -> {
            try {
                j2ts.convert(packageName + "." + key);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        JSONArray interfaces = json.getJSONArray("Interfaces");
        interfaces.forEach(key -> {
            try {
                j2ts.convert(packageName + "." + key);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        marge(packageName);
    }
}
