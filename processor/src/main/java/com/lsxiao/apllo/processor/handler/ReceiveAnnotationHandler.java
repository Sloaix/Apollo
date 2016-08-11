package com.lsxiao.apllo.processor.handler;

import com.lsxiao.apllo.Apollo;
import com.lsxiao.apllo.annotations.Receive;
import com.lsxiao.apllo.entity.SubscriptionBinder;
import com.lsxiao.apllo.processor.util.StrUtil;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

import rx.functions.Action1;

/**
 * author lsxiao
 * date 2016-08-07 17:52
 */
public class ReceiveAnnotationHandler extends BaseHandler {
    private Map<DeclaredType, List<ExecutableElement>> mClassMethodMap = new HashMap<>();
    //如果生成了新的源文件process()能够被调用多次,因为生成的源文件中可能会有注解,它们还将会被ApolloAnnotationProcessor处理。
    //所以这里需要是个是否完成标志变量,避免重复处理注解 创建源文件造成异常
    private boolean handleComplete = false;

    @Override
    public void process(RoundEnvironment roundEnv) {
        if (handleComplete) {
            return;
        }

        //单例变量
        FieldSpec.Builder fieldBuilder = FieldSpec.builder(Apollo.SubscriberBinder.class, "sInstance", Modifier.PRIVATE, Modifier.STATIC);

        //单例方法
        MethodSpec.Builder instanceMethodBuilder = MethodSpec.methodBuilder("instance")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.SYNCHRONIZED)
                .returns(Apollo.SubscriberBinder.class)
                .beginControlFlow("if (null == sInstance)")
                .addStatement("sInstance = new SubscriberBinderImplement()")
                .endControlFlow()
                .addStatement("return sInstance");
        //绑定方法
        MethodSpec.Builder bindMethodBuilder = MethodSpec.methodBuilder("bind")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(SubscriptionBinder.class)
                .addParameter(Object.class, "object")
                .addStatement("final $T subscriptionBinder = new $T()", SubscriptionBinder.class, SubscriptionBinder.class);

        for (Element element : roundEnv.getElementsAnnotatedWith(Receive.class)) {

            //注解最最外侧必须是一个类
            if (element.getEnclosingElement().getKind() != ElementKind.CLASS) {
                //打印出错信息
                error("@Receive must be wrapped by a class");
            }

            if (element.getKind() != ElementKind.METHOD) {
                error("@Receive only support method!");
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
            for (ExecutableElement methodElement : mClassMethodMap.get(classTypeAnnotationIn)) {

                //receive方法最多只能有一个参数
                if (methodElement.getParameters().size() > 1) {
                    error("the " + methodElement.toString() + " method in " + classTypeAnnotationIn.toString() + "  only support 1 parameter,but there are " + methodElement.getParameters().size());
                }

                boolean hasParameter = methodElement.getParameters().size() > 0;

                //获取方法第一个变量
                VariableElement eventVariable = hasParameter ? methodElement.getParameters().get(0) : null;

                //获取tag值
                String[] tags = methodElement.getAnnotation(Receive.class).tag();

                Receive.Thread observeOn = methodElement.getAnnotation(Receive.class).observeOn();

                Receive.Thread subscribeOn = methodElement.getAnnotation(Receive.class).subscribeOn();

                //获取receiveMethod是否接收sticky event
                boolean isSticky = methodElement.getAnnotation(Receive.class).type() == Receive.Type.STICKY;

                String receiveMethod = methodElement.getSimpleName().toString();
                String onSubscribeMethod = isSticky ? "toObservableSticky" : "toObservable";
                String eventVariableClassType = eventVariable == null ? "Object.class" : eventVariable.asType().toString() + ".class";
                String eventVariableClass = eventVariable == null ? "Object" : eventVariable.asType().toString();
                String eventVariableInstance = eventVariable == null ? "object" : eventVariable.getSimpleName().toString().toLowerCase();
                String tagsParameter = "new String[]{" + StrUtil.arraySplitBy(tags, ",") + "}";

                bindMethodBuilder
                        .addStatement("subscriptionBinder.add($T.get().$N($N,$N).subscribeOn($T.get().getThread().get($N.$N)).observeOn($T.get().getThread().get($N.$N)).subscribe(" +
                                        "new $T<$N>(){" +
                                        "@Override " +
                                        "public void call($N $N){" +
                                        "try {" +
                                        "$N.$N($N);" +
                                        "}" +
                                        "catch (Exception e){" +
                                        "e.printStackTrace();" +
                                        "}}}," +
                                        "new $T<$T>(){" +
                                        "@Override " +
                                        "public void call($T a){" +
                                        "a.printStackTrace();" +
                                        "}}" +
                                        "))",
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
                                Action1.class,
                                eventVariableClass,
                                eventVariableClass,
                                eventVariableInstance,
                                receiveMethodInvoker,
                                receiveMethod,
                                hasParameter ? eventVariableInstance : "",
                                Action1.class,
                                Throwable.class,
                                Throwable.class);
            }
            bindMethodBuilder.endControlFlow();
        }
        bindMethodBuilder.addStatement("return subscriptionBinder");

        TypeSpec subscriberClass = TypeSpec.classBuilder("SubscriberBinderImplement")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(Apollo.SubscriberBinder.class)
                .addField(fieldBuilder.build())
                .addMethod(instanceMethodBuilder.build())
                .addMethod(bindMethodBuilder.build())
                .build();

        generateCode(subscriberClass);
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
