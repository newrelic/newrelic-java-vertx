[![New Relic Experimental header](https://github.com/newrelic/opensource-website/raw/master/src/images/categories/Experimental.png)](https://opensource.newrelic.com/oss-category/#new-relic-experimental)

# New Relic Java Instrumentation for Vert.x Event Bus

> Provides instrumentation code for monitoring the Vert.x Event Bus.  Tracks flow across the event bus.
> 
> Vertx-Verticles provides instrumentation for classes extends AbstractVerticle.  It does this by instrumenting each class that is deployed.  It tracks all methods except those defined by AbstractVerticle. Methods not tracked: 	start, stop, rxStart, rxStop, config, deploymentID, getVertx, init, processArgs.


## Installation

> Clone this repository to your local disk

## Getting Started
> Install Gradle if needed.
> 
> Project can be imported into Eclipse or IntelliJ
>
> Eclipse:
> All modules
> gradle eclipse
>
> Individual Module
> gradle moduleName:eclipse
> e.g. gradle Vertx-EventBus-3.8:eclipse
>
> IntelliJ
> same command except use idea rather than eclipse


## Usage
>


## Building

> Set the environment variable NEW_RELIC_EXTENSIONS_DIR to the directory where you would like to build the extension jar(s)
>
> To build all modules
> gradle clean install
>
> To build a modules
> gradle moduleName:clean moduleName:install

## Testing

> Not currently supported.  Will be supported in the future

## Verifying

> To verify that the module will load into the Java Agent used the verifyInstrumentation option
> see https://github.com/newrelic/newrelic-gradle-verify-instrumentation for more information

## Support

> Vertx-EventBus-3.5 supports Vert.x versions 3.5.x
> Vertx-EventBus-3.6 supports Vert.x versions 3.6.x & 3.7.0
> Vertx-EventBus-3.7.1 supports Vert.x version 3.7.1
> Vertx-EventBus-3.8 supports Vert.x versions 3.8.x & 3.9.x
> Vertx-Verticles supports Vert.x versions 3.x beyond 3.5.0
> 

New Relic hosts and moderates an online forum where customers can interact with New Relic employees as well as other customers to get help and share best practices. Like all official New Relic open source projects, there's a related Community topic in the New Relic Explorers Hub. You can find this project's topic/threads here:

>Add the url for the support thread here

## Contributing
We encourage your contributions to improve [project name]! Keep in mind when you submit your pull request, you'll need to sign the CLA via the click-through using CLA-Assistant. You only have to sign the CLA one time per project.
If you have any questions, or to execute our corporate CLA, required if your contribution is on behalf of a company,  please drop us an email at opensource@newrelic.com.

## License
[Project Name] is licensed under the [Apache 2.0](http://apache.org/licenses/LICENSE-2.0.txt) License.
>[If applicable: The [project name] also uses source code from third-party libraries. You can find full details on which libraries are used and the terms under which they are licensed in the third-party notices document.]
