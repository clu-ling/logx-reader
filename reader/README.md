# Grammar development guidelines
- Grammar files should live in `reader/grammars` (NOT `src/main/resources/...`)
- Currently, we use a plugin to alter the grammars and copy them for use within the JAR for deployments and large runs.  For the sake of compatibility with this plugin's behavior, pleaserespect the following guidelines:
  - Variables should not us whitespace within `${}` (ex. `${rulePriority}` NOT `${ rulePriority }` 
  - If a new variable is added to any of the files in the grammar, you will need to add a line in `build.sbt` following the `variables in EditSource += "varName" -> "DOLLAR{varName}"` examples
