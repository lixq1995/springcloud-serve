package com.test.common.util;


import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * @author by Lixq
 * @Classname JsonUtil
 * @Description TODO
 * @Date 2021/4/5 21:48
 */
public class JsonUtil {

    public static final SerializerFeature[] SERIALIZER_FEATURES = {
            // 输出空置字段
            SerializerFeature.WriteMapNullValue,

            // list字段如果为空，输出为[]，而不是null
            SerializerFeature.WriteNullListAsEmpty,

            // 数值字段如果为null，输出为0，而不是null
            SerializerFeature.WriteNullNumberAsZero,

            // boolean字段如果为null，输出为false，而不是null
            SerializerFeature.WriteNullBooleanAsFalse,

            // 字符串字段如果为null，输出为""，而不是null
            SerializerFeature.WriteNullStringAsEmpty,
    };

    public static final Feature[] FEATURES = {
            // 按顺序打印执行
            Feature.OrderedField,
    };


    public static void main(String[] args) {
        SerializerFeature feature = JsonUtil.SERIALIZER_FEATURES[0];
        System.out.println(feature);
    }
}
