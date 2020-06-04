package org.parsertongue.mr

import org.clulab.odin.{ Mention, State }
import org.clulab.processors.{ Document => CluDocument }

trait InformationExtractor {

  def extract(doc: CluDocument, state: State = new State): Seq[Mention]
}
