package com.mock.internel

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Project

class MockTransform extends Transform {

    Project project

    MockTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return "MockTransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        super.transform(context, inputs, referencedInputs, outputProvider, isIncremental)
        JarInput injectJarInput

        inputs.each { TransformInput input ->


            //ClassPool中注入注解
            input.jarInputs.each { JarInput jarInput->
                if (jarInput.file.absolutePath.contains('mock-method-android')){
                    Inject.addPoolPath(jarInput.file.absolutePath)
                }
            }
            //jar
            input.jarInputs.each { JarInput jarInput ->
                //辅助类
                if (jarInput.file.absolutePath.contains('mock-method-android')) {
                    Inject.addPoolPath(jarInput.file.getAbsolutePath())
                    injectJarInput = jarInput
                } else {
                    //jar
                    Inject.injectJarPath(jarInput.file.getAbsolutePath(), project)

                    def jarName = jarInput.name
                    def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                    if (jarName.endsWith(".jar")) {
                        jarName = jarName.substring(0, jarName.length() - 4)
                    }
                    def dest = outputProvider.getContentLocation(jarName + md5Name,
                            jarInput.contentTypes, jarInput.scopes, Format.JAR)
                    FileUtils.copyFile(jarInput.file, dest)
                }
            }

            //文件
            input.directoryInputs.each { DirectoryInput directoryInput ->
                println '输出路径:' + directoryInput.file.absolutePath
                Inject.injectDir(directoryInput.file.absolutePath, project)
                def dest = outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes,
                        Format.DIRECTORY)
                FileUtils.copyDirectory(directoryInput.file, dest)
            }
        }

        //辅助类
        if (injectJarInput != null) {
            println '辅助类'
            println injectJarInput.file.absolutePath


            Inject.generatorMockMap(injectJarInput.file.getAbsolutePath(), project)
            def jarName = injectJarInput.name
            def md5Name = DigestUtils.md5Hex(injectJarInput.file.getAbsolutePath())
            if (jarName.endsWith(".jar")) {
                jarName = jarName.substring(0, jarName.length() - 4)
            }
            def dest = outputProvider.getContentLocation(jarName + md5Name,
                    injectJarInput.contentTypes, injectJarInput.scopes, Format.JAR)
            FileUtils.copyFile(injectJarInput.file, dest)

        }

    }
}