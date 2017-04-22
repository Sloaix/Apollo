package com.lsxiao.apllo.processor

import com.apollo.core.Apollo
import com.apollo.core.contract.ApolloBinder
import com.apollo.core.contract.ApolloBinderGenerator
import com.apollo.core.entity.ApolloBinderImpl
import com.apollo.core.entity.SchedulerProvider
import com.squareup.javapoet.*
import io.reactivex.BackpressureStrategy
import io.reactivex.subscribers.DisposableSubscriber
import java.io.IOException
import java.util.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier
import javax.lang.model.element.VariableElement


/**
 * write with Apollo
 * author:lsxiao
 * date:2017-04-22 18:00
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

internal class CodeGenerator private constructor(private val apolloDescriptors: ArrayList<ApolloDescriptor>, private val mFiler: Filer) {

    companion object {
        private val GENERATE_PACKAGE_NAME = "com.lsxiao.apollo.generate"
        private val GENERATE_CLASS_NAME = "ApolloBinderGeneratorImpl"
        private val SINGLE_INSTANCE_PARAM_NAME = "sInstance"
        private val SINGLE_INSTANCE_METHOD_NAME = "instance"
        private val SUBSCRIBER_BINDER_LOCAL_PARAM_NAME = "apolloBinder"
        private val GENERATE_METHOD_BIND_OBJECT_NAME = "bindObject"
        private val SUBSCRIBER_LOCAL_NAME = "subscriber"
        private val TO_FLOWABLE_STICKY_METHOD_NAME = "toFlowableSticky"
        private val TO_FLOWABLE_METHOD_NAME = "toFlowable"


        fun create(apolloDescriptors: ArrayList<ApolloDescriptor>, filer: Filer): CodeGenerator = CodeGenerator(apolloDescriptors, filer)
    }

    fun generate() = createJavaFile()

    private fun createJavaFile() = try {
        getBinderGeneratorJavaFile().writeTo(mFiler)
    } catch (e: IOException) {
        e.printStackTrace()
    }

    fun getBinderGeneratorJavaFile(): JavaFile = JavaFile
            .builder(GENERATE_PACKAGE_NAME, getGeneratorTypeSpec())
            .addStaticImport(SchedulerProvider.Tag.MAIN)
            .addStaticImport(SchedulerProvider.Tag.IO)
            .addStaticImport(SchedulerProvider.Tag.COMPUTATION)
            .addStaticImport(SchedulerProvider.Tag.NEW)
            .addStaticImport(SchedulerProvider.Tag.SINGLE)
            .addStaticImport(SchedulerProvider.Tag.TRAMPOLINE)
            .build()

    /**
     *   public final class ApolloBinderGeneratorImpl implements ApolloBinderGenerator {
     *      ...
     *  }
     */
    fun getGeneratorTypeSpec(): TypeSpec = TypeSpec
            .classBuilder(GENERATE_CLASS_NAME)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addSuperinterface(ApolloBinderGenerator::class.java)
            .addField(getSingleInstanceFileSpec())
            .addMethod(getSingleInstanceMethodSpec())
            .addMethod(getGenerateFunctionMethodSpec())
            .build()

    /**
     *   private static ApolloBinderGenerator sInstance;
     */
    fun getSingleInstanceFileSpec(): FieldSpec = FieldSpec
            .builder(ApolloBinderGenerator::class.java, SINGLE_INSTANCE_PARAM_NAME, Modifier.PRIVATE, Modifier.STATIC)
            .build()

    /**
     *  public static synchronized ApolloBinderGenerator instance() {
     *    if (null == sInstance) {
     *        sInstance = new ApolloBinderGeneratorImpl();
     *    }
     *    return sInstance;
     *  }
     */
    fun getSingleInstanceMethodSpec(): MethodSpec = MethodSpec.methodBuilder(SINGLE_INSTANCE_METHOD_NAME)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.SYNCHRONIZED)
            .returns(ApolloBinderGenerator::class.java)
            .beginControlFlow("if (null == $SINGLE_INSTANCE_PARAM_NAME)")
            .addStatement("$SINGLE_INSTANCE_PARAM_NAME = new $GENERATE_CLASS_NAME()")
            .endControlFlow()
            .addStatement("return $SINGLE_INSTANCE_PARAM_NAME")
            .build()

    /**
     *  @Override
     *  public ApolloBinder generate(Object object) {
     *      final ApolloBinderImpl apolloBinder = new ApolloBinderImpl();
     *      ...
     *      return apolloBinder;
     *  }
     */
    fun getGenerateFunctionMethodSpec(): MethodSpec {
        val builder = MethodSpec.methodBuilder("generate")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override::class.java)
                .returns(ApolloBinder::class.java)
                .addParameter(Any::class.java, GENERATE_METHOD_BIND_OBJECT_NAME, Modifier.FINAL)
                .addStatement("final \$T $SUBSCRIBER_BINDER_LOCAL_PARAM_NAME = new \$T()", ApolloBinderImpl::class.java, ApolloBinderImpl::class.java)

        apolloDescriptors.forEach {
            getSingleBinderStatement(builder, it)
        }

        return builder.addStatement("return $SUBSCRIBER_BINDER_LOCAL_PARAM_NAME").build()
    }

    /**
     *  if (object.getClass().getCanonicalName().equals(...)) {
     *      apolloBinder.add(Apollo.get().toFlowable(new String[]{...}).subscribeOn(Apollo.get().getSchedulerProvider().get(...)).observeOn(...).subscribeWith(...))
     *  }
     */
    fun getSingleBinderStatement(builder: MethodSpec.Builder, descriptor: ApolloDescriptor) {
        builder.beginControlFlow("if($GENERATE_METHOD_BIND_OBJECT_NAME.getClass().getCanonicalName().equals(\"${descriptor.methodElement.enclosingElement.asType()}\"))")
                .addStatement("$SUBSCRIBER_BINDER_LOCAL_PARAM_NAME.add(" +
                        getApollo() +
                        getToFlowableCode(descriptor) +
                        getBackpressure(descriptor) +
                        getSubscribeOnMethodCode(descriptor) +
                        getObserveOnMethodCode(descriptor) +
                        getSubscribeWithCode(descriptor) +
                        ")",
                        Apollo::class.java
                )
                .endControlFlow()
    }

    /**
     *  .toFlowable(new String[...tags])
     */
    fun getToFlowableCode(descriptor: ApolloDescriptor): CodeBlock {
        val toFlowable: String = if (descriptor.isSticky) {
            TO_FLOWABLE_STICKY_METHOD_NAME
        } else {
            TO_FLOWABLE_METHOD_NAME
        }
        return CodeBlock.of(".$toFlowable(new String[]{${Utils.arraySplitBy(descriptor.tags, ",")}})")
    }

    /**
     *  .onBackpressureBuffer()
     *  .onBackpressureDrop()
     *  .
     */
    fun getBackpressure(descriptor: ApolloDescriptor): CodeBlock {
        val onBackpressure: String = when (descriptor.backpressureStrategy) {
            BackpressureStrategy.BUFFER -> ".onBackpressureBuffer()"
            BackpressureStrategy.DROP -> ".onBackpressureDrop()"
            BackpressureStrategy.LATEST -> ".onBackpressureLatest()"
            else -> {
                ""
            }
        }
        return CodeBlock.of(onBackpressure)
    }

    /**
     *  Apollo.get().getSchedulerProvider().get(descriptor.subscribeOn)
     */
    fun getSubscribeOnMethodCode(descriptor: ApolloDescriptor): CodeBlock = CodeBlock.of(".subscribeOn(${getSchedulerCode(descriptor.subscribeOn)})")

    /**
     *  Apollo.get().getSchedulerProvider().get(descriptor.observeOn)
     */
    fun getObserveOnMethodCode(descriptor: ApolloDescriptor): CodeBlock = CodeBlock.of(".observeOn(${getSchedulerCode(descriptor.observeOn)})")

    /**
     *  Apollo.get().getSchedulerProvider().get(...)
     */
    fun getSchedulerCode(tag: SchedulerProvider.Tag): CodeBlock = CodeBlock.of("${getApollo()}.getSchedulerProvider().get(${tag.name})")

    /**
     *  Apollo.get()
     */
    fun getApollo(): CodeBlock = CodeBlock.of("\$T.get()", Apollo::class.java)

    /**
     *  .subscribeWith(new DisposableSubscriber<Object>(){
     *      ...
     *  }),
     */
    fun getSubscribeWithCode(descriptor: ApolloDescriptor): CodeBlock = CodeBlock.of(
            ".subscribeWith(new \$T<Object>(){" +
                    getOnCompleteMethodCode() +
                    getOnErrorMethodCode() +
                    getOnNextMethodCode(descriptor) +
                    "})",
            DisposableSubscriber::class.java
    )

    /**
     *  @java.lang.Override
     *  public void onComplete(Object o) {
     *      ...
     *  }
     */
    fun getOnCompleteMethodCode(): MethodSpec = MethodSpec.methodBuilder("onComplete")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override::class.java)
            .build()

    /**
     *  @java.lang.Override
     *  public void onError(java.lang.Throwable t) {
     *      ...
     *  }
     */
    fun getOnErrorMethodCode(): MethodSpec = MethodSpec.methodBuilder("onError")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override::class.java)
            .addParameter(Throwable::class.java, "t")
            .build()

    /**
     *  @java.lang.Override
     *  public void onNext(Object o) {
     *      ...
     *  }
     */
    fun getOnNextMethodCode(descriptor: ApolloDescriptor): MethodSpec = MethodSpec.methodBuilder("onNext")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override::class.java)
            .addParameter(Object::class.java, "o")
            .addCode(getReceiveMethodInvokeCode(descriptor).toString())
            .build()

    fun getReceiveMethodInvokeCode(descriptor: ApolloDescriptor): CodeBlock {
        val parameter = descriptor.methodElement.parameters.map(VariableElement::asType).first()
        val builder = CodeBlock
                .builder()
                .addStatement("final \$T $SUBSCRIBER_LOCAL_NAME=(\$T)$GENERATE_METHOD_BIND_OBJECT_NAME",
                        descriptor.methodElement.enclosingElement.asType(),
                        descriptor.methodElement.enclosingElement.asType()
                )
                .addStatement("$SUBSCRIBER_LOCAL_NAME.${descriptor.methodElement.simpleName}((\$T)o)", parameter)
        return builder.build()
    }
}
