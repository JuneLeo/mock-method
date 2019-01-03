package com.mock.internel

import com.mock.annotation.MockMethod
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import org.gradle.api.Project


class Inject {
    private static ClassPool pool = ClassPool.getDefault()
    private static List<MockMethodModel> generatorMockMap = new ArrayList<>();
    static void addPoolPath(String path) {
        pool.appendClassPath(path)
    }


    static void generatorMockMap(String path, Project project){
        File jarFile = new File(path)
        String jarZipDir = JarUtils.getExtractJarPath(jarFile)
        File unJar = new File(jarZipDir)
        List<String> strings = JarUtils.unJar(jarFile, unJar)
        addPoolPath(unJar.path)
        //todo inject代码
        if (strings.contains('com.mock.generator.MockMethodGenerator.class')){
            CtClass ctClass = pool.getCtClass('com.mock.generator.MockMethodGenerator')
            try {
            CtMethod ctMethod = ctClass.getDeclaredMethod('initMackMock')

            StringBuilder builder = new StringBuilder()
            generatorMockMap.each {
                builder.append(getMockMapDes(it.className,it.methodName,it.values,it.defaultValue)+"\n")
            }
            println '方法' + builder.toString()
            ctMethod.insertBefore(builder.toString())
            ctClass.writeFile(unJar.path)
            ctClass.detach()
            }catch (Exception e){
                e.printStackTrace()
            }
        }
        println '解压路径:' + new File(jarZipDir).path
        println '压缩路径:' + new File(path).path
        jarFile.delete()
        JarUtils.jar(jarFile, unJar)
    }

    private static String getMockMapDes(String className, String methodName, String values,String defaultValue) {
        return String.format("mockMethodModels.add(getMockMap(\"%s\",\"%s\",\"%s\",\"%s\"));", className, methodName, values, defaultValue)
    }

    static void  injectMockMap(String className,String methodName,String values,String defaultValue){
        MockMethodModel mockMethodModel= new MockMethodModel()
        mockMethodModel.values = values
        mockMethodModel.defaultValue = defaultValue
        mockMethodModel.methodName = methodName
        mockMethodModel.className = className
        generatorMockMap.add(mockMethodModel)
    }


    static void injectJarPath(String path, Project project) {
        //注入依赖与generator中的类，所以注入jar path
        project.extensions[MockExtension.plugin].subprojects.each {
            if (path.contains(it)) {
                File jarFile = new File(path)
                String jarZipDir = JarUtils.getExtractJarPath(jarFile)
                File unJar = new File(jarZipDir)
                List<String> strings = JarUtils.unJar(jarFile, unJar)
                injectDir(unJar.path, project)
                println '解压路径:' + new File(jarZipDir).path
                println '压缩路径:' + new File(path).path
                jarFile.delete()
                JarUtils.jar(jarFile, unJar)
            }
        }

    }


    static void injectDir(String path, Project project) {
        println("--------path----:" + path)
        addPoolPath(path)
        processorDir(path, project)
    }

    private static void processorDir(String path, Project project) {
        File dir = new File(path)
        if (dir.isDirectory()) {
            dir.eachFileRecurse { File file ->
                String classPath = getClassPath(file, path)
                String className = classPath.replace('\\', '.').replace('/', '.')
                if (className.endsWith(".class")
                        && !className.contains('R$')
                        && !className.contains('R.class')
                        && !className.contains("BuildConfig.class")) {

                    project.extensions[MockExtension.plugin].packages.each { String pkg ->
                        if (className.contains(pkg)) {
                            className = className.substring(0, className.length() - 6)
                            println 'className:'+className
                            try {
                                injectClass(path, className)
                            }catch(Exception e){
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        }
    }

    private static String getClassPath(File file, String path) {
        String filePath = file.absolutePath
        return filePath.substring(path.length() + 1, filePath.length())

    }


    private static void injectClass(String path, String className) {
        CtClass ctClass = pool.get(className)
        CtMethod[] methods = ctClass.getDeclaredMethods()
        if (methods == null || methods.length == 0) {
            return
        }
        methods.each { CtMethod method ->
            MockMethod mockMethod = method.getAnnotation(MockMethod.class)
            if (mockMethod == null) {
                return
            }
            println "method return : " + method.returnType.name
            if (!isAllow(method.returnType.name)) {
                return
            }
            injectMockMap(className,method.name,mockMethod.values(),mockMethod.defaultValue())
            println("methodDes:" + getMethodDes(className, method.name, method.returnType.name))

            method.insertBefore(getMethodDes(className, method.name, method.returnType.name))

        }
        ctClass.writeFile(path)
        ctClass.detach()
    }
    static List<String> dataType = [
            String.class.name,
            int.class.name,
            long.class.name,
            double.class.name,
            float.class.name,
            boolean.class.name,
            short.class.name,
            Integer.class.name,
            Long.class.name,
            Double.class.name,
            Float.class.name,
            Boolean.class.name,
            Short.class.name,].collect()

    static boolean isAllow(String returnType) {
        if (returnType == null) {
            return false
        }



        if (dataType.contains(returnType)) {
            return true
        }
        return false
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





}
