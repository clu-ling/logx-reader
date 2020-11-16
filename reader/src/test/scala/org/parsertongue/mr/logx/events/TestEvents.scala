package org.parsertongue.mr.logx.events

import org.parsertongue.mr.TestUtils._
import org.scalatest.{ FlatSpec, Matchers }


class TestEvents extends FlatSpec with Matchers {

    "MachineReadingSystem" should "find Transport events" in {

      val testCases = Seq(
        ExistsMentionTestCase(
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
        ExistsMentionTestCase(
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
            // deprecated next five blocks by exists-requires-pos-arg-tests, 11-15
            // NegativeArgTestCase( //This behaves as desired. passes because pos subtest fails
            //   role = PositiveRoleTestCase("shipment"), 
            //   labels = Seq(PositiveLabelTestCase("Cargo")),
            //   text = PositiveTextTestCase("F16") //note, missing ' engines'
            // ),
            // NegativeArgTestCase( //This behaves as desired. passes because neg subtest fails
            //   role = PositiveRoleTestCase("shipment"), 
            //   labels = Seq(PositiveLabelTestCase("Cargo")),
            //   text = NegativeTextTestCase("F16 engines") //right text, makes false.
            // ),
            // NegativeArgTestCase( // trying: negative argtestcase within positive event
            //   role = PositiveRoleTestCase("destination"),
            //   labels = Seq(PositiveLabelTestCase("Location")),
            //   text  = PositiveTextTestCase("Tucson") // correct role and label, wrong text
            // ),
            // NegativeArgTestCase( // trying: negative argtestcase within positive event
            //   role = PositiveRoleTestCase("destination"),
            //   labels = Seq(PositiveLabelTestCase("Unit")), // wrong label only
            //   text  = PositiveTextTestCase("Dubai")
            // ),
            // NegativeArgTestCase( // trying: negative argtestcase within positive event
            //   role = PositiveRoleTestCase("shipment"), // wrong role
            //   labels = Seq(PositiveLabelTestCase("Location")),
            //   text  = PositiveTextTestCase("Dubai")
            // ),
            // NegativeArgTestCase( // trying: negative argtestcase within positive event
            //   role = PositiveRoleTestCase("destination"),  //feeding correct everything; should fail. it does.
            //   labels = Seq(PositiveLabelTestCase("Location")),
            //   text  = PositiveTextTestCase("Dubai")
            // )
          )
        ),
        ExistsMentionTestCase(
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
        ExistsMentionTestCase(
          labels = Seq(PositiveLabelTestCase("Transport")),
          mentionSpan = PositiveTextTestCase("Frozen food that arrived before September 21st 2020 but after September 28th 2020"),
          text = "Frozen food that arrived before September 21st 2020 but after September 28th 2020.",
          args = List(
            PositiveArgTestCase(
              role = PositiveRoleTestCase("time"),
              labels = Seq(PositiveLabelTestCase("BeforeTime"), PositiveLabelTestCase("TimeExpression")),
              text = PositiveTextTestCase("before September 21st 2020")
            ),
            PositiveArgTestCase(
              role = PositiveRoleTestCase("time"),
              labels = Seq(PositiveLabelTestCase("AfterTime"), PositiveLabelTestCase("TimeExpression")),
              text = PositiveTextTestCase("after September 28th 2020")
            )
          )
        ),
        // ForAllMentionTestCase( //just added; identical to above except neg/gen mention. This fails, as desired.
        //   labels = Seq(PositiveLabelTestCase("Transport")),
        //   mentionSpan = PositiveTextTestCase("Frozen food that arrived before September 21st 2020 but after September 28th 2020"),
        //   text = "Frozen food that arrived before September 21st 2020 but after September 28th 2020.",
        //   args = List(
        //     PositiveArgTestCase(
        //       role = PositiveRoleTestCase("time"),
        //       labels = Seq(PositiveLabelTestCase("BeforeTime"), PositiveLabelTestCase("TimeExpression")),
        //       text = PositiveTextTestCase("before September 21st 2020")
        //     ),
        //     PositiveArgTestCase(
        //       role = PositiveRoleTestCase("time"),
        //       labels = Seq(PositiveLabelTestCase("AfterTime"), PositiveLabelTestCase("TimeExpression")),
        //       text = PositiveTextTestCase("after September 28th 2020")
        //     )
        //   )
        // )
      )

    //   // TODO: load AnnotatedDocument JSON from resources.
    //   //val doc: AnnotatedDocument

      testCases foreach { tc =>
        val results = system.extract(tc.text)
        results should not be empty
        checkMention(tc, results) should be (true)
      }
    }
    
    // trying: neg test that fails in above block; try embed under should-not-find?
    // it should "not find Transport events" in {

    //   val testCases = Seq(
    //     ForAllMentionTestCase( //just added; identical to above except neg/gen mention. This fails, as desired.
    //       labels = Seq(PositiveLabelTestCase("Transport")),
    //       mentionSpan = PositiveTextTestCase("Frozen food that arrived before September 21st 2020 but after September 28th 2020"),
    //       text = "Frozen food that arrived before September 21st 2020 but after September 28th 2020.",
    //       args = List(
    //         PositiveArgTestCase(
    //           role = PositiveRoleTestCase("time"),
    //           labels = Seq(PositiveLabelTestCase("BeforeTime"), PositiveLabelTestCase("TimeExpression")),
    //           text = PositiveTextTestCase("before September 21st 2020")
    //         ),
    //         PositiveArgTestCase(
    //           role = PositiveRoleTestCase("time"),
    //           labels = Seq(PositiveLabelTestCase("AfterTime"), PositiveLabelTestCase("TimeExpression")),
    //           text = PositiveTextTestCase("after September 28th 2020")
    //         )
    //       )
    //     )
    //   )

    //   testCases foreach { tc =>
    //     val results = system.extract(tc.text)
    //     results should not be empty
    //     checkMention(tc, results) should be (true)
    //   }
    // }

    it should "find Query events" in {

      val testCases = Seq(
        ExistsMentionTestCase( 
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
            PositiveArgTestCase( 
              role = PositiveRoleTestCase("constraints"),
              labels = Seq(PositiveLabelTestCase("ProximityConstraint"), PositiveLabelTestCase("Constraint")),
              text = PositiveTextTestCase("near Hamburg")
            ),
            PositiveArgTestCase( 
              role = PositiveRoleTestCase("constraints"),
              labels = Seq(PositiveLabelTestCase("QuantityConstraint"), PositiveLabelTestCase("Constraint")),
              text = PositiveTextTestCase("enough excess cargo capacity")
            ),
            // PositiveArgTestCase( // fails
            //   role = PositiveRoleTestCase("constraints"),
            //   labels = Seq(PositiveLabelTestCase("QuantityConstraint"), PositiveLabelTestCase("Constraint")),
            //   text = PositiveTextTestCase("cargo capacity") //QC-within-QC conflict causes above to fail?
            // ),
            // PositiveArgTestCase( // fails
            //   role = PositiveRoleTestCase("constraints"),
            //   labels = Seq(PositiveLabelTestCase("QuantityConstraint"), PositiveLabelTestCase("Constraint")),
            //   text = PositiveTextTestCase("enough excess") //QC-within-QC conflict causes above to fail?
            // ),
            PositiveArgTestCase( 
              role = PositiveRoleTestCase("constraints"),
              labels = Seq(PositiveLabelTestCase("TimeConstraint"), PositiveLabelTestCase("BeforeTime")),
              text = PositiveTextTestCase("before last week")
            )
          )
        ),
        ExistsMentionTestCase(
          labels = Seq(PositiveLabelTestCase("Query"), PositiveLabelTestCase("WhatQuery")), 
          mentionSpan = PositiveTextTestCase("Find ports near Hamburg with enough excess cargo capacity to handle shipments redirected from Hamburg since February 12"),
          text = "Find ports near Hamburg with enough excess cargo capacity to handle shipments redirected from Hamburg since February 12",
          args = List(
            PositiveArgTestCase( 
              role = PositiveRoleTestCase("need"),
              labels = Seq(PositiveLabelTestCase("UnspecifiedPort"), PositiveLabelTestCase("Location")),
              text = PositiveTextTestCase("ports")
            ),
            PositiveArgTestCase(
              role = PositiveRoleTestCase("constraints"),
              labels = Seq(PositiveLabelTestCase("ProximityConstraint"), PositiveLabelTestCase("Constraint")),
              text = PositiveTextTestCase("near Hamburg")
            ),
            PositiveArgTestCase(
              role = PositiveRoleTestCase("constraints"),
              labels = Seq(PositiveLabelTestCase("QuantityConstraint"), PositiveLabelTestCase("Constraint")),
              text = PositiveTextTestCase("enough excess cargo capacity")
            ),
            PositiveArgTestCase(
              role = PositiveRoleTestCase("constraints"),
              labels = Seq(PositiveLabelTestCase("AfterTime"), PositiveLabelTestCase("TimeConstraint")),
              text = PositiveTextTestCase("since February 12")
            )
          )
        ),
        ExistsMentionTestCase(
          labels = Seq(PositiveLabelTestCase("LocationQuery")),
          mentionSpan = PositiveTextTestCase("What are alternative ports with enough cargo capacity to handle shipments redirected from Hamburg"),
          text = "What are alternative ports with enough cargo capacity to handle shipments redirected from Hamburg",
          args = List(
            PositiveArgTestCase( 
              role = PositiveRoleTestCase("constraints"),
              labels = Seq(PositiveLabelTestCase("OriginConstraint"), PositiveLabelTestCase("Constraint")),
              text = PositiveTextTestCase("Hamburg") 
            ),
            PositiveArgTestCase( 
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
        ExistsMentionTestCase(
          labels = Seq(PositiveLabelTestCase("CargoQuery"), PositiveLabelTestCase("QuantityQuery")),
          text = "How many TEUs of zebras are heading to Scotland from Zimbabwe?",
          mentionSpan = PositiveTextTestCase("How many TEUs of zebras are heading to Scotland from Zimbabwe"),
          args = List(
            PositiveArgTestCase(
              role = PositiveRoleTestCase("need"),
              labels = Seq(PositiveLabelTestCase("QuantifiedCargo")),
              text = PositiveTextTestCase("TEUs of zebras")
            ),
            PositiveArgTestCase(
              role = PositiveRoleTestCase("constraints"),
              labels = Seq(PositiveLabelTestCase("OriginConstraint"), PositiveLabelTestCase("Constraint")),
              text = PositiveTextTestCase("Zimbabwe")
            ),
            PositiveArgTestCase(
              role = PositiveRoleTestCase("constraints"),
              labels = Seq(PositiveLabelTestCase("DestinationConstraint"), PositiveLabelTestCase("Constraint")),
              text = PositiveTextTestCase("Scotland")
            )
          )
        ),
        ExistsMentionTestCase(
          text = "How much frozen meat is heading to Hamburg?",
          labels = Seq(PositiveLabelTestCase("CargoQuery"), PositiveLabelTestCase("QuantityQuery")),
          mentionSpan = PositiveTextTestCase("How much frozen meat is heading to Hamburg"),
          args = List(
            PositiveArgTestCase(
              role = PositiveRoleTestCase("need"),
              labels = Seq(PositiveLabelTestCase("Cargo")),
              text = PositiveTextTestCase("frozen meat")
            ),
            PositiveArgTestCase(
              role = PositiveRoleTestCase("constraints"),
              labels = Seq(PositiveLabelTestCase("DestinationConstraint"), PositiveLabelTestCase("Constraint")),
              text = PositiveTextTestCase("Hamburg")
            )
          )
        ),
        ExistsMentionTestCase(
          text = "How many shipments of frozen meat are heading to Hamburg?",
          labels = Seq(PositiveLabelTestCase("CargoQuery"), PositiveLabelTestCase("QuantityQuery")),
          mentionSpan = PositiveTextTestCase("How many shipments of frozen meat are heading to Hamburg"),
          args = List(
            PositiveArgTestCase(
              role = PositiveRoleTestCase("need"),
              labels = Seq(PositiveLabelTestCase("ShipmentOf"), PositiveLabelTestCase("Cargo")),
              text = PositiveTextTestCase("shipments of frozen meat")
            ),
            PositiveArgTestCase(
              role = PositiveRoleTestCase("constraints"),
              labels = Seq(PositiveLabelTestCase("DestinationConstraint"), PositiveLabelTestCase("Constraint")),
              text = PositiveTextTestCase("Hamburg")
            )
          )
        ),
        ExistsMentionTestCase(
          text = "Which vessel left on Thursday?",
          labels = Seq(PositiveLabelTestCase("VesselQuery"), PositiveLabelTestCase("Query")),
          mentionSpan = PositiveTextTestCase("Which vessel left on Thursday"),
          args = List(
            PositiveArgTestCase(
              role = PositiveRoleTestCase("need"),
              labels = Seq(PositiveLabelTestCase("Vessel")),
              text = PositiveTextTestCase("vessel")
            ),
            PositiveArgTestCase(
              role = PositiveRoleTestCase("constraints"),
              labels = Seq(PositiveLabelTestCase("OnTime")),
              text = PositiveTextTestCase("on Thursday")
            )
          )
        ),
        ExistsMentionTestCase(
          text = "What cargo left Los Angeles last week?",
          labels = Seq(PositiveLabelTestCase("CargoQuery")),
          mentionSpan = PositiveTextTestCase("What cargo left Los Angeles last week"),
          args = List(
            PositiveArgTestCase(
              role = PositiveRoleTestCase("need"),
              labels = Seq(PositiveLabelTestCase("UnspecifiedCargo")),
              text = PositiveTextTestCase("cargo")
            ),
            PositiveArgTestCase(
              role = PositiveRoleTestCase("constraints"),
              labels = Seq(PositiveLabelTestCase("TimeConstraint")),
              text = PositiveTextTestCase("last week")
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

    it should "not find Query events" in {

      val testCases = Seq(
        ForAllMentionTestCase(
          labels = Seq(NegativeLabelTestCase("CargoQuery"), NegativeLabelTestCase("QuantityQuery")),
          text = "Some zebras are galloping to Scotland from Zimbabwe",
          mentionSpan = NegativeTextTestCase("Some zebras are heading to Scotland from Zimbabwe"),
          args = List( 
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
        // ForAllMentionTestCase(
        //   labels = Seq(PositiveLabelTestCase("CargoQuery"), PositiveLabelTestCase("QuantityQuery")),
        //   text = "Many zebras are galloping to Scotland from Zimbabwe?",
        //   mentionSpan = PositiveTextTestCase("tomfoolery") //block to here fails
    //       args = Nil
    //       // List(
    //       //   NegativeArgTestCase(
    //       //     role = PositiveRoleTestCase("need"),
    //       //     labels = Seq(PositiveLabelTestCase("QuantifiedCargo")),
    //       //     text = PositiveTextTestCase("zebras")
    //       //   ),
    //       //   NegativeArgTestCase(
    //       //     role = PositiveRoleTestCase("constraints"),
    //       //     labels = Seq(PositiveLabelTestCase("OriginConstraint"), PositiveLabelTestCase("Constraint")),
    //       //     text = PositiveTextTestCase("Zimbabwe")
    //       //   ),
    //       //   NegativeArgTestCase(
    //       //     role = PositiveRoleTestCase("constraints"),
    //       //     labels = Seq(PositiveLabelTestCase("DestinationConstraint"), PositiveLabelTestCase("Constraint")),
    //       //     text = PositiveTextTestCase("Scotland")
    //       //   )
    //       // )
        // ),
        ForAllMentionTestCase(
          labels = Seq(NegativeLabelTestCase("CargoQuery"), NegativeLabelTestCase("QuantityQuery")),
          text = "Many zebras are galloping to Scotland from Zimbabwe?",
          mentionSpan = NegativeTextTestCase("tomfoolery") //expect pass by parallel w above
        ),
        ExistsMentionTestCase( //note: identical tests to NegMention above; both pass
          labels = Seq(NegativeLabelTestCase("CargoQuery"), NegativeLabelTestCase("QuantityQuery")),
          text = "Many zebras are galloping to Scotland from Zimbabwe?",
          mentionSpan = NegativeTextTestCase("tomfoolery") 
        ),
        // ForAllMentionTestCase(// NB: just one failing pos-label test. this mention test fails.
        //   labels = Seq(NegativeLabelTestCase("CargoQuery"), PositiveLabelTestCase("QuantityQuery")), 
        //   text = "Many zebras are galloping to Scotland from Zimbabwe?",
        //   mentionSpan = NegativeTextTestCase("tomfoolery")
        // ),
        // ForAllMentionTestCase(// NB: just one failing pos-text test. this mention test fails.
        //   labels = Seq(NegativeLabelTestCase("CargoQuery"), NegativeLabelTestCase("QuantityQuery")), 
        //   text = "Many zebras are galloping to Scotland from Zimbabwe?",
        //   mentionSpan = PositiveTextTestCase("tomfoolery")
        // ),
        // ExistsMentionTestCase( //note: identical tests to NegMention above; both pass
        //   labels = Seq(NegativeLabelTestCase("CargoQuery"), NegativeLabelTestCase("QuantityQuery")),
        //   text = "Many zebras are galloping to Scotland from Zimbabwe?",
        //   mentionSpan = NegativeTextTestCase("tomfoolery") 
        // ),
      )
      testCases foreach { tc =>
        val results = system.extract(tc.text)
        results should not be empty
        checkMention(tc, results) should be (true)
      }
    }

    it should "find structured TimeExpressions" in {
      val testCases = Seq(
        ExistsMentionTestCase(
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
        ),
        // ForAllMentionTestCase(//added to test failure output
        //   labels = Seq(PositiveLabelTestCase("IntervalTime"), PositiveLabelTestCase("TimeExpression")),
        //   text = "How many TEUs of frozen fish are heading to Dubai between September 30th 2020 and October 2nd 2020?",
        //   mentionSpan = PositiveTextTestCase("between September 30th 2020 and October 2nd 2020"),
        //   args = List(
        //     PositiveArgTestCase(
        //       role = PositiveRoleTestCase("start"),
        //       labels = Seq(PositiveLabelTestCase("IntervalTime"), PositiveLabelTestCase("TimeExpression")),
        //       text = PositiveTextTestCase("September 30th 2020")
        //     ),
        //     PositiveArgTestCase(
        //       role = PositiveRoleTestCase("end"),
        //       labels = Seq(PositiveLabelTestCase("IntervalTime"), PositiveLabelTestCase("TimeExpression")),
        //       text = PositiveTextTestCase("October 2nd 2020")
        //     )
        //   )
        // )
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
