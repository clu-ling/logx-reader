package org.parsertongue.mr.logx.events

//import TestUtils._
import org.scalatest.{ FlatSpec, Matchers }

// class TestEvents extends FlatSpec with Matchers {
//
//   "MachineReadingSystem" should "find ???" in {
//     val s1 = "???"
//     // TODO: load AnnotatedDocument JSON from resources.
//     val doc: AnnotatedDocument
//     val results = system.extract(doc)
//     m1 should not be empty
//     val events = m1 filter (_.matches("???"))
//     events should have length 2
//     hasEventWithArguments("Label1", Seq("?", "??"), decreases) should be (true)
//   }

//   it should "split events into ??? events" in {
//     val results = system.extract("Pseudomonas fluorescens and Trichoderma harzianum suppressed M. incognita and M. arenaria.")
//     results should not be empty
//     val label = "????"
//     val events = ms filter (_.matches(label))
//     events should have length 4
//     for {
//       arg1 <- Seq("?", "?")
//       arg2 <- Seq("?", "?")
//     } {
//       hasEventWithArguments(label, Seq(arg1, arg2), events) should be (true)
//     }
//   }

// }