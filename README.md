# Yamllint support for IntelliJ-based IDEs

[![Build status](https://img.shields.io/github/workflow/status/aesy/yamllint-intellij/Continous%20Integration?style=flat-square)](https://github.com/aesy/yamllint-intellij/actions)
[![Test coverage](https://img.shields.io/codecov/c/github/aesy/yamllint-intellij?style=flat-square)](https://codecov.io/github/aesy/yamllint-intellij)
[![MIT license](https://img.shields.io/github/license/aesy/yamllint-intellij.svg?style=flat-square)](https://github.com/aesy/yamllint-intellij/blob/master/LICENSE)

## Development

#### Prerequisites

* [Gradle 5.2+](https://gradle.org/)
* [A Java 8+ Runtime](https://adoptopenjdk.net/)

#### Build

To compile and package the plugin, simply issue the following command:

```sh
$ ./gradlew buildPlugin
```

This will create a zip located in `build/distributions/`.

#### Test

Run the tests as you would in any other gradle project:

```sh
$ ./gradlew test
```

#### Run

To run the plugin from the command line, the following command can be used:

```sh
$ ./gradlew runIde
```

This will start IntelliJ Community with all necessary plugins loaded. Logs are located at 
`build/idea-sandbox/system/log/idea.log`.

## Contribute
Use the [issue tracker](https://github.com/aesy/yamllint-intellij/issues) to report bugs or make feature requests. 

## License
MIT, see [LICENSE](/LICENSE) file.
