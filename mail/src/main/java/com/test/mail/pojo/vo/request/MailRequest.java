package com.test.mail.pojo.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author by Lixq
 * @Classname MailRequest
 * @Description TODO
 * @Date 2021/5/16 18:04
 */
@Data
public class MailRequest {

    @ApiModelProperty(value = "邮件接收者")
    private String toEmail;

    @ApiModelProperty(value = "邮件主题")
    private String subject;

    @ApiModelProperty(value = "邮件内容")
    private String content;

}
