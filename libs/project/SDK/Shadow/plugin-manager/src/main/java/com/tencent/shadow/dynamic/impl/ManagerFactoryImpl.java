package com.tencent.shadow.dynamic.impl;

import android.content.Context;

import com.hl.shadow.pluginmanager.MyPluginManager;
import com.tencent.shadow.dynamic.host.ManagerFactory;
import com.tencent.shadow.dynamic.host.PluginManagerImpl;

/**
 * 此类包名及类名固定, ManagerImplLoader 会加载该路径下的 class 文件
 */
public final class ManagerFactoryImpl implements ManagerFactory {
    @Override
    public PluginManagerImpl buildManager(Context context) {
        return new MyPluginManager(context);
    }
}