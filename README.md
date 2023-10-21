Api-Concurrency
======================

### Description
This library uses the abstraction of a Future and the 'Either' structure of vavr.io to manage asynchronous operations and handle possible success or failure outcomes efficiently and elegantly in a program.

### Adding the library
To install the library add:
#### Gradle
```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

```groovy
dependencies {
    implementation 'com.github.ArielJoseArnedo:api-concurrency:2.0.0'
}
```

#### Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml
<dependency>
    <groupId>com.github.ArielJoseArnedo</groupId>
    <artifactId>api-concurrency</artifactId>
    <version>2.0.0</version>
</dependency>
```

