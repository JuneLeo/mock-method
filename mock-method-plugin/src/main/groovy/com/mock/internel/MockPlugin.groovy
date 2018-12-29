package com.mock.internel

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class MockPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        def androidPlugin = [AppPlugin, LibraryPlugin]
                .collect { project.plugins.findPlugin(it) }
                .find { it != null }
        if (!androidPlugin){
            throw new GradleException('must be application or library')
        }

        project.extensions.create(MockExtension.plugin, MockExtension)
        project.android.registerTransform(new MockTransform(project))
    }

}

