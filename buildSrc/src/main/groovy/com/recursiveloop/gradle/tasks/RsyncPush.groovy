package com.recursiveloop.gradle.tasks;

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


class RsyncPush extends DefaultTask {
  String hostName = 'localhost'
  String user
  String dest = '.'
  int sshPort = 22
  boolean verbose = false
  String src = '.'

  @TaskAction
  def runTask() {
    project.exec {
      def vTerm = verbose ? '-avzP' : 'az'

      executable 'bash'
      args '-c', "rsync ${vTerm} -e 'ssh -p ${sshPort}' ${src} ${user}@${hostName}:${dest}"
    }
  }
}
