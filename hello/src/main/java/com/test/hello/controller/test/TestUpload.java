package com.test.hello.controller.test;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class TestUpload {

    public static void main(String[] args) {
        testUpload1();
//        testUpload2();
    }


    private static void testUpload1() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        File file = new File("D:/downPath/word企业消防安全管理制度.docx");
        HttpPost post = new HttpPost("http://localhost:8080/file/upload");
        // todo 需注释掉请求头Content-Type，否则报错。 可能原因，entity中header里已设置Content-Type为application/octet-stream（不确定）
//        post.addHeader("Content-Type","application/json");
//        post.addHeader("Content-Type","multipart/form-data");
        FileBody fileBody = new FileBody(file, ContentType.DEFAULT_BINARY);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setCharset(Charset.forName("UTF-8"));
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addPart("file", fileBody);
        HttpEntity entity = builder.build();
        post.setEntity(entity);
        try {
            CloseableHttpResponse response = httpClient.execute(post);
            String resultString = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println(resultString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testUpload2() {
        com.mashape.unirest.http.HttpResponse<JsonNode> file = null;
        try {
            file = Unirest.post("http://localhost:8080/file/upload")
                    .field("file", new File("D:/downPath/word企业消防安全管理制度.docx")).asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        System.out.println(file.getBody());
    }
}
