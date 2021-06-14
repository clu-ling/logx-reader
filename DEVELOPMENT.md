# Developing The LogX Reader

For instructions on developing the reader rules live, follow the ```Dev Instructions``` in the logx-reader repo wiki on github. The following provides information on how the LogX Reader runs and how to develop the full system, including actions, processors, and dockerization.

The ```/api/extract``` endpoint will be used as a running example of the LogX reader workflow. The api endpoints are designated in ```rest.app.controllers.ApiController.scala```.

The following components of the LogX reader can be developed following the [Odin manual]( LINK ).

## Rules

Rules (and the taxonomy) are written in yaml. To develop, rules should be added to the entities and events in ```reader.grammars.logx```, NOT ```reader.src.main.resources.org.parsertongue.reader.grammars.logx```. When the api is run the ```reader.grammars.logx``` directory is to update the congruent directory in ```reader.src```, which is used in defining the ```MachineReadingSystem``` over which the api endpoints are run.

Rules should be developed in a test oriented manner. That is, tests should be written before the rules. Tests should capture what the expected output of the new rules should be, based on example inputs and the intended purpose.

### Entities

### Events

## Taxonomy

The taxonomy should be developed alongside the rules in the same manner. Always add to the taxonomy under ```reader.grammars.logx``` and NOT the corresponding directory under ```reader.src```.

The taxonomy represents the hiearchical structure of the entities and events being extracted. For example, if a rule produces the label ```Date```, the resultant mention in the json file will be displayed with the labels ```Constraint, TimeConstraint, TimeExpression, Date```, representing the hypernymic relationship of the rule's label.

## Actions

Actions dictate how text is annotated, how rules are applied to annotated text, and the final structure of the resultant mentions.

The MachineReadingSystem stipulates that the EntityFinder and EventFinder have three types of actions: actions, globalAction, and finalAction. The location of these actions is found in the config file used to initialize the MachineReadingSystem. For the running example this is ```reference.conf```, which sets the actions for both the Entity and Event Finder as ```LogxActions.scala```, which can be found under ```reader.src.main.resources.org.parsertongue.mr.logx.odin```. The global and final actions differ for the EntityFinder and EventFinder, as is expected given the flow of extraction. Global actions can be found in ```OdinActions.scala```, under ```reader.src.main.resources.scala.org.parsertongue.mr.actions```. Under the running config, the EntityFinder's globalAction is ```identityAction``` and the EventFinder's globalAction is ```cleanupEvents```. The final actions are ```cleanupEntities``` and ```finalSweep```. These globalActions and finalActions are in line with the LogX Reader workflow, in which the annotated text is first run through the EntityFinder and then the EventFinder, which is run over the "cleaned up" entity mentions, followed by a "clean up" of the event mentions and a "final sweep" of all mentions.

## Development FAQs

### When do I have to rebuild the Docker image?

When you have changed ```build.sbt``` or any scala files in ```reader.src```.

### How do I rebuild the Docker image?

To rebuild the Docker image, simply run ```sbt dockerize``` in your terminal. Make sure you are in the correct directory, which should be where you cloned the logx-reader repo.

### There are two identical grammars in different directories, where do I add rules?

Always add/change rules in the yml files under ```reader.grammars.logx```. The changes made to these files will be updated in the src files automatically.

### How do I change processors?

The config file passed to the API upon startup should include a specification for the preffered processor. Currently only "ProxiedProcessor", "CluProcessor", and "logx" are supported processor options. To specify a custom processor use "ProxiedProcessor."
