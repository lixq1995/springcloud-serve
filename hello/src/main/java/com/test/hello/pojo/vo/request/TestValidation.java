package com.test.hello.pojo.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author by Lixq
 * @Classname TestValidation
 * @Description TODO
 * @Date 2021/4/3 23:50
 */
@Data
public class TestValidation {

    @NotBlank(message = "不能为空")
    @ApiModelProperty(value = "判空")
    private String Null;

    @Size(min = 2,max = 20,message = "只能输入2-20长度")
    @NotBlank(message = "不能为空")
    @ApiModelProperty(value = "长度")
    private String length;

    @ApiModelProperty(value = "标识，1暂存，2保存")
    private String flag;

    @NotNull(message = "不能为空")
    @ApiModelProperty(value = "list")
    private List<@Valid User> userList;

    @Min(value = 1,message = "必须大于等于1")
    @Max(value = 100,message = "必须小于等于100")
    @ApiModelProperty(value = "第几页")
    private String pageNum;

    @Min(value = 1,message = "必须大于等于1")
    @ApiModelProperty(value = "页大小")
    private Integer pageSize;

    @Range(min=0, max=120,message = "年龄必须在0至120之间")
    @ApiModelProperty(value = "年龄")
    private Integer age;

    @Pattern(regexp = "[0-9]",message = "请输入0-9的数字")
    @ApiModelProperty(value = "正则测试")
    private String PatternTest;

    @Email(message = "请输入正确的邮箱格式")
    @NotBlank(message = "不能为空")
    @ApiModelProperty(value = "邮箱")
    private String email;

}
