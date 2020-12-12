package com.tufusi.trouter.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.tufusi.trouter.annotation.Parameter;
import com.tufusi.trouter.compiler.factory.ParameterFactory;
import com.tufusi.trouter.compiler.utils.Constants;
import com.tufusi.trouter.compiler.utils.EmptyUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Created by LeoCheung on 2020/12/11.
 *
 * @description 该注解生成的类必须在目标activity的同包之下，否则获取不到
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({Constants.PARAMETER_ANNOTATION_TYPES})
public class ParameterProcessor extends AbstractProcessor {

    private Elements elementUtils;
    private Types typeUtils;
    private Messager messager;
    private Filer filer;

    /**
     * 用于临时存储的容器，其中 key = XXActivity.class; value = 被注解标记的传递参数组成的数组
     */
    private Map<TypeElement, List<Element>> tempParameterMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (EmptyUtils.isEmpty(annotations)) {
            return false;
        }

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Parameter.class);
        if (EmptyUtils.isEmpty(elements)) {
            return false;
        }

        try {
            setValueInParameterMap(elements);
            createParameterFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return true;
    }

    private void createParameterFile() throws IOException {
        if (EmptyUtils.isEmpty(tempParameterMap)) {
            return;
        }

        TypeElement activityType = elementUtils.getTypeElement(Constants.ACTIVITY);
        TypeElement parameterLoadType = elementUtils.getTypeElement(Constants.PARAMETER_LOAD);

        // 先定义方法参数
        // Object target
        ParameterSpec parameterSpec = ParameterSpec
                .builder(TypeName.OBJECT, Constants.PARAMETER_PARAMETER_LOAD_NAME).build();

        // 遍历容器
        Set<Map.Entry<TypeElement, List<Element>>> paramEntrySet = tempParameterMap.entrySet();
        for (Map.Entry<TypeElement, List<Element>> entry : paramEntrySet) {
            // 找出集合键值，如：MainActivity.class
            TypeElement typeElement = entry.getKey();
            if (!typeUtils.isSubtype(typeElement.asType(), activityType.asType())) {
                throw new RuntimeException("@Parameter注解目前仅支持作用于Activity类之上");
            }

            // 获取类名
            ClassName className = ClassName.get(typeElement);
            // 方法体构建，新建工厂类处理
            ParameterFactory factory = new ParameterFactory.Builder(parameterSpec)
                    .setClassName(className)
                    .setMessager(messager)
                    .build();

            // 添加方法体内容的第一行
            factory.addFirstStatement();

            // 遍历类的所有属性并生成动态代码
            List<Element> elements = entry.getValue();
            for (Element element : elements) {
                factory.createStatement(element);
            }

            // 最终生成的类文件名（类名$$Parameter）
            String finalClassName = typeElement.getSimpleName() + Constants.PARAMETER_FILE_NAME;
            messager.printMessage(Diagnostic.Kind.NOTE, "APT生成获取参数类文件：" +
                    className.packageName() + "." + finalClassName);

            // MainActivity$$Parameter
            JavaFile.builder(className.packageName(),
                    TypeSpec.classBuilder(finalClassName)
                            .addSuperinterface(ClassName.get(parameterLoadType))
                            .addModifiers(Modifier.PUBLIC)
                            .addMethod(factory.build())
                            .build()) // 类构建完成
                    .build()
                    .writeTo(filer);
        }
    }

    /**
     * 为临时存储容器赋值
     *
     * @param elements 被标记的参数类型
     * @throws IOException 抛出IO异常
     */
    private void setValueInParameterMap(Set<? extends Element> elements) throws IOException {
        for (Element element : elements) {
            // 获取注解的父类结点
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            if (tempParameterMap.containsKey(enclosingElement)) {
                tempParameterMap.get(enclosingElement).add(element);
            } else {
                List<Element> elementList = new ArrayList<>();
                elementList.add(element);
                tempParameterMap.put(enclosingElement, elementList);
            }
        }
    }
}