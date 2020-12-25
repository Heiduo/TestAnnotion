package com.heiduo.annotator;

import com.heiduo.annotation.ViewById;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.swing.text.View;

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
        String className = ClassValidator.getClassName(typeElement, packageName);
        this.packageName = packageName;
        this.proxyClassName = className + "$$" + PROXY;
    }

    /**
     * 生成代码
     */
    public String generateJavaCode() {
        //参数

        //方法
        //注入activity
        MethodSpec inject = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addAnnotation(Override.class)
                .addParameter(ClassName.get(typeElement), "host")
                .addParameter(Object.class, "source")
                .addStatement("initViewById($L,$L) ", "host", "source")
                .build();

        //生成 initViewById 方法
        MethodSpec.Builder initViewByIdBuild = MethodSpec.methodBuilder("initViewById")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(ClassName.get(typeElement), "host")
                .addParameter(Object.class, "source");

        Iterator<Element> iterator = mElementList.iterator();
        while (iterator.hasNext()) {
            Element element = iterator.next();
            ViewById annotation = element.getAnnotation(ViewById.class);
            if (annotation != null) {
                VariableElement variableElement = (VariableElement) element;
//                initViewByIdBuild.addCode(generateViewById(variableElement));
                generateViewById(initViewByIdBuild, variableElement);
                iterator.remove();
            }
        }

        //类
        TypeSpec activityProxy = TypeSpec.classBuilder(proxyClassName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get("com.heiduo.annotationlibrary",ProxyInfo.PROXY
                        +"<" + typeElement.getQualifiedName() + ">"))
                .addMethod(inject)
                .addMethod(initViewByIdBuild.build())
                .build();

        //包
        JavaFile javaFile = JavaFile.builder(packageName, activityProxy)
//                .addStaticImport(ClassName.get("com.heiduo.annotationlibrary", "annotationlibrary"), "*")
//                .addStaticImport(ClassName.get("com.heiduo.annotation", "annotation"), "*")
//                .addStaticImport(ClassName.get(packageName, "R"), "R")
                .build();

        return javaFile.toString();
        /*try {
            Writer writer = javaFile.toJavaFileObject().openWriter();
            writer.write(javaFile.toString());
            writer.flush();
            writer.close();
//            javaFile.writeTo(System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        /*try {
            javaFile.writeTo(System.out);
//            javaFile.writeTo();
//            javaFile.writeTo();
//            javaFile.writeTo(new File(getProxyClassFullName()));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * 绑定View
     *
     * @param variableElement
     * @return
     */
    private String generateViewById(VariableElement variableElement) {
        //获取注释值
        int id = variableElement.getAnnotation(ViewById.class).value();
        //获取变量类型
        String type = variableElement.asType().toString();
        //获取变量名字
        String name = variableElement.getSimpleName().toString();

        StringBuilder builder = new StringBuilder();

        builder.append(" if(source instanceof android.app.Activity) { \n");
        builder.append(" host." + name).append(" = ");
        if (id == -1) {
            builder.append("(" + type + ")(((android.app.Activity)source).findViewById( " + "R.id." + name + "));\n");
        } else {
            builder.append("(" + type + ")(((android.app.Activity)source).findViewById( " + id + "));\n");
        }

        builder.append("\n}else{\n");

        builder.append("host." + name).append(" = ");
        if (id == -1) {
            builder.append("(" + type + ")(((android.view.View)source).findViewById( " + "R.id." + name + "));\n");
        } else {
            builder.append("(" + type + ")(((android.view.View)source).findViewById( " + id + "));\n");
        }
        builder.append("}\n");

        return builder.toString();
    }

    private void generateViewById(MethodSpec.Builder builder, VariableElement variableElement) {
        //获取注释值
        int id = variableElement.getAnnotation(ViewById.class).value();
        //获取变量类型
        String type = variableElement.asType().toString();
        //获取变量名字
        String name = variableElement.getSimpleName().toString();

        TypeName clazz = ClassName.get(variableElement.asType());
        if (id == -1) {
            builder.beginControlFlow("if ($L instanceof android.app.Activity)", "source")
                    .addStatement(" $L.$L = ($T)(((android.app.Activity)source).findViewById( " + "R.id.$L))","host", name,clazz,name)
                    .nextControlFlow("else ")
                    .addStatement(" $L.$L = ($T)(((android.view.View)source).findViewById( " + "R.id.$L))","host",name, clazz,name)
                    .endControlFlow();
        } else {
            builder.beginControlFlow("if ($L instanceof android.app.Activity)", "source")
                    .addStatement(" $L.$L = ($T)(((android.app.Activity)source).findViewById( $L))","host",name, clazz,id)
                    .nextControlFlow("else ")
                    .addStatement(" $L.$L = ($T)(((android.view.View)source).findViewById( $L))","host",name, clazz,id)
                    .endControlFlow();
        }
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }

    /**
     * 获取全名
     *
     * @return
     */
    public String getProxyClassFullName() {
        return packageName + "." + proxyClassName;
    }

    /**
     * 获取包名
     *
     * @param packageName
     * @return
     */
    private String getLibraryPath(String packageName) {
        try {
            return packageName.substring(0, ordinalIndexOf(packageName, ".", 3));
        } catch (Exception e) {
            return packageName;
        }
    }

    private int ordinalIndexOf(String str, String substr, int n) {
        int pos = str.indexOf(substr);
        while (--n > 0 && pos != -1) {
            pos = str.indexOf(substr, pos + 1);
        }
        return pos;
    }
}
