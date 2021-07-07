# Developing The LogX Reader

In developing the `logx-reader` there are three pieces: `rules` (and corresponding tests), `taxonomy`, and `actions`. The REST API and its endpoints are already defined. For information on how annotations are generated for use in rules, or how to use alternate processors, see the [Annotations](./annotations.md) section.

## Rules

Before developing a new rule or set of related rules, it is best to first define tests which describe the expected behavior of the rule(s). To develop tests follow the `Testing` section.

Rules in the `logx-reader` are defined using Odin. For an in depth look at Odin and writing a grammar, please see the [manual](https://arxiv.org/pdf/1509.07513.pdf).

The `logx-reader` has two grammars, `entities` and `events`. The grammars can be found under `reader/grammars/logx`. An identical set of grammars can be found under `reader/src/main/resources/org/parsertongue/reader/grammars/logx`, however, when developing the grammars the user should only modify the top level grammars. These will be edited and copied to the `src` grammars via action.

Rules can be developed with live reloading following the instructions in the [Development/Install](./install.md) section.

### Writing Rules

Odin rules are written in yaml and run over annotated text (the Odin manual includes a gentle introduction to [yaml syntax](https://arxiv.org/pdf/1509.07513.pdf#subsection.4.1)). Annotated text is produced using an external NLP service (such as `StanfordCoreNLP` or `SpaCY`), however, [Penn Tags](https://www.ling.upenn.edu/courses/Fall_2003/ling001/penn_treebank_pos.html) and [Universal Dependencies](https://universaldependencies.org/) are always included in the annotations.

There are two types of rules, `token` and `dependency`. If the type is not specified it defaults to `type=dependency`. Token rules are defined over the set of tokens and their values, while dependency rules are defined over the set of dependencies.

**Token Rule**

```yaml
- name: ner-loc
  label: Location
  priority: 1
  type: token
  pattern: |
    [entity=LOCATION]+
```

**Dependency Rule**

```yaml
- name: dancers_1
  label: Dance
  priority: 2
  pattern: |
    trigger = [lemma=dance]
    dancer:Entity = nsubj
    partner:Entity = dobj? prep_with
```

When the rules are run on text, a JSON file of `labeled mentions` is generated. Mentions are the matches found by the rules within the text, and the labels are included in a heiarchy defined in the `Taxomony`.

## Taxonomy

The `taxonomy` is a set of heiarchical relationships between mention labels. Like the grammars, the taxonomy can be found in two places but only the top level taxonomy should be modified in development.

A sample of the `logx-reader` taxonomy can be seen here:

```yaml
- Measurement:
  - Unit
  - NumericExpression:
    - Quantity
```

## Actions

Generally, when developing rules there should be no need to change the existing actions. However, if it is necessary, new actions or modifications to existing actions can be made in `reader/src/main/scala/org/parsertongue/mr/logx/odin/LogxActions.scala`. For more information about actions, see [How it Works](./howitworks.md).
