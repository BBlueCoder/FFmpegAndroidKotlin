
# FFmpegAndroidKotlin
FFmpegAndroidKotlin is a library to run `FFmpeg` commands on your android application. It includes prebuilt `binaries` for `android`.
It built using Kotlin and use Flows to run commands on different thread than the main thread.


## Features

- Supports `API Level 21+`
- Supports `arm-v7a`,`arm64-v8a`,`x86` and `x86_64` architectures
- Wrapper to run `FFmpeg` commands
- Run custom commands


## Using

1. Add `mavenCentral()` to your repositories in your build.gradle

````yaml
    repositories {
        mavenCentral()
    }
````
2. Add `FFmpegAndroidKotlin` dependency to your build.gradle

````yaml
    implementation 'com.github.BBlueCoder:FFmpegAndroidKotlin:2.0.7'
````
