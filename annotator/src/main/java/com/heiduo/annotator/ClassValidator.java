package com.heiduo.annotator;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public
/**
 * 描述：
 * @author Created by heiduo
 * @time Created on 2020/12/24
 */
final class ClassValidator {
    static boolean isPrivate(Element annotatedClass){
        return annotatedClass.getModifiers().contains(Modifier.PRIVATE);
    }

    static String getClassName(TypeElement type,String packageName){
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen)
                .replace(".","$");
    }
}
