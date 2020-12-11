package com.tufusi.trouter.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import com.tufusi.trouter.annotation.TRouter;
import com.tufusi.trouter.annotation.mode.RouterBean;
import com.tufusi.trouter.compiler.utils.Constants;
import com.tufusi.trouter.compiler.utils.EmptyUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

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
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.swing.plaf.TextUI;
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
@SupportedOptions({Constants.MODULE_NAME, Constants.APT_PACKAGE_NAME})
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

    // 子模块名，如：app/order/personal。需要拼接类名时用到（必传）TRouter$$Group$$order
    private String moduleName;
    // 这里临时全局变量存储组名，防止子模块名与path的截取不一致
    private String defaultGroupName = "";

    // 包名，用于存放APT生成的类文件
    private String packageNameForAPT;

    // 一个组容器，用于存放组名以及组名对应的 TRouter$$Group$$module_name 类名
    private Map<String, String> tempGroupMap = new HashMap<>();

    // 一个路径集合存放容器，用于存放组名，以及组名对应下的类文件数组（List<RouterBean）集合
    private Map<String, List<RouterBean>> tempPathMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
        // 拿取build.gradle里设置的参数
        Map<String, String> options = processingEnv.getOptions();
        if (!EmptyUtils.isEmpty(options)) {
            moduleName = options.get(Constants.MODULE_NAME);
            packageNameForAPT = options.get(Constants.APT_PACKAGE_NAME);

            messager.printMessage(Diagnostic.Kind.NOTE, "--------------- 日志开始 ---------------\r\n");
            messager.printMessage(Diagnostic.Kind.NOTE, "moduleName >>> " + moduleName + "\r\n");
            messager.printMessage(Diagnostic.Kind.NOTE, "aptPackageName >>> " + packageNameForAPT + "\r\n");
        }

        if (EmptyUtils.isEmpty(moduleName) || EmptyUtils.isEmpty(packageNameForAPT)) {
            throw new RuntimeException("The args 'moduleName' or 'packageNameApt' which required by the apt is null, please set in build.gradle");
        }
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
        if (EmptyUtils.isEmpty(elements)) {
            return false;
        }

        try {
            parseElements(elements);
        } catch (IOException e) {
            e.printStackTrace();
        }

        messager.printMessage(Diagnostic.Kind.NOTE, "--------------- 日志结束 ---------------\r\n");
        return true;
    }

    /**
     * 解析所有被 @TRouter 注解的类元素的集合
     *
     * @param elements
     */
    private void parseElements(Set<? extends Element> elements) throws IOException {

        // 通过工具类获取Activity全类名指定的Activity类型
        TypeElement activityType = elementUtils.getTypeElement(Constants.ACTIVITY);
        // 显示类信息，类的镜像信息（也叫作自描述）
        TypeMirror activityMirror = activityType.asType();

        // 遍历节点
        for (Element element : elements) {
            // 获取每个元素类信息
            TypeMirror elementMirror = element.asType();
            messager.printMessage(Diagnostic.Kind.NOTE, "遍历的元素信息有：" + elementMirror.toString() + "\r\n");

            // 拿到注解，获取注解值
            TRouter annotation = element.getAnnotation(TRouter.class);
            // 实例化路径封装对象，并初始化成员属性
            RouterBean routerBean = new RouterBean.Builder()
                    .setGroup(annotation.group())
                    .setPath(annotation.path())
                    .setElement(element)
                    .build();
            // 判断注解是否是注解在指定类上，目前指定类仅支持继承于Activity的类上
            if (typeUtils.isSubtype(elementMirror, activityMirror)) {
                routerBean.setType(RouterBean.Type.ACTIVITY);
            } else {
                throw new RuntimeException("@TRouter注解仅支持注解Activity");
            }

            // 临时容器存储，用来存放路由组Group与其对应的路由路径类对象
            setValueInMap(routerBean);
        }

        // 接下来遍历map，利用JavaPoet自动生成相关类文件
        // 首先需要获取实现定义的接口的类信息
        TypeElement groupLoadType = elementUtils.getTypeElement(Constants.TROUTER_GROUP);
        TypeElement pathLoadType = elementUtils.getTypeElement(Constants.TROUTER_PATH);

        // 先生成路由组Group对应的详细路径组的类文件，如："TRouter$$Path$$app"
        createPathFile(pathLoadType);
        // 再生成路由组Group类文件 如："TRouter$$Group$$app"
        createGroupFile(groupLoadType, pathLoadType);
    }

    /**
     * 生成路由组Group对应详细Path，如：TRouter$$Path$$app
     *
     * @param pathLoadType TRouterLoadPath接口信息
     */
    private void createPathFile(TypeElement pathLoadType) throws IOException {
        if (EmptyUtils.isEmpty(tempPathMap)) {
            return;
        }

        // 设置方法返回类型 Map<String, RouterBean>
        TypeName methodReturns = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouterBean.class));

        // 遍历分组，取出分组中的TRouter$$Path$$module_name类
        Set<Map.Entry<String, List<RouterBean>>> pathListEntry = tempPathMap.entrySet();
        for (Map.Entry<String, List<RouterBean>> entry : pathListEntry) {
            // 配置方法
            // @Override
            // public Map<String, RouterBean> loadPath() {
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constants.METHOD_PATH_LOAD_NAME)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(methodReturns);

            //遍历之前，先实例化容器，不需要在循环里面实例化
            // Map<String, RouterBean> pathMap = new HashMap<>();
            methodBuilder.addStatement("$T<$T, $T> $N = new $T<>()",
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(RouterBean.class),
                    Constants.PARAMETER_PATH_LOAD_NAME,
                    ClassName.get(HashMap.class));

            List<RouterBean> routerBeans = entry.getValue();
            // 方法内遍历，将组内所有的RouterBean往容器中存，methodBuilder继续添加实现代码行
            // 实现：pathMap.put("/app/MainActivity", RouterBean.create(RouterBean.Type.ACTIVITY, MainActivity.class, "/app/MainActivity", "app"));
            for (RouterBean bean : routerBeans) {
                methodBuilder.addStatement(
                        "$N.put($S, $T.create($T.$L, $T.class, $S, $S))",
                        Constants.PARAMETER_PATH_LOAD_NAME,
                        bean.getPath(),
                        ClassName.get(RouterBean.class),
                        ClassName.get(RouterBean.Type.class),
                        bean.getType(),
                        ClassName.get((TypeElement) bean.getElement()),
                        bean.getPath(),
                        bean.getGroup()
                );
            }

            // 遍历之后，返回pathMap
            methodBuilder.addStatement("return $N", Constants.PARAMETER_PATH_LOAD_NAME);

            // 最终生成的类文件名规范 如：TRouter$$Path$$app
            String finalClassName = Constants.PATH_FILE_NAME + entry.getKey();
            messager.printMessage(Diagnostic.Kind.NOTE,
                    "APT生成的路由Path类文件：" + packageNameForAPT + "." + finalClassName + "\r\n");

            // 生成类 public class TRouter$$Path$$app implements TRouterPathLoad
            TypeSpec pathTypeSpec = TypeSpec.classBuilder(finalClassName)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(ClassName.get(pathLoadType))
                    .addMethod(methodBuilder.build())
                    .build();


            // 写入java文件中
            JavaFile.builder(packageNameForAPT, pathTypeSpec).build().writeTo(filer);

            // 生成path类文件，立马赋值路由组，继续生成
            tempGroupMap.put(entry.getKey(), finalClassName);
        }
    }

    /**
     * 生成路由组Group文件 如：TRouter$$Group$$app
     *
     * @param groupLoadType TRouterGroupLoad 接口信息类
     * @param pathLoadType  TRouterPathLoad 接口信息类
     */
    private void createGroupFile(TypeElement groupLoadType, TypeElement pathLoadType) throws IOException {
        if (EmptyUtils.isEmpty(tempGroupMap)) {
            return;
        }

        // Map<String, Class<? extends TRouterPathLoad>>
        TypeName methodReturns = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        // 通配符类型获取 subtypeOf(上边界) supertypeOf(下边界)
                        WildcardTypeName.subtypeOf(ClassName.get(pathLoadType)))
        );

        // 先生成方法
        // @Override
        // public Map<String, Class<? extends TRouterPathLoad>> loadGroup() {
        MethodSpec.Builder loadGroupBuilder = MethodSpec.methodBuilder(Constants.METHOD_GROUP_LOAD_NAME)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(methodReturns);

        // 同样遍历之前先 实例化存储容器：Map<String, Class<? extends TRouterPathLoad>> groupMap = new HashMap<>();
        loadGroupBuilder.addStatement("$T<$T, $T> $N = new $T<>()",
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(pathLoadType))),
                Constants.PARAMETER_GROUP_LOAD_NAME,
                ClassName.get(HashMap.class));

        Set<Map.Entry<String, String>> groupEntry = tempGroupMap.entrySet();
        for (Map.Entry<String, String> entry : groupEntry) {
            // 组名 + 租路径对象
            // groupMap.put("app", TRouter$$Path$$app.class);
            loadGroupBuilder.addStatement("$N.put($S, $T.class)",
                    Constants.PARAMETER_GROUP_LOAD_NAME,
                    entry.getKey(),
                    ClassName.get(packageNameForAPT, entry.getValue()));
        }

        // 返回值  return groupMap;
        loadGroupBuilder.addStatement("return $N", Constants.PARAMETER_GROUP_LOAD_NAME);

        // 整合生成文件
        String finalClassName = Constants.GROUP_FILE_NAME + (!EmptyUtils.isEmpty(defaultGroupName) ? defaultGroupName : moduleName);
        messager.printMessage(Diagnostic.Kind.NOTE,
                "APT生成的路由Group类文件：" + packageNameForAPT + "." + finalClassName + "\r\n");

        TypeSpec groupType = TypeSpec.classBuilder(finalClassName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(groupLoadType))
                .addMethod(loadGroupBuilder.build())
                .build();

        JavaFile.builder(packageNameForAPT, groupType).build().writeTo(filer);
    }

    private void setValueInMap(RouterBean bean) {
        if (validateRouterPath(bean)) {
            // 打印符合规范的注解路径对象
            messager.printMessage(Diagnostic.Kind.NOTE, "RouterBean >>> " + bean.toString() + "\r\n");
            defaultGroupName = bean.getGroup();

            // 根据组名，查询容器中改组名下的所有路由对象数组
            List<RouterBean> routerBeans = tempPathMap.get(bean.getGroup());
            // 存储临时map
            if (!EmptyUtils.isEmpty(routerBeans)) {
                // 如果获取到，则直接往里添加
                routerBeans.add(bean);
            } else {
                // 如果没获取到，是新组，则初始化数组，并加入进来
                routerBeans = new ArrayList<>();
                routerBeans.add(bean);
                tempPathMap.put(bean.getGroup(), routerBeans);
            }
        } else {
            messager.printMessage(Diagnostic.Kind.ERROR, "请按规范配置@TRouter注解，形如：/app/MainActivity\r\n");
        }
    }

    /**
     * 校验路由组名是否合法，如果不合法则截取path，主动设置组名
     * 有关组名，必须强制设置为子模块的模块名，否则无效抛异常
     */
    private boolean validateRouterPath(RouterBean bean) {
        String group = bean.getGroup();
        String path = bean.getPath();

        // @TRouter注解的path必须以 / 开头
        if (EmptyUtils.isEmpty(path) || !path.startsWith("/")) {
            messager.printMessage(Diagnostic.Kind.ERROR, "请按规范配置@TRouter注解中的path值，必须以 / 开头\r\n");
            return false;
        }
        // @TRouter注解的path必须将组名和类名以 / 分隔开，因此肯定大于等于两个 /
        if (path.lastIndexOf("/") == 0) {
            messager.printMessage(Diagnostic.Kind.ERROR, "请按规范配置@TRouter注解中的path值，形如：/app/MainActivity\r\n");
            return false;
        }

        // 截取两端 / 中间的字符串
        String finalGroup = path.substring(1, path.indexOf("/", 1));
        // 如果截取出的组名中仍然包含 / ，也不合法
        if (finalGroup.contains("/")) {
            messager.printMessage(Diagnostic.Kind.ERROR, "请按规范配置@TRouter注解中的path值，形如：/app/MainActivity\r\n");
            return false;
        }

        // 强制要求截取组名必须和子模块名一致，否则不合法
        if (!EmptyUtils.isEmpty(group) && !group.equalsIgnoreCase(moduleName)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "请按规范配置@TRouter注解中的group值，必须和子模块名称一致\r\n");
            return false;
        } else {
            bean.setGroup(finalGroup);
        }

        return true;
    }
}