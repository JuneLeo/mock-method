package com.mock.internel

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class MockPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        //校验必须在application和library plugin 后面
        def androidPlugin = [AppPlugin, LibraryPlugin]
                .collect { project.plugins.findPlugin(it) }
                .find { it != null }
        if (!androidPlugin){
            throw new GradleException('must be application or library')
        }
        //依赖
        project.gradle.addListener(new DependencyListener(project))
        //拓展
        project.extensions.create(MockExtension.plugin, MockExtension)

        project.afterEvaluate {
            List<String> strings = new ArrayList<>()
            project.rootProject.subprojects{
                strings.add(it.project.name)
            }
            if (strings.contains('annotation')){
                strings.remove('annotation')
            }
            if (strings.contains('compiler')){
                strings.remove('compiler')
            }
            println '子项目数量：'+strings
            project.extensions[MockExtension.plugin].subprojects = strings
        }



        if (!project.extensions[MockExtension.plugin].isEnable){
            return
        }

        //transform
        project.android.registerTransform(new MockTransform(project))
    }

}

