import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

public class testGson {
    public static void main(String[] args) {
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("1","11");
        hashMap.put("2","22");
        hashMap.put("3","33");
        Gson gson = new GsonBuilder().create();
        System.out.println(gson.toJson(hashMap));
    }
}
