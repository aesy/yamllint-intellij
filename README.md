<img align="left" width="80" height="80" src="./src/main/resources/META-INF/pluginIcon.svg">

# Yamllint support for IntelliJ-based IDEs

A plugin providing static code analysis of Yaml files through integration with [Yamllint](https://yamllint.readthedocs.io).

[![Plugin repository](https://img.shields.io/jetbrains/plugin/v/15349-yamllint?label=plugin%20repository&style=flat-square)](https://plugins.jetbrains.com/plugin/15349-yamllint/versions)
[![Plugin downloads](https://img.shields.io/jetbrains/plugin/d/15349-yamllint?style=flat-square)](https://plugins.jetbrains.com/plugin/15349-yamllint)
[![Plugin stars](https://img.shields.io/jetbrains/plugin/r/stars/15349-yamllint?style=flat-square)](https://plugins.jetbrains.com/plugin/15349-yamllint/reviews)
[![Build status](https://img.shields.io/github/actions/workflow/status/aesy/yamllint-intellij/ci.yml?branch=master&style=flat-square)](https://github.com/aesy/yamllint-intellij/actions)
[![Test coverage](https://img.shields.io/codecov/c/github/aesy/yamllint-intellij?style=flat-square)](https://codecov.io/gh/aesy/yamllint-intellij)
[![MIT license](https://img.shields.io/github/license/aesy/yamllint-intellij.svg?style=flat-square)](https://github.com/aesy/yamllint-intellij/blob/master/LICENSE)

## Installation 

Download and install the plugin through your IDE or through [the marketplace](https://plugins.jetbrains.com/plugin/15349-yamllint).

## Usage

Install Yamllint according to the [documentation](https://yamllint.readthedocs.io/en/stable/quickstart.html#installing-yamllint) 
and make sure it's in your PATH or as a package in your projects' SDK, such as virtualenv.

After opening a YAML file and if the project is applicable you'll be asked if you want to enable to plugin. If not, 
go to `Settings > Tools > Yamllint` to configure the plugin.

![](./img/settings.png)

Open a Yaml file, warnings and errors should be displayed inline according to your settings!

![](./img/usage.png)

## Development

#### Prerequisites

* [Gradle 8.5+](https://gradle.org/)
* [A Java 11+ Runtime](https://adoptopenjdk.net/)

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
