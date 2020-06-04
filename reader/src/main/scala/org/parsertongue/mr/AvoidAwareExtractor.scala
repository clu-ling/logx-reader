package org.parsertongue.mr

import org.clulab.odin._
import org.clulab.processors.{ Document => CluDocument }

/** Extractor that avoids certain mentions */
class AvoidAwareExtractor(
  rules: String,
  actions: Actions = new Actions,
  globalAction: Action = identityAction,
  avoidRules: String,
  avoidActions: Actions = new Actions,
  avoidGlobalAction: Action = identityAction
) extends OdinExtractor(
  rules        = rules,
  actions      = actions,
  globalAction = globalAction
) {

  val avoidEngine  = ExtractorEngine(rules = avoidRules, actions = avoidActions, globalAction = avoidGlobalAction)

  // FIXME: should we explicitly filter here or enforce filtering via a subtype of the entityActions?
  override def extract(doc: CluDocument, state: State): Seq[Mention] = {
    val avoidState: State = State(avoidEngine.extractFrom(doc, state))
    baseEngine.extractFrom(doc, avoidState)
  }
}
