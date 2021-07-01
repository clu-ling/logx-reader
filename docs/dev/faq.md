# Development FAQs

## When do I have to rebuild the Docker image?

When you have changed ```build.sbt``` or any scala files in ```reader.src```.

## How do I rebuild the Docker image?

To rebuild the Docker image, simply run ```sbt dockerize``` in your terminal. Make sure you are in the correct directory, which should be where you cloned the logx-reader repo.

## There are two identical grammars in different directories, where do I add rules?

Always add/change rules in the yml files under ```reader.grammars.logx```. The changes made to these files will be updated in the src files automatically.

## How do I change processors?

The config file passed to the API upon startup should include a specification for the preffered processor. Currently only "ProxiedProcessor", "CluProcessor", and "logx" are supported processor options. To specify a custom processor use "ProxiedProcessor."

## What variables can I specify in the config file?

The user can specify `RULES_PREFIX` and `PROCESSORS_SERVICE_URL`. The latter is the url of the proxied processor service.

## What sbt command line options are there?

sbt tasks
