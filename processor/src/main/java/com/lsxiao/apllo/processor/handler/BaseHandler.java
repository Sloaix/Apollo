package com.lsxiao.apllo.processor.handler;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * author lsxiao
 * date 2016-08-07 17:43
 */
public abstract class BaseHandler {
    public static Types mTypeUtil;
    public static Elements mElementUtil;
    public static Filer mFiler;
    public static Messager mMessager;

    public static void init(Messager messager, Types typeUtil, Elements elementUtil, Filer filer) {
        mMessager = messager;
        mTypeUtil = typeUtil;
        mElementUtil = elementUtil;
        mFiler = filer;
    }


    public Types getTypeUtil() {
        return mTypeUtil;
    }

    public Elements getElementUtil() {
        return mElementUtil;
    }

    public Filer getFiler() {
        return mFiler;
    }

    public Messager getMessager() {
        return mMessager;
    }


    protected void note(String note) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, note);
    }

    protected void error(String error) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, error);
    }

    public abstract void process(RoundEnvironment roundEnv);
}
