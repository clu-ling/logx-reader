# Documentation

## API Documentation

We use `scaladoc` to generate our API documentation. To generate API documentaion use the following command:

```bash
sbt doc
```

This will generate HTML pages documenting the API for each subproject:

- `reader`: reader/target/scala-2.12/api/index.html
- `rest`: rest/target/scala-2.12/api/index.html

**NOTE: These files are copied to the `docs/api` directory via github actions when the static documentation is generated.

## General Documentation

We use `mkdocs` to generate our site documentation from markdown. Markdown source files are located under the `docs` directory. To develop the documentation with live updates use the following commands:

```bash
docker pull parsertongue/mkdocs:latest
```

This pulls our published `mkdocs` image from dockerhub.

```bash
docker run --rm -it -v $PWD:/app \
    -p 8000:8000 \
    parsertongue/mkdocs:latest \
    mkdocs serve -a 0.0.0.0:8000
```

Open your browser to `localhost:8000`.
