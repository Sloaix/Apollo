package com.lsxiao.apllo.processor;

import com.google.auto.service.AutoService;
import com.lsxiao.apllo.processor.handler.BaseHandler;
import com.lsxiao.apllo.processor.handler.ReceiveAnnotationHandler;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;


@AutoService(Processor.class)
public class ApolloAnnotationProcessor extends AbstractProcessor {
    private ReceiveAnnotationHandler mReceiveAnnotationHandler;

    //init():初始化操作的方法，RoundEnvironment会提供很多有用的工具类Elements、Types和Filer等。
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Filer mFiler = processingEnv.getFiler();
        Types mTypeUtil = processingEnv.getTypeUtils();
        Elements mElementUtil = processingEnv.getElementUtils();
        Messager mMessager = processingEnv.getMessager();
        BaseHandler.init(mMessager, mTypeUtil, mElementUtil, mFiler);
        mReceiveAnnotationHandler = new ReceiveAnnotationHandler();
    }

    //process()相当于每个处理器的主函数main()。在该方法中去扫描、评估、处理以及生成Java文件。
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        /**
         * TypeElement表示一个类或接口程序元素,在这里annotations里面的TypeElement是用来注解的类。
         * 提供对有关类型及其成员的信息的访问。
         * 注意，枚举类型是一种类，而注释类型是一种接口。
         * 而DeclaredType表示一个类或接口类型，后者将成为前者的一种使用（或调用）。
         * 这种区别对于一般的类型是最明显的，对于这些类型，单个元素可以定义一系列完整的类型。
         * 例如，元素java.util.Set对应于参数化类型java.util.Set<String>和java.util.Set<Number>（以及其他许多类型），还对应于原始类型java.util.Set。
         */
        /**
         * Element表示一个程序元素，比如包、类或者方法。每个元素都表示一个静态的语言级构造（不表示虚拟机的运行时构造）。
         * 元素应该使用equals(Object)方法进行比较。不保证总是使用相同的对象表示某个特定的元素。
         * 要实现基于Element对象类的操作，可以使用visitor或者使用getKind()方法的结果。
         * 使用instanceof确定此建模层次结构中某一对象的有效类未必可靠，因为一个实现可以选择让单个对象实现多个Element子接口。
         */
        mReceiveAnnotationHandler.process(roundEnv);
        return true;
    }

    /**
     * 指定该注解处理器需要处理的注解类型
     *
     * @return 需要处理的注解类型名的集合Set<String>
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(com.lsxiao.apllo.annotations.Receive.class.getCanonicalName());
        return types;
    }

    /**
     * 指定使用的java版本。通常这里会直接放回SourceVersion.latestSupported()即可。
     *
     * @return SourceVersion
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
