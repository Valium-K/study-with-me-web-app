npm on gradle
=============

### build.gradle
```
plugins {
    id "com.moowork.node" version "1.3.1" // npm plugin
}

dependencies {
    implementation "com.moowork.gradle:gradle-node-plugin:1.3.1"
}

apply plugin: "com.moowork.node"

// npm using on gradle project
    tasks.register("npm-Install") {
    group = "application"
    description = "Installs dependencies from package.json"
    tasks.appNpmInstall.exec();
}

task appNpmInstall(type: NpmTask) {
    // src/main/resources/static
    description = "Installs dependencies from package.json"
    workingDir = file("/src/main/resources/static")
    args = ['install']
}
```