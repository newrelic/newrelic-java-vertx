<a href="https://opensource.newrelic.com/oss-category/#community-project"><picture><source media="(prefers-color-scheme: dark)" srcset="https://github.com/newrelic/opensource-website/raw/main/src/images/categories/dark/Community_Project.png"><source media="(prefers-color-scheme: light)" srcset="https://github.com/newrelic/opensource-website/raw/main/src/images/categories/Community_Project.png"><img alt="New Relic Open Source community project banner." src="https://github.com/newrelic/opensource-website/raw/main/src/images/categories/Community_Project.png"></picture></a>

![GitHub forks](https://img.shields.io/github/forks/newrelic/newrelic-java-vertx?style=social)
![GitHub stars](https://img.shields.io/github/stars/newrelic/newrelic-java-vertx?style=social)
![GitHub watchers](https://img.shields.io/github/watchers/newrelic/newrelic-java-vertx?style=social)

![GitHub all releases](https://img.shields.io/github/downloads/newrelic/newrelic-java-vertx/total)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/newrelic/newrelic-java-vertx)
![GitHub last commit](https://img.shields.io/github/last-commit/newrelic/newrelic-java-vertx)
![GitHub Release Date](https://img.shields.io/github/release-date/newrelic/newrelic-java-vertx)


![GitHub issues](https://img.shields.io/github/issues/newrelic/newrelic-java-vertx)
![GitHub issues closed](https://img.shields.io/github/issues-closed/newrelic/newrelic-java-vertx)
![GitHub pull requests](https://img.shields.io/github/issues-pr/newrelic/newrelic-java-vertx)
![GitHub pull requests closed](https://img.shields.io/github/issues-pr-closed/newrelic/newrelic-java-vertx)


# New Relic Java Instrumentation for Vert.x Event Bus

Provides instrumentation code for monitoring the Vert.x Event Bus.  Tracks flow across the event bus.

 Vertx-Verticles provides instrumentation for classes extends AbstractVerticle.  It does this by instrumenting each class that is deployed.  It tracks all methods except those defined by AbstractVerticle. Methods not tracked: 	start, stop, rxStart, rxStop, config, deploymentID, getVertx, init, processArgs.


## Installation

To install the instrumentation:
1. Download the latest release.
2. In the New Relic Java Agent directory, create a directory named extensions if it does not already exist.
3. Copy the downloaded jar files into the extensions directory
4. Restart the Vert.x instance.  

## Getting Started

## Usage

## Building

If you make changes to the instrumentation code and need to build the instrumentation jars, follow these steps
1. Set environment variable NEW_RELIC_EXTENSIONS_DIR.  Its value should be the directory where you want to build the jars (i.e. the extensions directory of the Java Agent).   
2. Build one or all of the jars.   
  a. To build one jar, run the command:  gradlew _moduleName_:clean  _moduleName_:install    
  b. To build all jars, run the command: gradlew clean install

## Testing

Not currently supported.  Will be supported in the future

## Verifying
To verify that the module will load into the Java Agent used the verifyInstrumentation option
see https://github.com/newrelic/newrelic-gradle-verify-instrumentation for more information

## Support

> Vertx-EventBus-3.5 supports Vert.x versions 3.5.x
> Vertx-EventBus-3.6 supports Vert.x versions 3.6.x & 3.7.0
> Vertx-EventBus-3.7.1 supports Vert.x version 3.7.1
> Vertx-EventBus-3.8 supports Vert.x versions 3.8.x & 3.9.x
> Vertx-Verticles supports Vert.x versions 3.x beyond 3.5.0
>

New Relic hosts and moderates an online forum where customers can interact with New Relic employees as well as other customers to get help and share best practices. Like all official New Relic open source projects, there's a related Community topic in the New Relic Explorers Hub. You can find this project's topic/threads here:

## Contributing
New Relic has open-sourced this project. This project is provided AS-IS WITHOUT WARRANTY OR DEDICATED SUPPORT. Issues and contributions should be reported to the project here on GitHub.

We encourage you to bring your experiences and questions to the [Explorers Hub](https://discuss.newrelic.com) where our community members collaborate on solutions and new ideas.

**A note about vulnerabilities**

As noted in our [security policy](../../security/policy), New Relic is committed to the privacy and security of our customers and their data. We believe that providing coordinated disclosure by security researchers and engaging with the security community are important means to achieve our security goals.

If you believe you have found a security vulnerability in this project or any of New Relic's products or websites, we welcome and greatly appreciate you reporting it to New Relic through [HackerOne](https://hackerone.com/newrelic).


## License
New Relic Java Instrumentation for Vert.x Event Bus is licensed under the [Apache 2.0](http://apache.org/licenses/LICENSE-2.0.txt) License.
