#!/bin/sh
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_151.jdk/Contents/Home
export MAVEN_HOME=/Users/yangwen/my/service/maven/apache-maven-3.8.1
export PATH=:$PATH:$JAVA_HOME/bin:$MAVEN_HOME/bin


version=12.0.0
rootDir=$PWD/../..
fromDir=$rootDir/jcudax-$version/src/main/java
toDir=$rootDir/jcudax-natives-$version/src/main/c

cd $fromDir
javah -d $toDir org.dromara.jcudax.JCudax
