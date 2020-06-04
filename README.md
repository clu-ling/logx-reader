# logx-reader

A machine reading system to support the LogX program.

## Requirements

- [sbt](https://scala-sbt.org)
- [docker](https://docs.docker.com/get-docker)
- 8G of RAM

## Testing

You can run all project tests with the following command:

```scala
sbt test
```

We use [ScalaTest with `FlatSpec` and `Matchers`](https://www.scalatest.org/user_guide/using_matchers) for BDD-style unit tests.  

- Entity tests: `reader/src/test/scala/org/parsertongue/mr/logx/entities`

- Event/relation tests: `reader/src/test/scala/org/parsertongue/mr/logx/events`



## Documentation

You can generate API documentation using the following command:

```scala
sbt doc
```

This will generate HTML pages documenting the API for each subproject:

- `reader`: reader/target/scala-2.12/api/index.html

- `rest`: rest/target/scala-2.12/api/index.html

## Reader

The core of the LogX machine reading system is defined in the `reader` subproject.  Currently, the reader is powered by a combination of [Odin](https://github.com/clu-ling/odin-tutorial) and [statistical models](http://clulab.github.io/processors/metal) for parsing and tagging.

## REST API

### Releases

We publish releases in the form of docker images:
- logx.cloud container registry: gitlab-registry.logx.cloud/team/bbn/logx-reader
- ~~DockerHub: parsertongue/logx-reader-rest-api~~
  - _no longer maintained_

### Build

The project can be built using either docker or sbt; however, the recommended method is to use docker.

#### Docker

We construct our docker images using the sbt [native-packager](https://www.scala-sbt.org/sbt-native-packager/formats/docker.html) plugin:

```scala
sbt dockerize
```

For information on additional tasks (generating Dockerfiles, publishing images, etc.), see [this section of the `native-packager` documentation](https://www.scala-sbt.org/sbt-native-packager/formats/docker.html#tasks).

#### `sbt` (Scala)

The REST API server can be launched directly using SBT:

```scala
sbt web
```

### Run

After building the docker image, launch a container using the following command:

```docker
docker run --name="odinson-rest-api" \
  -it \
  --restart "on-failure" \
  -e "HOME=/app" \
  -p "0.0.0.0:9000:9000" \
  "lumai/odinson-rest-api"
```

Navigate to [localhost:9000/api](http://localhost:9000/api) to interactively explore the API through the [OpenAPI 3.0](http://spec.openapis.org/oas/v3.0.3) specification.

### Examples

See [this gist](https://gist.github.com/myedibleenso/9241a4c9c71d29f148ef0b8c44602b60) for sample input and output corresponding to the `/api/extract` endpoint.

## Support

For feature requests and bug reports, please open an issue.


## Authors

- [Gus Hahn-Powell](https://parsertongue.org/about)