#!/bin/sh
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_151.jdk/Contents/Home
export MAVEN_HOME=/Users/yangwen/my/service/maven/apache-maven-3.8.1
export PATH=:$PATH:$JAVA_HOME/bin:$MAVEN_HOME/bin

version=12.0.0
rootDir=$PWD/../..
workDir=$rootDir

cd $workDir
mvn clean package  -Dmaven.test.skip=true
#mvn package -pl jcudax-natives-${version} -P windows-x86_64
#mvn package -pl jcudax-natives-${version} -P linux-x86_64

#mvn package -P windows-x86_64
#mvn package -P linux-x86_64
