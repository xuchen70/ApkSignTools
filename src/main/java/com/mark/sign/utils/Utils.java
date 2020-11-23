package com.mark.sign.utils;

import java.io.*;

public class Utils {

    public static String readAlias(String path,String password){
        if (StringUtils.isEmpty(path) || StringUtils.isEmpty(password)){
            return null;
        }
        try {
            String alias = null;
            String cmd = String.format("keytool -list -v -keystore %s -storepass %s",path,password);
            Process process = Runtime.getRuntime().exec(cmd);
            BufferedInputStream bis = new BufferedInputStream(process.getInputStream());
            InputStreamReader reader = new InputStreamReader(process.getInputStream(), getCharset());
            BufferedReader br = new BufferedReader(reader);
            String line;
            while ((line = br.readLine()) != null) {
//            System.out.println(line);
                if (line.startsWith("别名") && line.contains(":")) {
                    String[] split = line.split(": ");
                    if (split.length > 1) {
                        alias = split[1];
                        return alias;
                    }
                }
            }

            process.waitFor();
            if (process.exitValue() != 0) {
                System.out.println("error!");
            }
            bis.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getOsName(){
        return System.getProperty("os.name").toLowerCase();
    }

    private static String getCharset() {
        String os = getOsName();
        if (os.contains("window")) {
            return "GBK";
        } else if (os.contains("linux")) {
            return "UTF-8";
        }
        return "GBK";
    }

}
