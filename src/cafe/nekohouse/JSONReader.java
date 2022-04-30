package cafe.nekohouse;



import com.alibaba.fastjson.JSONObject;

import java.io.IOException;


public class JSONReader {
    public static JSONObject readJSON(String fileName) throws IOException {
        String content = J2TS.readFile(fileName);
        return JSONObject.parseObject(content);
    }
}
