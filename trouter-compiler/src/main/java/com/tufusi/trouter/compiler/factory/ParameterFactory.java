package com.tufusi.trouter.compiler.factory;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.tufusi.trouter.annotation.Parameter;
import com.tufusi.trouter.compiler.utils.Constants;
import com.tufusi.trouter.compiler.utils.EmptyUtils;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * Created by LeoCheung on 2020/12/12.
 *
 * @description 传递参数辅助类生成工厂
 */
public class ParameterFactory {

    private static final String STATEMENT = "$T t = ($T)target";
    private MethodSpec.Builder methodBuilder;
    private Messager messager;
    private ClassName className;

    private ParameterFactory(Builder builder) {
        this.messager = builder.messager;
        this.className = builder.className;

        // 先定义方法
        // @Override
        // public void loadParameter(Object target) {
        methodBuilder = MethodSpec.methodBuilder(Constants.METHOD_PARAMETER_LOAD_NAME)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(builder.parameterSpec);
    }

    /**
     * MessageActivity t = (MessageActivity) target;
     */
    public void addFirstStatement() {
        methodBuilder.addStatement(STATEMENT, className, className);
    }

    /**
     * t.name = t.getIntent().getStringExtra("name");
     *
     * @param element 被注解的属性元素
     */
    public void createStatement(Element element) {
        // 遍历注解属性节点
        TypeMirror typeMirror = element.asType();
        // 获取typeKind 的枚举类型序列号
        int ordinal = typeMirror.getKind().ordinal();
        // 获取属性名
        String fieldName = element.getSimpleName().toString();
        // 获取注解值
        String annotationValue = element.getAnnotation(Parameter.class).name();
        // 判断注解值是否为空，如果为空，则直接用属性名替代
        annotationValue = EmptyUtils.isEmpty(annotationValue) ? fieldName : annotationValue;
        // 表达式等号左边
        String intentValue = "t." + fieldName;
        // 表达式等号右边
        String rightExp = " = t.getIntent().";
        String methodContent = intentValue + rightExp;

        // 根据TypeKind 枚举类型判断获取extra类型
        if (ordinal == TypeKind.INT.ordinal()) {
            // t.s = t.getIntent().getIntExtra("num", t.num);
            methodContent += "getIntExtra($S, " + intentValue + ")";
        } else if (ordinal == TypeKind.BOOLEAN.ordinal()) {
            // t.s = t.getIntent().getBooleanExtra("isSuccess", t.age);
            methodContent += "getBooleanExtra($S, " + intentValue + ")";
        } else {
            // t.s = t.getIntent.getStringExtra("s");
            if (typeMirror.toString().equalsIgnoreCase(Constants.STRING)) {
                methodContent += "getStringExtra($S)";
            }
        }

        // 健壮代码
        if (methodContent.endsWith(")")) {
            // 添加最终拼接方法内容语句
            methodBuilder.addStatement(methodContent, annotationValue);
        } else {
            messager.printMessage(Diagnostic.Kind.ERROR, "目前暂支持String、int、boolean传参");
        }
    }

    public static class Builder {

        private Messager messager;
        private ClassName className;

        private ParameterSpec parameterSpec;

        public Builder(ParameterSpec parameterSpec) {
            this.parameterSpec = parameterSpec;
        }

        public ParameterFactory build() {
            if (parameterSpec == null) {
                throw new IllegalArgumentException("parameterSpec方法参数体为空");
            }

            if (className == null) {
                throw new IllegalArgumentException("方法内容中的className为空");
            }

            if (messager == null) {
                throw new IllegalArgumentException("messager为空，Messager用来报告错误、警告和其他提示信息");
            }
            return new ParameterFactory(this);
        }

        public Builder setMessager(Messager messager) {
            this.messager = messager;
            return this;
        }

        public Builder setClassName(ClassName className) {
            this.className = className;
            return this;
        }
    }


    public MethodSpec build() {
        return methodBuilder.build();
    }
}