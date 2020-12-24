package com.heiduo.annotator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public
/**
 * 描述：
 * @author Created by heiduo
 * @time Created on 2020/12/24
 */
class ProxyInfo {
    /**
     * 包名
     */
    private String packageName;

    /**
     * 生成的类名
     */
    private String proxyClassName;

    /**
     * 外部类
     */
    private TypeElement typeElement;

    /**
     * 保存类里面的所有注解
     */
    public List<Element> mElementList = new ArrayList<>();

    private Set<String> needClass = new HashSet<>();

    public static final String PROXY = "ViewInject";

    public ProxyInfo(Elements elementUtils, TypeElement typeElement) {
        this.typeElement = typeElement;
        PackageElement packageElement = elementUtils.getPackageOf(typeElement);
        String packageName = packageElement.getQualifiedName().toString();
    }
}
