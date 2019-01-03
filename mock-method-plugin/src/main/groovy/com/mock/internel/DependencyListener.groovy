package com.mock.internel

import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyResolutionListener
import org.gradle.api.artifacts.ResolvableDependencies

class DependencyListener implements DependencyResolutionListener {
    private Project mProject

    DependencyListener(Project project) {
        mProject = project
    }

    @Override
    void beforeResolve(ResolvableDependencies resolvableDependencies) {
        if (mProject.extensions[MockExtension.plugin].dependencyEnable) {
            println '内部依赖使用'
            mProject.rootProject.repositories.maven {
                url "https://jitpack.io"
            }
            mProject.rootProject.subprojects {
                println 'project name:' + it.name
                it.configurations.implementation.dependencies.add(
                        it.dependencies.create('com.github.JuneLeo:mock-method-annotation:1.0.8'))
            }
//            mProject.configurations.annotationProcessor.dependencies.add(
//                    mProject.dependencies.create('com.github.JuneLeo:mock-method-compiler:1.0.8'))
        }
        mProject.gradle.removeListener(this)
    }

    @Override
    void afterResolve(ResolvableDependencies resolvableDependencies) {

    }
}