package com.heiduo.annotator;

import com.google.auto.service.AutoService;
import com.heiduo.annotation.ViewById;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

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
        return false;
    }

    /**
     * 检查元素属性是否能获取（非private）
     *
     * @param annotatedElement
     * @return
     */
    private boolean checkAnnotationValid(Element annotatedElement) {

        return true;
    }

    private void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        mMessager.printMessage(Diagnostic.Kind.NOTE, message, element);
    }
}