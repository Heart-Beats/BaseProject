package com.tencent.shadow.dynamic.impl;

/**
 * 此类包名及类名固定
 * ApkClassLoader 的白名单
 * 插件 loader 可以加载宿主中位于白名单内的类
 */
public interface WhiteList {

    /**
     * 该字段名也需固定， 定义的为包名， 包下的所有类都可被插件加载使用
     */
    String[] sWhiteList = new String[]
            {
                    "com.hl.shadow.dynamic.impl"
            };
}
