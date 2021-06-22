package com.zero.cdownload.util;

import java.text.DecimalFormat;

/**
 * @Name: ClassicDownload
 * @Description: 描述信息
 * @Author: Created by heyong on 6/21/21
 */
public class Utils {
    public static String formatSize(long size) {
        if (size <= 0) {
            return "0B";
        }
        double kb = size / 1024.0;
        double mb = size / 1024.0 / 1024.0;
        double gb = size / 1024.0 / 1024.0 / 1024.0;
        double tb = size / 1024.0 / 1024.0 / 1024.0 / 1024.0;
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
        String resultSize = "";
        if (tb >= 1) {
            //如果当前Byte的值大于等于1TB
            resultSize = df.format(tb) + "TB";
        } else if (gb >= 1) {
            //如果当前Byte的值大于等于1GB
            resultSize = df.format(gb) + "G";
        } else if (mb >= 1) {
            //如果当前Byte的值大于等于1MB
            resultSize = df.format(mb) + "M";
        } else if (kb >= 1) {
            //如果当前Byte的值大于等于1KB
            resultSize = df.format(kb) + "KB";
        } else {
            resultSize = size + "B";
        }
        return resultSize;
    }
}
