package com.im.core.utils;

import com.google.gson.Gson;

/**
 * @ClassName: GSONParser
 * @Description: TODO
 * @Author: Lyon.Cao
 * @Date: 2020/5/17 14:58
 * @Version: 1.0.0
 **/
public class GSONParser {

    private static Gson gson = new Gson();

    public static Gson getInstance() {
        return gson;
    }

}
