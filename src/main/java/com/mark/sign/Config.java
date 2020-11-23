package com.mark.sign;

import jdk.nashorn.internal.ir.debug.JSONWriter;
import jdk.nashorn.internal.parser.JSONParser;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class Config {
    public static final String KEY_STORE_PATH = "keyStorePath";
    public static final String KEY_STORE_PASSWORD = "keyStorePwd";
    public static final String KEY_ALIAS_PASSWORD = "keyAliasPwd";
    public static final String KEY_ALIAS= "keyAlias";
    public static final String APK_OUTPUT_PATH = "apkOutputPath";

    private static final String configFileName = "signConfig";

    private final String separator = "  ";

    private static Config config;

    private Config(){}

    public static Config init(){
        if (config == null){
            config = new Config();
        }
        return config;
    }


    public void saveConfig(Map<String, String> config) {
        if (config == null) return;
        try {
            String str = configEncoder(config);
            File file = getConfigFile();
            FileWriter writer = new FileWriter(file);
            writer.write(str);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String,String> getConfig(){
        return configDecoder();
    }



    private String configEncoder(Map<String, String> config) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String,String> map : config.entrySet()){
            sb.append(map.getKey()).append(separator).append(map.getValue()).append("\r\n");
        }
        return sb.toString();
    }

    private Map<String,String> configDecoder(){
        File file = getConfigFile();
        if (!file.exists()){
            return null;
        }
        try {
            Map<String,String> map = new HashMap<String, String>();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null){
                String[] sp = line.split(separator);
                map.put(sp[0],sp[1]);
            }
            reader.close();
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private File getConfigFile() {
        String path = new File("").getAbsolutePath();
        File file = new File(path, configFileName);
        return file;
    }

}
