package com.mock.internel

import com.mock.annotation.MockMethod
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import org.gradle.api.Project

import java.lang.reflect.Modifier

class Inject {
    private static ClassPool pool = ClassPool.getDefault()


    static void injectJarPath(String path){
        //注入依赖与generator中的类，所以注入jar path
        println("--------jar-path----:" + path)
        pool.appendClassPath(path)
    }

    //class期，不可以使用
    static void injectDir(String path, Project project) {
        println("--------path----:" + path)
        ///Users/bighero/demo/Mock/mock-method-generator/build/classes
//        println '--------MockManagerPath----:'+project.rootProject.rootDir.absolutePath +'/mock-method-generator/build/classes/java/main'
//        pool.appendClassPath(project.rootProject.rootDir.absolutePath +'/mock-method-generator/build/classes/java/main')
        pool.appendClassPath(path)
//        fakeMockManager(path)
        File dir = new File(path)
        if (dir.isDirectory()) {
            dir.eachFileRecurse { File file ->
                String filePath = file.absolutePath
                if (filePath.endsWith(".class")) {
                    String className = filePath.replace('\\', '.').replace('/', '.')
                    if (!className.contains('R$')) {
                        MockExtension printExtension = project.extensions.mock
                        printExtension.packages.each { String pkg ->
                            if (className.contains(pkg)) {
                                //
                                int start = className.indexOf(pkg)
                                String clzName = className.substring(start, className.length() - 6)
                                println 'clzName:' + clzName
                                injectClass(path, clzName)
                            }
                        }
                    }
                }

            }
        }
    }

    private static void injectClass(String path, String className) {
        CtClass ctClass = pool.get(className)
        CtMethod[] methods = ctClass.getMethods()
        if (methods == null || methods.length == 0) {
            return
        }
        methods.each { CtMethod method ->
            MockMethod mockMethod = method.getAnnotation(MockMethod.class)
            if (mockMethod == null) {
                return
            }
            println("methodDes:" + getMethodDes(className, method.name, method.returnType.name))

            method.insertBefore(getMethodDes(className, method.name, method.returnType.name))

        }
        ctClass.writeFile(path)
        ctClass.detach()
    }

    private static String getMethodDes(String className, String methodName, String returnType) {
        return String.format("if (com.mock.generator.MockManager.isMock(\"%s\", \"%s\")) {\n" +
                "            String value = com.mock.generator.MockManager.getValue(\"%s\", \"%s\");\n" +
                "            return " + getReturnValueByStr(returnType) + ";\n" +
                "        }", className, methodName, className, methodName)
    }

    private static String getReturnValueByStr(String returnType) {
        if (returnType.contains("String")) {
            return 'value'
        } else if (returnType.contains("int")) {
            return 'Integer.parseInt(value)'
        } else if (returnType.contains("long")) {
            return 'Long.parseLong(value)'
        } else if (returnType.contains("double")) {
            return 'Double.parseDouble(value)'
        } else if (returnType.contains("float")) {
            return 'Float.parseFloat(value)'
        } else if (returnType.contains("boolean")) {
            return 'Boolean.parseBoolean(value)'
        } else if (returnType.contains("short")) {
            return 'Short.parseShort(value)'
        } else if (returnType.contains("Integer")) {
            return 'Integer.valueOf(value)'
        } else if (returnType.contains("Long")) {
            return 'Long.valueOf(value)'
        } else if (returnType.contains("Double")) {
            return 'Double.valueOf(value)'
        } else if (returnType.contains("Float")) {
            return 'Float.valueOf(value)'
        } else if (returnType.contains("Boolean")) {
            return 'Boolean.valueOf(value)'
        } else if (returnType.contains("Short")) {
            return 'Short.valueOf(value)'
        }
    }
    /**
     * 伪造一个类，骗过编译器
     * @param path
     */
    private static void fakeMockManager(String path) {
        CtClass ctClass = pool.makeClass('com.mock.generator.MockManager')
        CtClass stringCtClass = pool.getCtClass("java.lang.String")
        CtMethod ctMethod = new CtMethod(CtClass.booleanType, 'isMock', new CtClass[2] {
            stringCtClass; stringCtClass
        }, ctClass)
        ctMethod.setModifiers(Modifier.PUBLIC | Modifier.STATIC)
        ctMethod.setBody("return false")
        ctClass.addMethod(ctMethod)

        CtMethod ctMethod2 = new CtMethod(stringCtClass, 'getValue', new CtClass[2] {
            stringCtClass; stringCtClass
        }, ctClass)
        ctMethod2.setModifiers(Modifier.PUBLIC | Modifier.STATIC)
        ctMethod2.setBody("return false")
        ctClass.addMethod(ctMethod2)
        ctClass.writeFile(path)
        ctClass.detach()
    }

//
}
