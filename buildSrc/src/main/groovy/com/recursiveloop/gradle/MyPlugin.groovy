package com.recursiveloop.gradle;

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project


class MyPlugin implements Plugin<Project> {
  void apply(Project project) {
    project.task('neato') {
      doLast {
        println 'This task is neato!'
      }
    }
  }
}
