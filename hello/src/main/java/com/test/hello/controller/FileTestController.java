package com.test.hello.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author by Lixq
 * @Classname FileTestController
 * @Description TODO
 * @Date 2021/5/23 14:42
 */
@RestController
@RequestMapping("/file")
@Api(tags = "文件上传下载控制层")
@Slf4j
public class FileTestController {

    private final static List<String> fileType = new ArrayList<>();

    static {
        fileType.add("jpg");
        fileType.add("txt");
        fileType.add("xlsx");
        fileType.add("doc");
    }


    @RequestMapping(value = "upload",method = RequestMethod.POST)
    public String upload(MultipartFile multipartFile, HttpServletRequest request) throws IOException {
        //获取项目根路径
        String uploadPath = "E:\\ideaTool\\ideaSource\\springcloud-serve\\hello\\src\\main\\resources";
        System.out.println("路径 = " + request.getSession().getServletContext().getRealPath("/"));
        System.out.println("文件名 = " + multipartFile.getOriginalFilename());
        System.out.println("文件类型 = " + multipartFile.getContentType());
        System.out.println("文件大小 = " + multipartFile.getSize());
        String realPath = uploadPath + "/filetest";
        File file = new File(realPath, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        //判断文件父目录是否存在
        if(!file.exists()) {
            // 创建文件
            file.mkdirs();
        }
        // 获取文件名字
        String originalFilename = multipartFile.getOriginalFilename();
        // 获取文件类型
        String extension = FilenameUtils.getExtension(originalFilename);
        // todo 效验文件类型
        if (fileType.contains(extension)) {
            System.out.println("文件不合法");
        }
        // 文件大小校验
        // 文件名是否合法校验
        String newName = UUID.randomUUID().toString().replaceAll("-", "") + new SimpleDateFormat("YYYYMMddHHmmss").format(new Date()) + "." + extension;
        multipartFile.transferTo(new File(file, newName));
        return "success";
    }

    @GetMapping("/download/{downFileName}")
    public String downLoad(@PathVariable String downFileName,
                           HttpServletResponse response)
            throws UnsupportedEncodingException {
        //String filename="test.jpg";
        String filename = downFileName;
        String filePath = "C:/test";
        File file = new File(filePath + "/" + filename);
        //判断文件父目录及文件是否存在
        if (file.exists()) {
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            // response.setContentType("application/force-download");
            response.setHeader("Content-Disposition", "attachment;fileName=" + java.net.URLEncoder.encode(filename, "UTF-8"));
            byte[] buffer = new byte[1024];
            FileInputStream fis = null; //文件输入流
            BufferedInputStream bis = null;

            OutputStream os = null; //输出流
            try {
                os = response.getOutputStream();
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer);
                    i = bis.read(buffer);
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("----------file download---" + filename);
            try {
                bis.close();
                fis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(UUID.randomUUID());
        System.out.println(new SimpleDateFormat("YYYYMMddHHmmss").format(new Date()));
        boolean contains = fileType.contains("1");
        System.out.println(contains);
    }
}
