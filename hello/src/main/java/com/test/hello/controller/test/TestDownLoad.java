package com.test.hello.controller.test;

import com.alibaba.fastjson.JSON;
import com.test.common.util.http.HttpSslClientUtil;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;

import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;

public class TestDownLoad {

    public static void main(String[] args) {
        testDownLoad1("民族码值表.xlsx");

    }

    private static String testDownLoad1(String downFileName) {
        HashMap<String, String> map = new HashMap<>();
        map.put("fileName",downFileName);
        String jsonStringFile = JSON.toJSONString(map);
        HttpResponse responseFile = HttpSslClientUtil.doPostJson("http://localhost:8080/file/download/file", jsonStringFile, map);
        String fileName = "";
        HeaderElement[] elements = responseFile.getFirstHeader("Content-Disposition").getElements();
        for (HeaderElement h: elements) {
            NameValuePair nameValuePair = h.getParameterByName("filename");
            if (nameValuePair != null) {
                try {
                    //此处根据具体编码来设置
                    fileName = new String(nameValuePair.getValue().toString().getBytes("utf-8"), "utf-8");
                    fileName = URLDecoder.decode(fileName, "utf-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            InputStream inputStream = responseFile.getEntity().getContent();
            File file = new File("D:\\downPath",fileName);
            if (file.exists()) {
                file.delete();
            }
            OutputStream fileOutputStream = new FileOutputStream(file);
            int len = -1;
            byte[] bytes = new byte[1024];
            while ((len = inputStream.read(bytes)) != -1) {
                fileOutputStream.write(bytes,0,len);
            }
            inputStream.close();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(responseFile);
        return "success";
    }
}
