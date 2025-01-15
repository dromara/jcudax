#!/bin/sh

export GPG_TTY=$(tty)
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_151.jdk/Contents/Home

version=12.0.0
rootDir=$PWD/../..
workDir=$rootDir

cd $workDir

mvn clean javadoc:jar deploy -Dhttps.protocols=TLSv1.2 -P release -Dmaven.test.skip=true
