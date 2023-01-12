package com.test.common.util.string;

import java.text.MessageFormat;

/**
 * @author newupbank
 */
public class StringUtil {

    public static void main(String[] args) {

        String desc = "尊敬的{0}：{1}好";
        String formatDesc = getFormatDesc(desc, "李", "下午");
        System.out.println(formatDesc);
    }

    public static String getFormatDesc(String desc,Object... labs) {
        Object[] infos = new String[labs.length];
        for (int i = 0; i < labs.length; i++) {
            // 统一处理string
            infos[i] = String.valueOf(labs[i]);
        }
        return MessageFormat.format(desc,infos);
    }
}
