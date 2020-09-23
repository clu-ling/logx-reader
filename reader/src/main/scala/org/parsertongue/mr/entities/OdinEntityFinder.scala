package org.parsertongue.mr.entities

import org.parsertongue.mr.OdinExtractor
import org.clulab.odin.{ Action, Actions, identityAction }


class OdinEntityFinder(
  rules: String,
  actions: Actions = new Actions,
  globalAction: Action = identityAction,
  finalAction: Action = identityAction
) extends OdinExtractor(
  rules = rules,
  actions = actions,
  globalAction = globalAction,
  finalAction = finalAction
) with EntityFinder { }

