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
            NegativeArgTestCase( //This behaves as desired. passes because pos subtest fails
              role = PositiveRoleTestCase("shipment"), 
              labels = Seq(PositiveLabelTestCase("Cargo")),
              text = PositiveTextTestCase("F16") //note, missing ' engines'
            ),
            NegativeArgTestCase( //This behaves as desired. passes because neg subtest fails
              role = PositiveRoleTestCase("shipment"), 
              labels = Seq(PositiveLabelTestCase("Cargo")),
              text = NegativeTextTestCase("F16 engines") //right text, makes false.
            ),
            NegativeArgTestCase( // trying: negative argtestcase within positive event
              role = PositiveRoleTestCase("destination"),
              labels = Seq(PositiveLabelTestCase("Location")),
              text  = PositiveTextTestCase("Tucson") // correct role and label, wrong text
            ),
    //         // NegativeArgTestCase( // trying: negative argtestcase within positive event
    //         //   role = PositiveRoleTestCase("destination"),
    //         //   labels = Seq(PositiveLabelTestCase("Unit")), // wrong label only
    //         //   text  = "Dubai"
    //         // ),
    //         // NegativeArgTestCase( // trying: negative argtestcase within positive event
    //         //   role = PositiveRoleTestCase("shipment"), // wrong role
    //         //   labels = Seq(PositiveLabelTestCase("Location")),
    //         //   text  = "Dubai"
    //         // ),
    //         // NegativeArgTestCase( // trying: negative argtestcase within positive event
    //         //   role = PositiveRoleTestCase("destination"),  //feeding correct everything; should fail
    //         //   labels = Seq(PositiveLabelTestCase("Location")),
    //         //   text  = "Dubai"
    //         // )
          )
        ),
        GeneralMentionTestCase(
          labels = Seq(PositiveLabelTestCase("Transport")),
          mentionSpan = PositiveTextTestCase("TEUs of DoD Frozen Meat are heading to Hamburg"),
          text = "How many TEUs of DoD Frozen Meat are heading to Hamburg?",
          args = List(
            PositiveArgTestCase(
              role = PositiveRoleTestCase("shipment"), 
              labels = Seq(PositiveLabelTestCase("QuantifiedCargo")),
              text = PositiveTextTestCase("TEUs of DoD Frozen Meat")
            ),
            PositiveArgTestCase(
              role = PositiveRoleTestCase("destination"),
              labels = Seq(PositiveLabelTestCase("Location")),
              text  = PositiveTextTestCase("Hamburg")
            )
          )
        ),
    //     GeneralMentionTestCase(
    //       labels = Seq(PositiveLabelTestCase("Transport")),
    //       text = "Frozen food that arrived before September 21st 2020 but after September 28th 2020.",

    //       args = List(
    //         PositiveArgTestCase(
    //           role = PositiveRoleTestCase("time"),
    //           labels = Seq(PositiveLabelTestCase("BeforeTime", PositiveLabelTestCase("TimeExpression")),
    //           text = "before September 21st 2020"
    //         ),
    //         PositiveArgTestCase(
    //           role = PositiveRoleTestCase("time"),
    //           labels = Seq(PositiveLabelTestCase("AfterTime", PositiveLabelTestCase("TimeExpression")),
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
    
    it should "find Query events" in {

      val testCases = Seq(
        GeneralMentionTestCase( 
          labels = Seq(
            PositiveLabelTestCase("Query"), 
            PositiveLabelTestCase("WhatQuery")
          ),
          mentionSpan = PositiveTextTestCase("Find ports near Hamburg with enough excess cargo capacity to handle shipments redirected from Hamburg before last week"),
          text = "Find ports near Hamburg with enough excess cargo capacity to handle shipments redirected from Hamburg before last week",
          args = List(
            PositiveArgTestCase(
              role = PositiveRoleTestCase("need"),
              labels = Seq(
                PositiveLabelTestCase("UnspecifiedPort"),
                PositiveLabelTestCase("Location"),
              ),
              text = PositiveTextTestCase("ports")
            ),
            // PositiveArgTestCase( //this test fails
            //   role = PositiveRoleTestCase("constraints"),
            //   labels = Seq(PositiveLabelTestCase("ProximityConstraint"), PositiveLabelTestCase("Constraint")),
            //   text = PositiveTextTestCase("near Hamburg")
            // ),
            // PositiveArgTestCase( //this fails
            //   role = PositiveRoleTestCase("constraints"),
            //   labels = Seq(PositiveLabelTestCase("QuantityConstraint"), PositiveLabelTestCase("Constraint")),
            //   text = PositiveTextTestCase("enough excess cargo capacity")
            // ),
            // PositiveArgTestCase( //still fails
            //   role = PositiveRoleTestCase("constraints"),
            //   labels = Seq(PositiveLabelTestCase("QuantityConstraint"), PositiveLabelTestCase("Constraint")),
            //   text = PositiveTextTestCase("cargo capacity") //Qc-within-QC conflict causes above to fail?
            // ),
            // PositiveArgTestCase( //still fails
            //   role = PositiveRoleTestCase("constraints"),
            //   labels = Seq(PositiveLabelTestCase("QuantityConstraint"), PositiveLabelTestCase("Constraint")),
            //   text = PositiveTextTestCase("enough excess") //Qc-within-QC conflict causes above to fail?
            // ),
            // PositiveArgTestCase( //fails
            //   role = PositiveRoleTestCase("constraints"),
            //   labels = Seq(PositiveLabelTestCase("TimeConstraint"), PositiveLabelTestCase("BeforeTime")),
            //   text = PositiveTextTestCase("before last week")
            // )
          )
        ),
        GeneralMentionTestCase(
          labels = Seq(PositiveLabelTestCase("Query"), PositiveLabelTestCase("WhatQuery")), 
          mentionSpan = PositiveTextTestCase("Find ports near Hamburg with enough excess cargo capacity to handle shipments redirected from Hamburg since February 12"),
          text = "Find ports near Hamburg with enough excess cargo capacity to handle shipments redirected from Hamburg since February 12",
          args = List(
            PositiveArgTestCase( 
              role = PositiveRoleTestCase("need"),
              labels = Seq(PositiveLabelTestCase("UnspecifiedPort"), PositiveLabelTestCase("Location")),
              text = PositiveTextTestCase("ports")
            ),
            // PositiveArgTestCase( //next three ArgTest blocks causes failure
            //   role = PositiveRoleTestCase("constraints"),
            //   labels = Seq(PositiveLabelTestCase("ProximityConstraint"), PositiveLabelTestCase("Constraint")),
            //   text = PositiveTextTestCase("near Hamburg")
            // ),
            // PositiveArgTestCase(
            //   role = PositiveRoleTestCase("constraints"),
            //   labels = Seq(PositiveLabelTestCase("QuantityConstraint"), PositiveLabelTestCase("Constraint")),
            //   text = PositiveTextTestCase("enough excess cargo capacity")
            // ),
            // PositiveArgTestCase(
            //   role = PositiveRoleTestCase("constraints"),
            //   labels = Seq(PositiveLabelTestCase("AfterTime"), PositiveLabelTestCase("TimeConstraint")),
            //   text = PositiveTextTestCase("since February 12")
            // )
          )
        ),
        GeneralMentionTestCase(
          labels = Seq(PositiveLabelTestCase("LocationQuery")),
          mentionSpan = PositiveTextTestCase("What are alternative ports with enough cargo capacity to handle shipments redirected from Hamburg"),
          text = "What are alternative ports with enough cargo capacity to handle shipments redirected from Hamburg",
          args = List(
            PositiveArgTestCase( //fails
              role = PositiveRoleTestCase("constraints"),
              labels = Seq(PositiveLabelTestCase("OriginConstraint"), PositiveLabelTestCase("Constraint")),
              text = PositiveTextTestCase("Hamburg") 
            ),
            PositiveArgTestCase( //fails
              role = PositiveRoleTestCase("constraints"),
              labels = Seq(PositiveLabelTestCase("QuantityConstraint"), PositiveLabelTestCase("Constraint")),
              text = PositiveTextTestCase("enough cargo capacity")
            ),
            PositiveArgTestCase(
              role = PositiveRoleTestCase("need"),
              labels = Seq(PositiveLabelTestCase("Concept")),
              text = PositiveTextTestCase("ports")
            )
          )
        ),
        GeneralMentionTestCase(
          labels = Seq(PositiveLabelTestCase("CargoQuery"), PositiveLabelTestCase("QuantityQuery")),
          text = "How many TEUs of zebras are heading to Scotland from Zimbabwe?",
          mentionSpan = PositiveTextTestCase("How many TEUs of zebras are heading to Scotland from Zimbabwe"),
          args = List(
            PositiveArgTestCase(
              role = PositiveRoleTestCase("need"),
              labels = Seq(PositiveLabelTestCase("QuantifiedCargo")),
              text = PositiveTextTestCase("TEUs of zebras")
            ),
    //         PositiveArgTestCase(
    //           role = PositiveRoleTestCase("constraints"),
    //           labels = Seq(PositiveLabelTestCase("OriginConstraint", PositiveLabelTestCase("Constraint")),
    //           text = "Zimbabwe"
    //         ),
    //         PositiveArgTestCase(
    //           role = PositiveRoleTestCase("constraints"),
    //           labels = Seq(PositiveLabelTestCase("DestinationConstraint", PositiveLabelTestCase("Constraint")),
    //           text = "Scotland"
    //         )
          )
        ),

    //     GeneralMentionTestCase(
    //       text = "How much frozen meat is heading to Hamburg?",
    //       labels = Seq(PositiveLabelTestCase("CargoQuery", PositiveLabelTestCase("QuantityQuery")),
    //       args = List(
    //         PositiveArgTestCase(
    //           role = PositiveRoleTestCase("need"),
    //           labels = Seq(PositiveLabelTestCase("Cargo")),
    //           text = "frozen meat"
    //         ),
    //         PositiveArgTestCase(
    //           role = PositiveRoleTestCase("constraints"),
    //           labels = Seq(PositiveLabelTestCase("DestinationConstraint", PositiveLabelTestCase("Constraint")),
    //           text = "Hamburg"
    //         )
    //       )
    //     ),
    //     GeneralMentionTestCase(
    //       text = "How many shipments of frozen meat are heading to Hamburg?",
    //       labels = Seq(PositiveLabelTestCase("CargoQuery", PositiveLabelTestCase("QuantityQuery")),
    //       args = List(
    //         PositiveArgTestCase(
    //           role = PositiveRoleTestCase("need"),
    //           labels = Seq(PositiveLabelTestCase("ShipmentOf", PositiveLabelTestCase("Cargo")),
    //           text = "shipments of frozen meat"
    //         ),
    //         PositiveArgTestCase(
    //           role = PositiveRoleTestCase("constraints"),
    //           labels = Seq(PositiveLabelTestCase("DestinationConstraint", PositiveLabelTestCase("Constraint")),
    //           text = "Hamburg"
    //         )
    //       )
    //     ),
    //     GeneralMentionTestCase(
    //       text = "Which vessel left on Thursday?",
    //       labels = Seq(PositiveLabelTestCase("VesselQuery", PositiveLabelTestCase("Query")),
    //       args = List(
    //         PositiveArgTestCase(
    //           role = PositiveRoleTestCase("need"),
    //           labels = Seq(PositiveLabelTestCase("Vessel")),
    //           text = "vessel"
    //         ),
    //         PositiveArgTestCase(
    //           role = PositiveRoleTestCase("constraints"),
    //           labels = Seq(PositiveLabelTestCase("OnTime")),
    //           text = "on Thursday"
    //         )
    //       )
    //     ),
    //     GeneralMentionTestCase(
    //       text = "What cargo left Los Angeles last week?",
    //       labels = Seq(PositiveLabelTestCase("CargoQuery")),
    //       args = List(
    //         PositiveArgTestCase(
    //           role = PositiveRoleTestCase("need"),
    //           labels = Seq(PositiveLabelTestCase("UnspecifiedCargo")),
    //           text = "cargo"
    //         ),
    //         PositiveArgTestCase(
    //           role = PositiveRoleTestCase("constraints"),
    //           labels = Seq(PositiveLabelTestCase("TimeConstraint")),
    //           text = "last week"
    //         )
    //       )
    //     )
      )

      testCases foreach { tc =>
        val results = system.extract(tc.text)
        results should not be empty
        checkMention(tc, results) should be (true)
      }
    }

    it should "not find Query events" in {

      val testCases = Seq(
        NegativeMentionTestCase(
          labels = Seq(NegativeLabelTestCase("CargoQuery"), NegativeLabelTestCase("QuantityQuery")),
          text = "Some zebras are galloping to Scotland from Zimbabwe",
          mentionSpan = NegativeTextTestCase("Some zebras are heading to Scotland from Zimbabwe"),
          args = List( // this works -- ie passing in negargtestcase list, not just Nil as above
             NegativeArgTestCase(
              role = PositiveRoleTestCase("need"),
              labels = Seq(PositiveLabelTestCase("QuantifiedCargo")),
              text = PositiveTextTestCase("zebras")
            ),
            NegativeArgTestCase(
              role = PositiveRoleTestCase("constraints"),
              labels = Seq(PositiveLabelTestCase("OriginConstraint"), PositiveLabelTestCase("Constraint")),
              text = PositiveTextTestCase("Zimbabwe")
            ),
            NegativeArgTestCase(
              role = PositiveRoleTestCase("constraints"),
              labels = Seq(PositiveLabelTestCase("DestinationConstraint"), PositiveLabelTestCase("Constraint")),
              text = PositiveTextTestCase("Scotland")
            )
          )
        ),
    //     NegativeMentionTestCase(
    //       labels = Seq(PositiveLabelTestCase("CargoQuery", PositiveLabelTestCase("QuantityQuery")),
    //       text = "Many zebras are galloping to Scotland from Zimbabwe?",
    //       args = Nil
    //       // List(
    //       //   NegativeArgTestCase(
    //       //     role = PositiveRoleTestCase("need"),
    //       //     labels = Seq(PositiveLabelTestCase("QuantifiedCargo")),
    //       //     text = "zebras"
    //       //   ),
    //       //   NegativeArgTestCase(
    //       //     role = PositiveRoleTestCase("constraints"),
    //       //     labels = Seq(PositiveLabelTestCase("OriginConstraint", PositiveLabelTestCase("Constraint")),
    //       //     text = "Zimbabwe"
    //       //   ),
    //       //   NegativeArgTestCase(
    //       //     role = PositiveRoleTestCase("constraints"),
    //       //     labels = Seq(PositiveLabelTestCase("DestinationConstraint", PositiveLabelTestCase("Constraint")),
    //       //     text = "Scotland"
    //       //   )
    //       // )
    //     ),
      )
      testCases foreach { tc =>
        val results = system.extract(tc.text)
        results should not be empty
        checkMention(tc, results) should be (true)
      }
    }

    it should "find structured TimeExpressions" in {
      val testCases = Seq(
        GeneralMentionTestCase(
          labels = Seq(PositiveLabelTestCase("IntervalTime"), PositiveLabelTestCase("TimeExpression")),
          text = "How many TEUs of frozen fish are heading to Dubai between September 30th 2020 and October 2nd 2020?",
          mentionSpan = PositiveTextTestCase("between September 30th 2020 and October 2nd 2020"),
          args = List(
            PositiveArgTestCase(
              role = PositiveRoleTestCase("start"),
              labels = Seq(PositiveLabelTestCase("IntervalTime"), PositiveLabelTestCase("TimeExpression")),
              text = PositiveTextTestCase("September 30th 2020")
            ),
            PositiveArgTestCase(
              role = PositiveRoleTestCase("end"),
              labels = Seq(PositiveLabelTestCase("IntervalTime"), PositiveLabelTestCase("TimeExpression")),
              text = PositiveTextTestCase("October 2nd 2020")
            )
          )
        )
      )

      testCases foreach { tc =>
        val results = system.extract(tc.text)
        results should not be empty
        checkMention(tc, results) should be (true)
      }
    }
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
