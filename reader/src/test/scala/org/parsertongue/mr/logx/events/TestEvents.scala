package org.parsertongue.mr.logx.events

import org.parsertongue.mr.TestUtils._
import org.scalatest.{ FlatSpec, Matchers }

case class EventTestCase(label: String, sentence: String, args: Seq[String])

class TestEvents extends FlatSpec with Matchers {
    "MachineReadingSystem" should "find Transport events" in {

      val testCases = Seq(
        EventTestCase(
          label = "Transport", 
          sentence = "What is the risk of spoilage for frozen fish heading to Dubai on August 24th 2020?", 
          args = Seq("frozen fish", "Dubai", "August 24th 2020")
        ), 
        EventTestCase(
          label = "Transport",
          sentence = "How many F16 engines are heading to Dubai?",
          args = Seq("F16 engines", "Dubai")
        )
      )

      // TODO: load AnnotatedDocument JSON from resources.
      //val doc: AnnotatedDocument

      testCases foreach { tc =>
        val results = system.extract(tc.sentence)
        results should not be empty
        val events = results filter (_.matches(tc.label))
        //events should have length 2
        hasEventWithArguments(tc.label, tc.args, events) should be (true)
      }
    }
}
// What cargo was shipped from Los Angeles on August 12 2014 and is heading to Hamburg?
// What is the risk of spoilage for frozen fish heading to Dubai on August 24th 2020?

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


// What time will the freighter arrive in Hamburg?

// How will the freighter arrive in Hamburg?

// When will the freighter arrive in Hamburg?

// When will the train arrive?

// How frequently does the train arrive?

// Who will Jorge meet?

// Who is Roger?

// How many TEUs of DoD frozen meat is heading to Hamburg?

// What is the risk of spoilage for frozen fish heading to Dubai on August 24th 2020?
// What is the risk of spoilage for frozen fish heading to Dubai on August 24 2020?
// What is the risk of spoilage for frozen fish heading to Dubai on August 24, 2020?
// What is the risk of spoilage for frozen fish heading to Dubai on 08/24/2020?
// What is the risk of spoilage for frozen fish heading to Dubai on 08/2020?
