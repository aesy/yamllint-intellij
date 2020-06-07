# Yamllint support for IntelliJ-based IDEs

[![MIT license](https://img.shields.io/github/license/aesy/yamllint-intellij.svg?style=flat-square)](https://github.com/aesy/yamllint-intellij/blob/master/LICENSE)

## Development

#### Prerequisites

* [Gradle 5.2+](https://gradle.org/)
* [A Java 8+ Runtime](https://adoptopenjdk.net/)

#### Build

To compile and package the plugin, simply issue the following command:

$ `gradle buildPlugin`

This will create a zip located in `build/distributions/`.

#### Run

To run the plugin from the command line, the following command can be used:

$ `gradle runIde`

This will start IntelliJ Community with all necessary plugins loaded. Logs are located at 
`build/idea-sandbox/system/log/idea.log`.

## Contribute
Use the [issue tracker](https://github.com/aesy/yamllint-intellij/issues) to report bugs or make feature requests. 

## License
MIT, see [LICENSE](/LICENSE) file.
