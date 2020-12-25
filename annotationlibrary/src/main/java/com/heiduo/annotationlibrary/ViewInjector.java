package com.heiduo.annotationlibrary;

import android.app.Activity;
import android.view.View;

public
/**
 * 描述：
 * @author Created by heiduo
 * @time Created on 2020/12/25
 */
class ViewInjector {
    private static final String SUFFIX = "$$ViewInject";

    public static void injectView(Activity activity){
        ViewInject viewInject = findProxyActivity(activity);
        if(viewInject == null){
            return;
        }
        viewInject.inject(activity,activity);
    }

    public static void injectView(Object object, View view){
        ViewInject viewInject = findProxyActivity(object);
        if(viewInject == null){
            return;
        }
        viewInject.inject(object,view);
    }

    /**
     * 根据使用注解的类和约定的命名规则，反射获取注解生成的类
     *
     * @param object
     * @return
     */
    private static ViewInject findProxyActivity(Object object) {
        try {
            Class clazz = object.getClass();
            Class injectorClazz = Class.forName(clazz.getName() + SUFFIX);
            return (ViewInject) injectorClazz.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new RuntimeException(String.format("can not find %s , something when compiler.", object.getClass().getSimpleName() + SUFFIX));
    }
}
