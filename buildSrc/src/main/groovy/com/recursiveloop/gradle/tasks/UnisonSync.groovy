package com.recursiveloop.gradle.tasks;

import org.gradle.api.tasks.TaskAction
import org.gradle.api.DefaultTask


class UnisonSync extends DefaultTask {
  String profileFileDir = "."
  String profileFile = "profile.prf"
  String profileName = "profile"
  String unisonDir = "."
  Map<String, String> variableSubstitutions = [:]
  int sshPort = 22

  @TaskAction
  def action() {
    project.copy {
      from profileFileDir
      into unisonDir
      include profileFile
      rename (profileFile, "${profileName}.prf")
      expand variableSubstitutions
    }

    project.exec {
      executable 'bash'
      args '-c', "env UNISON=${unisonDir} unison -sshargs '-p ${sshPort}' ${profileName}"
    }
  }
}
