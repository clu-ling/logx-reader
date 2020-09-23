package org.parsertongue.mr

import org.clulab.odin._
import org.clulab.processors.{ Document => CluDocument }

class OdinExtractor(
  val rules: String,
  val actions: Actions = new Actions,
  val globalAction: Action = identityAction,
  val finalAction: Action = identityAction
) extends InformationExtractor {

  val baseEngine = ExtractorEngine(rules = rules, actions = actions, globalAction = globalAction)

  def extract(doc: CluDocument, state: State): Seq[Mention] = {
    val res = baseEngine.extractFrom(doc, state)
    finalAction(res, state)
  }

}
