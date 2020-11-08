package org.parsertongue.mr.logx.events

import org.parsertongue.mr.TestUtils._
import org.scalatest.{ FlatSpec, Matchers }


class TestEvents extends FlatSpec with Matchers {
  // "PlaceHolder" should "return true" in {
  //   true should be (true)
  // }
    "MachineReadingSystem" should "find Transport events" in {

      val testCases = Seq(
        GeneralMentionTestCase(
          labels = Seq(PositiveLabelTestCase("Transport")),
          mentionSpan = PositiveTextTestCase("frozen fish heading to Dubai on August 24th 2020"), //note: trimmed
          text = "What is the risk of spoilage for frozen fish heading to Dubai on August 24th 2020?",
          args = List(
            PositiveArgTestCase(
              role = PositiveRoleTestCase("shipment"), 
              labels = Seq(PositiveLabelTestCase("Cargo")),
              text = PositiveTextTestCase("frozen fish")
            ),
            PositiveArgTestCase(
              role = PositiveRoleTestCase("destination"),
              labels = Seq(PositiveLabelTestCase("Location")),
              text  = PositiveTextTestCase("Dubai")
            ),
            PositiveArgTestCase(
              role = PositiveRoleTestCase("time"),
              labels = Seq(PositiveLabelTestCase("TimeExpression"), PositiveLabelTestCase("OnTime")),
              text = PositiveTextTestCase("on August 24th 2020")
            )
          )
        ),
        GeneralMentionTestCase(
          labels = Seq(PositiveLabelTestCase("Transport")),
          mentionSpan = PositiveTextTestCase("F16 engines are heading to Dubai"),
          text = "How many F16 engines are heading to Dubai?",
          args = List(
            PositiveArgTestCase(
              role = PositiveRoleTestCase("shipment"), 
              labels = Seq(PositiveLabelTestCase("Cargo")),
              text = PositiveTextTestCase("F16 engines")
            ),
            PositiveArgTestCase(
              role = PositiveRoleTestCase("destination"),
              labels = Seq(PositiveLabelTestCase("Location")),
              text  = PositiveTextTestCase("Dubai")
            ),
            // NegativeArgTestCase(
            //   role = PositiveRoleTestCase("shipment"), 
            //   labels = Seq(PositiveLabelTestCase("Cargo")),
            //   text = PositiveTextTestCase("F16")
            // ),
    //         // NegativeArgTestCase( // trying: negative argtestcase within positive event
    //         //   role = "destination",
    //         //   labels = Seq("Location"),
    //         //   text  = "Tucson" // correct role and label, wrong text
    //         // ),
    //         // NegativeArgTestCase( // trying: negative argtestcase within positive event
    //         //   role = "destination",
    //         //   labels = Seq("Unit"), // wrong label only
    //         //   text  = "Dubai"
    //         // ),
    //         // NegativeArgTestCase( // trying: negative argtestcase within positive event
    //         //   role = "shipment", // wrong role
    //         //   labels = Seq("Location"),
    //         //   text  = "Dubai"
    //         // ),
    //         // NegativeArgTestCase( // trying: negative argtestcase within positive event
    //         //   role = "destination",  //feeding correct everything; should fail
    //         //   labels = Seq("Location"),
    //         //   text  = "Dubai"
    //         // )
          )
        ),
    //     PositiveEventTestCase(
    //       labels = Seq("Transport"),
    //       text = "How many TEUs of DoD Frozen Meat are heading to Hamburg?",
    //       args = List(
    //         PositiveArgTestCase(
    //           role = "shipment", 
    //           labels = Seq("QuantifiedCargo"),
    //           text = "TEUs of DoD Frozen Meat"
    //         ),
    //         PositiveArgTestCase(
    //           role = "destination",
    //           labels = Seq("Location"),
    //           text  = "Hamburg"
    //         )
    //       )
    //     ),
    //     PositiveEventTestCase(
    //       labels = Seq("Transport"),
    //       text = "Frozen food that arrived before September 21st 2020 but after September 28th 2020.",
    //       args = List(
    //         PositiveArgTestCase(
    //           role = "time",
    //           labels = Seq("BeforeTime", "TimeExpression"),
    //           text = "before September 21st 2020"
    //         ),
    //         PositiveArgTestCase(
    //           role = "time",
    //           labels = Seq("AfterTime", "TimeExpression"),
    //           text = "after September 28th 2020"
    //         )
    //       )
    //     )
      )

    //   // TODO: load AnnotatedDocument JSON from resources.
    //   //val doc: AnnotatedDocument

      testCases foreach { tc =>
        val results = system.extract(tc.text)
        results should not be empty
        checkMention(tc, results) should be (true)
      }
    }
    
    // it should "find Query events" in {

    //   val testCases = Seq(
    //     EventTestCase(
    //       labels = Seq(
    //         PositiveLabelTestCase("Query"), 
    //         PositiveLabelTestCase("WhatQuery")
    //       ),
    //       text = "Find ports near Hamburg with enough excess cargo capacity to handle shipments redirected from Hamburg before last week",
    //       args = List(
    //         ArgTestCase(
    //           role = PositiveRoleTestCase("need"),
    //           labels = Seq(
    //             PositiveLabelTestCase("UnspecifiedPort"),
    //             PositiveLabelTestCase("Location"),
    //           )
    //           text = "ports"
    //         ),
    //         PositiveArgTestCase(
    //           role = "constraints",
    //           labels = Seq("ProximityConstraint", "Constraint"),
    //           text = "near Hamburg"
    //         ),
    //         PositiveArgTestCase(
    //           role = "constraints",
    //           labels = Seq("QuantityConstraint", "Constraint"),
    //           text = "enough excess cargo capacity"
    //         ),
    //         PositiveArgTestCase(
    //           role = "constraints",
    //           labels = Seq("TimeConstraint", "BeforeTime"),
    //           text = "before last week"
    //         )
    //       )
    //     ),
    //     PositiveEventTestCase(
    //       labels = Seq("Query", "WhatQuery"),
    //       text = "Find ports near Hamburg with enough excess cargo capacity to handle shipments redirected from Hamburg since February 12",
    //       args = List(
    //         PositiveArgTestCase(
    //           role = "need",
    //           labels = Seq("UnspecifiedPort", "Location"),
    //           text = "ports"
    //         ),
    //         PositiveArgTestCase(
    //           role = "constraints",
    //           labels = Seq("ProximityConstraint", "Constraint"),
    //           text = "near Hamburg"
    //         ),
    //         PositiveArgTestCase(
    //           role = "constraints",
    //           labels = Seq("QuantityConstraint", "Constraint"),
    //           text = "enough excess cargo capacity"
    //         ),
    //         PositiveArgTestCase(
    //           role = "constraints",
    //           labels = Seq("AfterTime", "TimeConstraint"),
    //           text = "since February 12"
    //         )
    //       )
    //     ),
    //     PositiveEventTestCase(
    //       labels = Seq("LocationQuery"),
    //       text = "What are alternative ports with enough cargo capacity to handle shipments redirected from Hamburg",
    //       args = List(
    //         PositiveArgTestCase(
    //           role = "constraints",
    //           labels = Seq("OriginConstraint", "Constraint"),
    //           text = "Hamburg"
    //         ),
    //         PositiveArgTestCase(
    //           role = "constraints",
    //           labels = Seq("QuantityConstraint", "Constraint"),
    //           text = "enough cargo capacity"
    //         ),
    //         PositiveArgTestCase(
    //           role = "need",
    //           labels = Seq("Concept"),
    //           text = "ports"
    //         )
    //       )
    //     ),
    //     PositiveEventTestCase(
    //       labels = Seq("CargoQuery", "QuantityQuery"),
    //       text = "How many TEUs of zebras are heading to Scotland from Zimbabwe?",
    //       args = List(
    //         PositiveArgTestCase(
    //           role = "need",
    //           labels = Seq("QuantifiedCargo"),
    //           text = "TEUs of zebras"
    //         ),
    //         PositiveArgTestCase(
    //           role = "constraints",
    //           labels = Seq("OriginConstraint", "Constraint"),
    //           text = "Zimbabwe"
    //         ),
    //         PositiveArgTestCase(
    //           role = "constraints",
    //           labels = Seq("DestinationConstraint", "Constraint"),
    //           text = "Scotland"
    //         )
    //       )
    //     ),

    //     PositiveEventTestCase(
    //       text = "How much frozen meat is heading to Hamburg?",
    //       labels = Seq("CargoQuery", "QuantityQuery"),
    //       args = List(
    //         PositiveArgTestCase(
    //           role = "need",
    //           labels = Seq("Cargo"),
    //           text = "frozen meat"
    //         ),
    //         PositiveArgTestCase(
    //           role = "constraints",
    //           labels = Seq("DestinationConstraint", "Constraint"),
    //           text = "Hamburg"
    //         )
    //       )
    //     ),
    //     PositiveEventTestCase(
    //       text = "How many shipments of frozen meat are heading to Hamburg?",
    //       labels = Seq("CargoQuery", "QuantityQuery"),
    //       args = List(
    //         PositiveArgTestCase(
    //           role = "need",
    //           labels = Seq("ShipmentOf", "Cargo"),
    //           text = "shipments of frozen meat"
    //         ),
    //         PositiveArgTestCase(
    //           role = "constraints",
    //           labels = Seq("DestinationConstraint", "Constraint"),
    //           text = "Hamburg"
    //         )
    //       )
    //     ),
    //     PositiveEventTestCase(
    //       text = "Which vessel left on Thursday?",
    //       labels = Seq("VesselQuery", "Query"),
    //       args = List(
    //         PositiveArgTestCase(
    //           role = "need",
    //           labels = Seq("Vessel"),
    //           text = "vessel"
    //         ),
    //         PositiveArgTestCase(
    //           role = "constraints",
    //           labels = Seq("OnTime"),
    //           text = "on Thursday"
    //         )
    //       )
    //     ),
    //     PositiveEventTestCase(
    //       text = "What cargo left Los Angeles last week?",
    //       labels = Seq("CargoQuery"),
    //       args = List(
    //         PositiveArgTestCase(
    //           role = "need",
    //           labels = Seq("UnspecifiedCargo"),
    //           text = "cargo"
    //         ),
    //         PositiveArgTestCase(
    //           role = "constraints",
    //           labels = Seq("TimeConstraint"),
    //           text = "last week"
    //         )
    //       )
    //     )
    //   )

    //   testCases foreach { tc =>
    //     val results = system.extract(tc.text)
    //     results should not be empty
    //     checkEvent(tc, results) should be (true)
    //   }
    // }

    // it should "not find Query events" in {

    //   val testCases = Seq(
    //     NegativeEventTestCase(
    //       labels = Seq("CargoQuery", "QuantityQuery"),
    //       text = "Many zebras are galloping to Scotland from Zimbabwe?",
    //       args = Nil
    //       // List(
    //       //   NegativeArgTestCase(
    //       //     role = "need",
    //       //     labels = Seq("QuantifiedCargo"),
    //       //     text = "zebras"
    //       //   ),
    //       //   NegativeArgTestCase(
    //       //     role = "constraints",
    //       //     labels = Seq("OriginConstraint", "Constraint"),
    //       //     text = "Zimbabwe"
    //       //   ),
    //       //   NegativeArgTestCase(
    //       //     role = "constraints",
    //       //     labels = Seq("DestinationConstraint", "Constraint"),
    //       //     text = "Scotland"
    //       //   )
    //       // )
    //     ),
    //     NegativeEventTestCase(
    //       labels = Seq("CargoQuery", "QuantityQuery"),
    //       text = "Some zebras are galloping to Scotland from Zimbabwe",
    //       args = List( // this works -- ie passing in negargtestcase list, not just Nil as above
    //          NegativeArgTestCase(
    //           role = "need",
    //           labels = Seq("QuantifiedCargo"),
    //           text = "zebras"
    //         ),
    //         NegativeArgTestCase(
    //           role = "constraints",
    //           labels = Seq("OriginConstraint", "Constraint"),
    //           text = "Zimbabwe"
    //         ),
    //         NegativeArgTestCase(
    //           role = "constraints",
    //           labels = Seq("DestinationConstraint", "Constraint"),
    //           text = "Scotland"
    //         )
    //       )
    //     )
    //   )
    //   testCases foreach { tc =>
    //     val results = system.extract(tc.text)
    //     results should not be empty
    //     checkEvent(tc, results) should be (true)
    //   }
    // }

    // it should "find structured TimeExpressions" in {
    //   val testCases = Seq(
    //     PositiveEventTestCase(
    //       labels = Seq("IntervalTime", "TimeExpression"),
    //       text = "How many TEUs of frozen fish are heading to Dubai between September 30th 2020 and October 2nd 2020?",
    //       args = List(
    //         PositiveArgTestCase(
    //           role = "start",
    //           labels = Seq("IntervalTime", "TimeExpression"),
    //           text = "September 30th 2020"
    //         ),
    //         PositiveArgTestCase(
    //           role = "end",
    //           labels = Seq("IntervalTime", "TimeExpression"),
    //           text = "October 2nd 2020"
    //         )
    //       )
    //     )
    //   )

    //   testCases foreach { tc =>
    //     val results = system.extract(tc.text)
    //     results should not be empty
    //     checkEvent(tc, results) should be (true)
    //   }
    // }
}
// What cargo was shipped from Los Angeles on August 12 2014 and is heading to Hamburg?
// What is the risk of spoilage for frozen fish heading to Dubai on August 24th 2020?

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
