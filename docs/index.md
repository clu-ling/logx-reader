# LogX-Reader

## What is it?

The `LogX-Reader` is a machine reading system which utilizes [Odin](https://github.com/clu-ling/odin-tutorial) and [statistical models](http://clulab.github.io/processors/metal.html) for parsing, tagging, and rule-based entity/event extraction. The system contains three [sbt subprojects](https://www.scala-sbt.org/1.x/docs/Multi-Project.html), `core`, `reader`, and `rest`. The API documentation for each subproject can be found here: [core](./api/core/index.html), [reader](./api/reader/index.html), [rest](./api/rest/index.html).

The `core` subproject includes development utilities and metadata.

The `reader` subproject contains the `LogX-Reader`.

The `rest` subproject defines the REST API for the reader.

## How do I use it?

The `LogX-Reader` can be used through a REST API defined in the `rest` subproject. The REST API can be run following the instructions in [Usage](./tutorial.md) after installing.

## Developing

For instructions on developing the `LogX-Reader`, navigate to the [Development](./dev/install.md) section.
