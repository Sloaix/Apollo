package com.lsxiao.apllo.processor.handler;

import com.lsxiao.apllo.Apollo;
import com.lsxiao.apllo.annotations.Receive;
import com.lsxiao.apllo.contract.ApolloBinder;
import com.lsxiao.apllo.contract.ApolloBinderGenerator;
import com.lsxiao.apllo.entity.ApolloBinderImpl;
import com.lsxiao.apllo.processor.util.StrUtil;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;

import io.reactivex.subscribers.ResourceSubscriber;


/**
 * author lsxiao
 * date 2016-08-07 17:52
 */
public class ReceiveAnnotationHandler extends BaseHandler {
    private Map<DeclaredType, List<ExecutableElement>> mClassMethodMap = new LinkedHashMap<>();
    //如果生成了新的源文件process()能够被调用多次,因为生成的源文件中可能会有注解,它们还将会被ApolloAnnotationProcessor处理。
    //所以这里需要是个是否完成标志变量,避免重复处理注解 创建源文件造成异常
    private boolean handleComplete = false;

    @Override
    public void process(RoundEnvironment roundEnv) {
        if (handleComplete) {
            return;
        }

        //单例变量
        FieldSpec.Builder fieldBuilder = FieldSpec.builder(ApolloBinderGenerator.class, "sInstance", Modifier.PRIVATE, Modifier.STATIC);

        //单例方法
        MethodSpec.Builder instanceMethodBuilder = MethodSpec.methodBuilder("instance")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.SYNCHRONIZED)
                .returns(ApolloBinderGenerator.class)
                .beginControlFlow("if (null == sInstance)")
                .addStatement("sInstance = new ApolloBinderGeneratorImpl()")
                .endControlFlow()
                .addStatement("return sInstance");
        //绑定方法
        MethodSpec.Builder bindMethodBuilder = MethodSpec.methodBuilder("generate")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(ApolloBinder.class)
                .addParameter(Object.class, "object")
                .addStatement("final $T subscriptionBinder = new $T()", ApolloBinderImpl.class, ApolloBinderImpl.class);

        for (Element element : roundEnv.getElementsAnnotatedWith(Receive.class)) {
            if (!isValidElement(element)) {
                return;
            }

            //转行成可执行element
            ExecutableElement receiveMethodElement = (ExecutableElement) element;

            //找到注解所属的类
            TypeElement classElementAnnotationIn = (TypeElement) element.getEnclosingElement();

            //获取类的完全限定名
            final DeclaredType declaredType = getTypeUtil().getDeclaredType(classElementAnnotationIn);

            List<ExecutableElement> methodList = mClassMethodMap.get(declaredType);
            if (methodList == null) {
                methodList = new ArrayList<>();
                mClassMethodMap.put(declaredType, methodList);
            }

            //存储方法
            methodList.add(receiveMethodElement);
        }


        for (DeclaredType classTypeAnnotationIn : mClassMethodMap.keySet()) {
            String receiveMethodInvoker = StrUtil.dot2Underline(classTypeAnnotationIn.toString());
            bindMethodBuilder
                    .beginControlFlow("if(object.getClass().getCanonicalName().equals($S))", classTypeAnnotationIn.toString())
                    .addStatement("final $T $N=($T)object", classTypeAnnotationIn, receiveMethodInvoker, classTypeAnnotationIn);

            final List<ExecutableElement> list = mClassMethodMap.get(classTypeAnnotationIn);

            for (ExecutableElement methodElement : list) {
                note(methodElement.getSimpleName().toString());
                if (!isValidMethod(methodElement, classTypeAnnotationIn)) {
                    return;
                }

                boolean hasParameter = methodElement.getParameters().size() > 0;

                //获取方法第一个变量
                VariableElement eventVariable = hasParameter ? methodElement.getParameters().get(0) : null;

                //获取tag值
                String[] tags = methodElement.getAnnotation(Receive.class).tag();

                Receive.Thread observeOn = methodElement.getAnnotation(Receive.class).observeOn();

                Receive.Thread subscribeOn = methodElement.getAnnotation(Receive.class).subscribeOn();

                //获取receiveMethod是否接收sticky event
                Receive.Type type = methodElement.getAnnotation(Receive.class).type();

                String receiveMethod = methodElement.getSimpleName().toString();
                String onSubscribeMethod = type == Receive.Type.STICKY ? "toFlowableSticky" : "toFlowable";
                String tagsParameter = "new String[]{" + StrUtil.arraySplitBy(tags, ",") + "}";
                String takeOnceMethod = type == Receive.Type.NORMAL_ONCE ? ".take(1)" : "";
                String stickyRemove = type == Receive.Type.STICKY_REMOVE ? "Apollo.get().removeStickyEvent(" + tagsParameter + ");" : type == Receive.Type.STICKY_REMOVE_ALL ? "Apollo.get().removeAllStickyEvents();" : "";
                String eventVariableClassType = eventVariable == null ? "Object.class" : parseBaseType(eventVariable) + ".class";
                String eventVariableClass = eventVariable == null ? "Object" : parseBaseType(eventVariable);
                String eventVariableInstance = eventVariable == null ? "object" : eventVariable.getSimpleName().toString().toLowerCase();

                bindMethodBuilder
                        .addStatement("subscriptionBinder.add(" +
                                        "$T.get().$N($N,$N)" +
                                        takeOnceMethod +
                                        ".subscribeOn($T.get().getSchedulerProvider().get($N.$N)).observeOn($T.get().getSchedulerProvider().get($N.$N)).subscribeWith(" +
                                        "new $T<$N>(){" +
                                        "@Override " +
                                        "public void onComplete(){" +
                                        "}" +
                                        "@Override " +
                                        "public void onNext($N $N){" +
                                        "try {" +
                                        stickyRemove +
                                        "$N.$N($N);" +
                                        "}" +
                                        "catch (Exception e){" +
                                        "e.printStackTrace();" +
                                        "}}" +
                                        "@Override " +
                                        "public void onError($T a){" +
                                        "a.printStackTrace();" +
                                        "}" +
                                        "}))",
                                Apollo.class,
                                onSubscribeMethod,
                                tagsParameter,
                                eventVariableClassType,
                                Apollo.class,
                                Receive.Thread.class.getCanonicalName(),
                                subscribeOn.name(),
                                Apollo.class,
                                Receive.Thread.class.getCanonicalName(),
                                observeOn.name(),
                                ResourceSubscriber.class,
                                eventVariableClass,
                                eventVariableClass,
                                eventVariableInstance,
                                receiveMethodInvoker,
                                receiveMethod,
                                hasParameter ? eventVariableInstance : "",
                                Throwable.class);
            }
            bindMethodBuilder.endControlFlow();
        }
        bindMethodBuilder.addStatement("return subscriptionBinder");

        TypeSpec subscriberClass = TypeSpec.classBuilder("ApolloBinderGeneratorImpl")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ApolloBinderGenerator.class)
                .addField(fieldBuilder.build())
                .addMethod(instanceMethodBuilder.build())
                .addMethod(bindMethodBuilder.build())
                .build();

        generateCode(subscriberClass);
    }

    /**
     * 返回基本数据类型装箱后的类型
     *
     * @param variableElement VariableElement
     * @return String
     */
    private String parseBaseType(VariableElement variableElement) {
        TypeKind typeKind = variableElement.asType().getKind();
        switch (typeKind) {
            case BOOLEAN: {
                return Boolean.class.getSimpleName();
            }
            case BYTE: {
                return Byte.class.getSimpleName();
            }
            case SHORT: {
                return Short.class.getSimpleName();
            }
            case INT: {
                return Integer.class.getSimpleName();
            }
            case LONG: {
                return Long.class.getSimpleName();
            }
            case CHAR: {
                return Character.class.getSimpleName();
            }
            case FLOAT: {
                return Float.class.getSimpleName();
            }
            case DOUBLE: {
                return Double.class.getSimpleName();
            }
            default: {
                if (variableElement.asType() instanceof DeclaredType) {
                    return handleGenericTypeVariable(variableElement);
                }

                return variableElement.asType().toString();
            }
        }
    }

    /**
     * 处理泛型,返回不带泛型的类类型,List<User>,直接返回java.util.List.class而不是java.util.List<User>.class
     *
     * @return String
     */
    private String handleGenericTypeVariable(VariableElement variableElement) {
        DeclaredType declaredType = (DeclaredType) variableElement.asType();
        return ((TypeElement) declaredType.asElement()).getQualifiedName().toString();
    }

    private boolean isValidElement(Element element) {
        //注解最最外侧必须是一个类
        if (element.getEnclosingElement().getKind() != ElementKind.CLASS) {
            //打印出错信息
            error("@Receive must be wrapped by a class");
            return false;
        }

        if (element.getKind() != ElementKind.METHOD) {
            error("@Receive only support method!");
            return false;
        }

        return true;
    }

    //方法是否有合法
    private boolean isValidMethod(ExecutableElement methodElement, DeclaredType classTypeAnnotationIn) {
        //receive方法最多只能有一个参数
        if (methodElement.getParameters().size() > 1) {
            error("the " + methodElement.toString() + " method in " + classTypeAnnotationIn.toString() + "  only support 1 parameter,but there are " + methodElement.getParameters().size());
            return false;
        }

        //不能是抽象方法
        if (methodElement.getModifiers().contains(Modifier.ABSTRACT)) {
            error("the " + methodElement.toString() + " method in " + classTypeAnnotationIn.toString() + " must only be a public method ,not a public abstract method ");
            return false;
        }

        //必须是公有方法
        if (!methodElement.getModifiers().contains(Modifier.PUBLIC)) {
            error("the " + methodElement.toString() + " method in " + classTypeAnnotationIn.toString() + " must only be a public method ");
            return false;
        }

        return true;
    }

    private void generateCode(TypeSpec subscriberClass) {
        JavaFile javaFile = JavaFile.builder("com.lsxiao.apollo.generate", subscriberClass)
                .build();
        try {
            javaFile.writeTo(getFiler());
            handleComplete = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
