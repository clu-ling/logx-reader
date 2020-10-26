package org.parsertongue.mr.logx.events

import org.parsertongue.mr.TestUtils._
import org.scalatest.{ FlatSpec, Matchers }


class TestEvents extends FlatSpec with Matchers {

    "MachineReadingSystem" should "find Transport events" in {

      val testCases = Seq(
        PositiveEventTestCase(
          labels = Seq("Transport"), 
          text = "What is the risk of spoilage for frozen fish heading to Dubai on August 24th 2020?", 
          args = List(
            PositiveArgTestCase(
              role = "shipment", 
              labels = Seq("Cargo"),
              text = "frozen fish"
            ),
            PositiveArgTestCase(
              role = "destination",
              labels = Seq("Location"),
              text  = "Dubai"
            ),
            PositiveArgTestCase(
              role = "time",
              labels = Seq("TimeExpression", "OnTimeExpression"),
              text = "on August 24th 2020"
            )
          )
        ),
        PositiveEventTestCase(
          labels = Seq("Transport"),
          text = "How many F16 engines are heading to Dubai?",
          args = List(
            PositiveArgTestCase(
              role = "shipment", 
              labels = Seq("Cargo"),
              text = "F16 engines"
            ),
            PositiveArgTestCase(
              role = "destination",
              labels = Seq("Location"),
              text  = "Dubai"
            )
          )
        ),
        PositiveEventTestCase(
          labels = Seq("Transport"),
          text = "How many TEUs of DoD Frozen Meat are heading to Hamburg?",
          args = List(
            PositiveArgTestCase(
              role = "shipment", 
              labels = Seq("Cargo"),
              text = "DoD Frozen Meat"
            ),
            PositiveArgTestCase(
              role = "destination",
              labels = Seq("Location"),
              text  = "Hamburg"
            )
          )
        )
      )

      // TODO: load AnnotatedDocument JSON from resources.
      //val doc: AnnotatedDocument

      testCases foreach { tc =>
        val results = system.extract(tc.text)
        results should not be empty
        checkEvent(tc, results) should be (true)
      }
    }
    
    it should "find Query events" in {

      val testCases = Seq(
        PositiveEventTestCase(
          labels = Seq("Query", "WhatQuery"),
          text = "Find ports near Hamburg with enough excess cargo capacity to handle shipments redirected from Hamburg before last week",
          args = List(
            PositiveArgTestCase(
              role = "need",
              labels = Seq("Concept"),
              text = "ports"
            ),
            PositiveArgTestCase(
              role = "constraints",
              labels = Seq("ProximityConstraint", "Constraint"),
              text = "near Hamburg"
            ),
            PositiveArgTestCase(
              role = "constraints",
              labels = Seq("QuantityConstraint", "Constraint"),
              text = "enough excess cargo capacity"
            ),
            PositiveArgTestCase(
              role = "constraints",
              labels = Seq("TimeConstraint", "TimeExpression", "BeforeTimeExpression"),
              text = "before last week"
            )
          )
        ),
        PositiveEventTestCase(
          foundBy = Some("what-query-1"),
          labels = Seq("WhatQuery"),
          text = "What are alternative ports with enough cargo capacity to handle shipments redirected from Hamburg",
          args = List(
            PositiveArgTestCase(
              role = "constraints",
              labels = Seq("ProximityConstraint", "Constraint"),
              text = "from Hamburg"
            ),
            PositiveArgTestCase(
              role = "constraints",
              labels = Seq("QuantityConstraint", "Constraint"),
              text = "enough cargo capacity"
            ),
            PositiveArgTestCase(
              role = "need",
              labels = Seq("Concept"),
              text = "alternative ports"
            )
          )
        )
      )

      testCases foreach { tc =>
        val results = system.extract(tc.text)
        results should not be empty
        checkEvent(tc, results) should be (true)
      }
    }

    it should "find structured TimeExpressions" in {
      val testCases = Seq(
        PositiveEventTestCase(
          labels = Seq("IntervalTimeExpression", "TimeExpression"),
          text = "How many TEUs of frozen fish are heading to Dubai between September 30th 2020 and October 2nd 2020?",
          args = List(
            PositiveArgTestCase(
              role = "start",
              labels = Seq("IntervalTimeExpression", "TimeExpression"),
              text = "September 30th 2020"
            ),
            PositiveArgTestCase(
              role = "end",
              labels = Seq("IntervalTimeExpression", "TimeExpression"),
              text = "October 2nd 2020"
            )
          )
        ),
        PositiveEventTestCase(
          labels = Seq("BeforeTimeExpression", "TimeExpression"),
          text = "Frozen food that arrived before September 21st 2020 but after September 28th 2020.",
          args = List(
            PositiveArgTestCase(
              role = "date",
              labels = Seq("BeforeTimeExpression", "TimeExpression"),
              text = "September 21st 2020"
            )
          )
        ),
        PositiveEventTestCase(
          labels = Seq("AfterTimeExpression", "TimeExpression"),
          text = "Frozen food that arrived before September 21st 2020 but after September 28th 2020.",
          args = List(
            PositiveArgTestCase(
              role = "date",
              labels = Seq("AfterTimeExpression", "TimeExpression"),
              text = "September 28th 2020"
            )
          )
        )
      )

      testCases foreach { tc =>
        val results = system.extract(tc.text)
        results should not be empty
        checkEvent(tc, results) should be (true)
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
