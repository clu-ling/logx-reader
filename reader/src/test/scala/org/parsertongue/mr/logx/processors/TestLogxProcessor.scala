package org.parsertongue.mr.logx.processors

import org.clulab.struct.GraphMap
import org.parsertongue.mr.logx.LogxTestUtils._
import org.scalatest.{ FlatSpec, Matchers }

// How many TEUs of DoD Frozen Meat are heading to Hamburg?


class TestLogxProcessor extends FlatSpec with Matchers {

  val text = "How many TEUs of DoD Frozen Meat are heading to Hamburg?"
  val doc = logxProc.annotate(text)

  "LogxProcessor" should "annotate text" in {
    doc.sentences.length should be (1)
  }

  it should "include semantic roles" in {
    doc.sentences.head.graphs contains GraphMap.SEMANTIC_ROLES
  }

  it should "include Penn-style POS tags" in {
    val sent = doc.sentences.head 
    sent.tags should not equal None
    sent.tags.get should contain theSameElementsAs Seq(
      "WRB", 
      "JJ", 
      "NNS", 
      "IN", 
      "NNP", 
      "NNP", 
      "NNP", 
      "VBP", 
      "VBG", 
      "TO", 
      "NNP", 
      "."
    )
  }
}