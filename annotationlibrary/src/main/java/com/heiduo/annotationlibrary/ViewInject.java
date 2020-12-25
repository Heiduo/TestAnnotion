package com.heiduo.annotationlibrary;

/**
 * 描述：
 *
 * @author Created by heiduo
 * @time Created on 2020/12/25
 */
public interface ViewInject<T> {
    void inject(T t,Object source);
}
