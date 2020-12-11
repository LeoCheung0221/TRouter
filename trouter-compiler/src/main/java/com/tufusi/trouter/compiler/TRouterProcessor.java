package com.tufusi.trouter.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.tufusi.trouter.annotation.TRouter;
import com.tufusi.trouter.annotation.utils.Constants;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Created by LeoCheung on 2020/12/10.
 *
 * @description 路由注解处理器
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes({Constants.TROUTER_ANNOTATION_TYPES})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
// 注解处理器接收的参数
@SupportedOptions({"moduleName", "packageNameForAPT"})
public class TRouterProcessor extends AbstractProcessor {

    /**
     * 操作 Element 工具类（类、函数、属性都是 Element）
     */
    private Elements elementUtils;
    /**
     * 操作类信息 Type 工具类（操作TypeMirror的工具方法）
     */
    private Types typeUtils;
    /**
     * 日志消息工具类
     */
    private Messager messager;
    /**
     * 文件生成器，用来创建新的源文件，class文件以及辅助文件等等
     */
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
    }

    /**
     * 注解处理器入口方法
     *
     * @param annotations 使用了支持处理注解的节点集合
     * @param roundEnv    整个回合运行环境对象，可以通过该对象查找找到的注解对象集合
     * @return true 表示后续处理器不会再处理（已经处理完成）
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        }
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(TRouter.class);
        // 遍历所有被注解过的类节点
        for (Element element : elements) {
            // 通过类节点找出其包节点 （全路径包名）
            String packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
            // 获取简单类名
            String className = element.getSimpleName().toString();
            messager.printMessage(Diagnostic.Kind.NOTE, "被@TRouter注解的类有：" + className);
            // 合成最终想要生成的类文件名
            String finalClassName = className + "$$TRouter";

            // 通过JavaPoet动态构建类对象文件
            try {
                // 获取被注解的path值
                TRouter tRouter = element.getAnnotation(TRouter.class);

                // 构建方法体
                MethodSpec methodSpec = MethodSpec.methodBuilder("findTargetClass")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(Class.class)
                        .addParameter(String.class, "path")
                        // 方法内容拼接：
                        // return path.equalsIgnoreCase("/app/MainActivity") ? MainActivity.class : null
                        .addStatement("return path.equalsIgnoreCase($S) ? $T.class : null",
                                tRouter.path(),
                                ClassName.get((TypeElement) element)
                        )
                        .build();

                // 类内容拼接：
                // public class MainActivity$$ARouter {
                TypeSpec typeSpec = TypeSpec.classBuilder(finalClassName)
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(methodSpec)
                        .build();

                // 生成Java类文件
                JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();
                javaFile.writeTo(filer);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return true;
    }
}