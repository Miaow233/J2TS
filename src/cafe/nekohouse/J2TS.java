package cafe.nekohouse;

import org.bsc.java2typescript.TSType;
import org.bsc.java2typescript.TypescriptConverter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class J2TS {
    static Map<String, TSType> declaredTypeMap = new HashMap<>();

    /* formatting: Packages.%s */
    public TypescriptConverter converter;

    J2TS(TypescriptConverter.Compatibility compatibility) {
        this.converter = new TypescriptConverter(TypescriptConverter.Compatibility.RHINO);
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        addPackage("java.lang");
        addPackage("java.io");
        addPackage("java.net");

        J2TS j2ts = new J2TS(TypescriptConverter.Compatibility.RHINO);
        j2ts.convert("java.lang.String");
        j2ts.convert("java.lang.Class");
        j2ts.convert("java.lang.ClassLoader");
        j2ts.convert("java.lang.Process");
        j2ts.convert("java.lang.ProcessBuilder");
        j2ts.convert("java.lang.Runtime");
        marge("java.lang");
        j2ts.convert("java.io.File");
        j2ts.convert("java.io.FileInputStream");
        j2ts.convert("java.io.FileOutputStream");
        j2ts.convert("java.io.InputStream");
        j2ts.convert("java.io.OutputStream");
        j2ts.convert("java.io.PrintStream");
        j2ts.convert("java.io.PrintWriter");
        j2ts.convert("java.io.BufferedInputStream");
        j2ts.convert("java.io.BufferedOutputStream");
        j2ts.convert("java.io.BufferedReader");
        j2ts.convert("java.io.BufferedWriter");
        j2ts.convert("java.io.ByteArrayInputStream");
        j2ts.convert("java.io.ByteArrayOutputStream");
        j2ts.convert("java.io.DataInputStream");
        j2ts.convert("java.io.DataOutputStream");
        j2ts.convert("java.io.FileInputStream");
        j2ts.convert("java.io.FileOutputStream");
        j2ts.convert("java.io.InputStreamReader");
        j2ts.convert("java.io.OutputStreamWriter");
        j2ts.convert("java.io.PrintStream");
        marge("java.io");
    }

    private void convert(String cl) throws IOException {

        Path p = Paths.get("G:\\IntelliJIDEAProjects\\J2TS\\src\\resources\\types\\" + cl + ".d.ts");
        Files.write(p, converter.processClass(3, declaredTypeMap.get(cl), declaredTypeMap).getBytes());
        p = Paths.get("G:\\IntelliJIDEAProjects\\J2TS\\src\\resources\\" + cl + ".ts");
        Files.write(p, converter.processStatic(declaredTypeMap.get(cl), declaredTypeMap).getBytes());
    }

    private static void addType(String className) throws ClassNotFoundException {
        Class<?> cl = Class.forName(className);
        declaredTypeMap.put(className, TSType.of(cl));
    }

    private static void addPackage(String packageName) throws IOException, ClassNotFoundException {

        JSONObject json = JSONReader.readJSON("G:\\IntelliJIDEAProjects\\J2TS\\src\\resources\\" + packageName + ".json");

        JSONArray interfaces = json.getJSONArray("Interfaces");
        interfaces.forEach(key -> {
            try {
                addType(packageName + "." + key);
            } catch (ClassNotFoundException e) {
                System.out.println(packageName + "." + key + "not found");
            }
        });

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
        String base = "G:\\IntelliJIDEAProjects\\J2TS\\src\\resources";

        File typeDir = new File(base + "\\types");
        File[] files = typeDir.listFiles((dir1, name) -> name.startsWith(packageName) && name.endsWith(".d.ts"));
        StringBuilder sb = new StringBuilder();
        for (File file : files) {
            // read file
            sb.append(new String(Files.readAllBytes(file.toPath())));
            file.delete();
        }
        // write file
        Path p = Paths.get(typeDir.getAbsolutePath() + "\\" + packageName + ".d.ts");
        Files.write(p, sb.toString().getBytes());

        File staticDir = new File(base );
        files = staticDir.listFiles((dir1, name) -> name.startsWith(packageName) && name.endsWith(".ts"));
        sb = new StringBuilder();
        for (File file : files) {
            // read file
            sb.append(new String(Files.readAllBytes(file.toPath())));
            file.delete();

        }
        // write file
        p = Paths.get(staticDir.getAbsolutePath() + "\\" + packageName + ".ts");
        Files.write(p, sb.toString().getBytes());

    }
}
