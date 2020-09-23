package org.parsertongue.mr.events

import org.parsertongue.mr.OdinExtractor
import org.clulab.odin._
import org.clulab.processors.Document

class OdinEventFinder(
  rules: String,
  actions: Actions = new Actions,
  globalAction: Action = identityAction,
  finalAction: Action  = identityAction
) extends OdinExtractor(
  rules = rules,
  actions = actions,
  globalAction = globalAction,
  finalAction = identityAction
) with EventFinder { }
