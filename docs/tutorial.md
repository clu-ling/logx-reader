# REST API

The reader can be used through the REST API. After building the docker image, launch a container using the following command:

```bash
docker run --name="logx-reader" \
  -it \
  --restart "on-failure" \
  -e "HOME=/app" \
  -p "0.0.0.0:9000:9000" \
  "gitlab-registry.logx.cloud/team/bbn/logx-reader:latest"
```

Navigate to [localhost:9000/api](http://localhost:9000/api) to interactively explore the API through the [OpenAPI 3.0](http://spec.openapis.org/oas/v3.0.3) specification.

## API Endpoints and Examples

| Endpoint | Example Output |
| :--- | :--- |
| /api/extract | See [this gist](https://gist.github.com/myedibleenso/9241a4c9c71d29f148ef0b8c44602b60) |
| /api/annotate | See [Annotations](./dev/annotations.md) |
| /api/parse-query | .. |
| /api/taxonomy/hyponyms-for | .. |
| /api/taxonomy/hypernyms-for | .. |
