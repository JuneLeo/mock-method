package com.mock.compiler;

import com.google.auto.service.AutoService;
import com.mock.annotation.MockMethod;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.annotation.processing.FilerException;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * Created by spf on 2018/12/24.
 */
@AutoService(Processor.class)
public class MockProcessor extends AbstractProcessor {


    private Messager mMessager;
    private Filer mFiler;
    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mMessager = processingEnv.getMessager();
        mFiler = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(MockMethod.class);
        if (elements == null || elements.isEmpty()) {
            return false;
        }

        try {
            TypeSpec type = injectMockMethodManager(elements);
            if (type != null) {
                JavaFile.builder("com.mock.generator", type).build().writeTo(mFiler);
            }
        } catch (FilerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            error(e.getMessage());
        }

        return false;
    }

    private TypeSpec injectMockMethodManager(Set<? extends Element> elements) {

        TypeName modelParamterized1 = TypeName.get(String.class);
        ParameterSpec modelSpec1 = ParameterSpec.builder(modelParamterized1, "className").build();

        TypeName modelParamterized2 = TypeName.get(String.class);
        ParameterSpec modelSpec2 = ParameterSpec.builder(modelParamterized2, "methodName").build();

        TypeName modelParamterized4 = TypeName.get(String.class);
        ParameterSpec modelSpec4 = ParameterSpec.builder(modelParamterized4, "values").build();

        TypeName modelParamterized5 = TypeName.get(String.class);
        ParameterSpec modelSpec5 = ParameterSpec.builder(modelParamterized5, "defaultValue").build();


        ParameterizedTypeName hashMapReturnParameter = ParameterizedTypeName.get(ClassName.get(HashMap.class), ClassName.get(String.class), ClassName.get(String.class));

        MethodSpec.Builder transformMethod = MethodSpec.methodBuilder("getMockMap")
                .addModifiers(Modifier.PRIVATE)
                .returns(hashMapReturnParameter)
                .addParameter(modelSpec1)
                .addParameter(modelSpec2)
                .addParameter(modelSpec4)
                .addParameter(modelSpec5);

        transformMethod.addStatement("HashMap<String,String> map = new HashMap<>()");
        transformMethod.addStatement("map.put(\"className\",className)");
        transformMethod.addStatement("map.put(\"methodName\",methodName)");
        transformMethod.addStatement("map.put(\"values\",values)");
        transformMethod.addStatement("map.put(\"defaultValue\",defaultValue)");
        transformMethod.addStatement("return map");


        ParameterizedTypeName hashMapParameterized = ParameterizedTypeName.get(ClassName.get(HashMap.class), ClassName.get(String.class), ClassName.get(String.class));
        ParameterizedTypeName parameter = ParameterizedTypeName.get(ClassName.get(List.class), hashMapParameterized);
        ParameterSpec parameterSpec = ParameterSpec.builder(parameter, "mockMethodModels")
                .build();
        MethodSpec.Builder mockMethod = MethodSpec.methodBuilder("initMackMock")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(parameterSpec);

        for (Element element : elements) {
            String className = element.getEnclosingElement().asType().toString();
            String methodName = element.getSimpleName().toString();
            MockMethod annotation = element.getAnnotation(MockMethod.class);
            String defaultValue = annotation.defaultValue();
            String values = annotation.values();
            mockMethod.addStatement("mockMethodModels.add(getMockMap($S,$S,$S,$S))", className,methodName,values,defaultValue);
        }
        
        TypeElement typeElement = elementUtils.getTypeElement("com.mock.generator.IMockMethodMap");
        return TypeSpec.classBuilder("MockMethodMap")
                .addSuperinterface(ClassName.get(typeElement))
                .addModifiers(Modifier.PUBLIC)
                .addMethod(mockMethod.build())
                .addMethod(transformMethod.build())
                .build();

    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(MockMethod.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void error(String error) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, error);
    }
}
