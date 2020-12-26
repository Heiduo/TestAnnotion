package com.heiduo.annotator;

import com.google.auto.service.AutoService;
import com.heiduo.annotation.ViewById;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
public class IocProcess extends AbstractProcessor {
    /**
     * 生成代码使用
     */
    private Filer mFilerUtils;

    /**
     * 根元素相关的辅助类，获取元素相关信息
     * - VariableElement 一般成员变量
     * - ExecutableElement 一般代表类中的方法
     * - TypeElement 一般代表类
     * - PackageElement 一般代表package
     */
    private Elements mElementUtils;

    /**
     * 跟日志相关的辅助类
     */
    private Messager mMessager;

    private Map<String, ProxyInfo> mProxyMap = new HashMap<String, ProxyInfo>();

    /**
     * 初始化需要使用的工具类
     *
     * @param processingEnv
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFilerUtils = processingEnv.getFiler();
        mElementUtils = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
    }

    /**
     * 设置需要处理的注解
     *
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotationTypes = new HashSet<>();
        annotationTypes.add(ViewById.class.getCanonicalName());
        return annotationTypes;
    }

    /**
     * 设置支持的JAVA版本
     *
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * 最关键的代码生成
     *
     * @param annotations
     * @param roundEnv
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mProxyMap.clear();

        //添加需要处理的注解
        List<Class> classList = new ArrayList<>();
        classList.add(ViewById.class);

        //保存注解
        if (!saveAnnotation(roundEnv, classList)) {
            return false;
        }

        for (String key : mProxyMap.keySet()) {
            ProxyInfo proxyInfo = mProxyMap.get(key);
            //创建一个新的源文件，并写入
            try {
                JavaFileObject jfo = mFilerUtils.createSourceFile(
                        proxyInfo.getProxyClassFullName(),
                        proxyInfo.getTypeElement()
                );
                Writer writer = jfo.openWriter();
                writer.write(proxyInfo.generateJavaCode());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                error(proxyInfo.getTypeElement(),
                        "Unable to write injector for type %s: %s",
                        proxyInfo.getTypeElement(), e.getMessage());
            }
        }
        return false;
    }

    /**
     * 获取并保存需要处理的注解
     *
     * @param roundEnv
     * @param list
     * @return
     */
    private boolean saveAnnotation(RoundEnvironment roundEnv, List<Class> list) {
        for (Class clazz : list) {
            //获取被注解的元素
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(clazz);
            for (Element element : elements) {
                //检查element类型
                if (!checkAnnotationValid(element)) {
                    return false;
                }
                //获取到这个变量的外部类
                TypeElement typeElement = (TypeElement) element.getEnclosingElement();
                //获取外部类的类名
                String qualifiedName = typeElement.getQualifiedName().toString();

                PackageElement packageElement = mElementUtils.getPackageOf(typeElement);
                String packageName = packageElement.getQualifiedName().toString();
                //类的标志ID
                String id = packageName + "." + qualifiedName;

                //以外部类为单位保存
                ProxyInfo proxyInfo = mProxyMap.get(id);
                if (proxyInfo == null) {
                    proxyInfo = new ProxyInfo(mElementUtils, typeElement);
                    mProxyMap.put(id, proxyInfo);
                }
                //把这个注解保存到proxyInfo里面，用于实现功能
                proxyInfo.mElementList.add(element);
            }
        }
        return true;
    }

    /**
     * 检查元素属性是否能获取（非private）
     *
     * @param annotatedElement
     * @return
     */
    private boolean checkAnnotationValid(Element annotatedElement) {
        if (ClassValidator.isPrivate(annotatedElement)) {
            error(annotatedElement, "%s() must can not be private.", annotatedElement.getSimpleName());
            return false;
        }
        return true;
    }

    private void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        mMessager.printMessage(Diagnostic.Kind.NOTE, message, element);
    }
}