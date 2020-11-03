package org.parsertongue.mr.logx.entities

//import TestUtils._
import org.parsertongue.mr.TestUtils._
import org.scalatest.{ FlatSpec, Matchers }


class TestEntities extends FlatSpec with Matchers {
    
  "LogX MachineReadingSystem" should "identify TimeExpression mentions" in {

    val testCases = Seq(
      MentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression")
        ),
        mentionSpan = PositiveMentionTextTestCase("August 24th 2020"),
        text = "August 24th 2020"
      ),
      MentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"),
          PositiveLabelTestCase("OnTime")
        ),
        mentionSpan = PositiveMentionTextTestCase("on August 24th 2020"),
        text = "on August 24th 2020" 
      ) // ,
      // MentionTestCase(
      //   labels = Seq(PositiveLabelTestCase("TimeExpression"), "AfterTime"),
      //   text = "after August 24, 2020" 
      // ),
      // MentionTestCase(
      //   labels = Seq(PositiveLabelTestCase("TimeExpression"), "BeforeTime"),
      //   text = "by August 24th 2020" 
      // ),
      // MentionTestCase(
      //   labels = Seq(PositiveLabelTestCase("TimeExpression"), "IntervalTime"),
      //   text = "from August 12, 2020 to August 19, 2020" 
      // ),
      // // EntityTestCase(
      // //  labels = Seq(PositiveLabelTestCase("TimeExpression"), "IntervalTimeExpression"),
      // //  text = "during the week of October 12"
      // // ),
      // MentionTestCase(
      //   labels = Seq(PositiveLabelTestCase("TimeExpression"), "IntervalTime"),
      //   text = "during the week"
      // ),
      // MentionTestCase(
      //   labels = Seq(PositiveLabelTestCase("TimeExpression"), "IntervalTime"),
      //   text = "throughout 1991"
      // ),
      // MentionTestCase(
      //   labels = Seq(PositiveLabelTestCase("TimeExpression"), "TimeUnit"),
      //   text = "the next few days" 
      // ),
      // MentionTestCase(
      //   labels = Seq(PositiveLabelTestCase("TimeExpression"), "Date"),
      //   text = "August 2020" 
      // ),
      // MentionTestCase(
      //   labels = Seq(PositiveLabelTestCase("TimeExpression"), "Date"),
      //   text = "August 12 2020" 
      // ),
      // MentionTestCase(
      //   labels = Seq(PositiveLabelTestCase("TimeExpression"), "Date"),
      //   text = "12/02/1986" 
      // ),
      // MentionTestCase(
      //   labels = Seq(PositiveLabelTestCase("TimeExpression"), "Date"),
      //   text = "05/1986" 
      // ),
      // MentionTestCase(
      //   labels = Seq(PositiveLabelTestCase("TimeExpression"), "Date"),
      //   text = "05/86" 
      // ),
      // MentionTestCase(
      //   labels = Seq(PositiveLabelTestCase("TimeExpression"), "Date"),
      //   text = "1986-12-21" 
      // ),
      // MentionTestCase(
      //   labels = Seq(PositiveLabelTestCase("TimeExpression"), "Date"),
      //   text = "2012 11 30" 
      // ),
      // MentionTestCase(
      //   labels = Seq(PositiveLabelTestCase("TimeExpression"), "Date"),
      //   text = "12 JUN 2021" 
      // ),
      // MentionTestCase(
      //   labels = Seq(PositiveLabelTestCase("TimeExpression"), "Date"),
      //   text = "12 Jun 2021" 
      // ),
      // MentionTestCase(
      //   labels = Seq(PositiveLabelTestCase("TimeExpression"), "Date"),
      //   text = "12-Jun-2021" 
      // ),
      // // NegativeEntityTestCase(
      // //   labels = Seq(PositiveLabelTestCase("TimeExpression"), "AfterTime"),
      // //   text = "June 12 2100" //wrong behavior: this passes as tho postestcase. segregate in own "should not find mentions" block?
      // // ),
      // //MentionTestCase(
      // //  labels = Seq(PositiveLabelTestCase("TimeExpression"), "IntervalTime"),
      // //  text = "During the week of October 12" 
      // //),
      // //MentionTestCase(
      // //  labels = Seq(PositiveLabelTestCase("TimeExpression"), "IntervalTime"),
      // //  text = "During the week of October 12th" 
      // //),
      // MentionTestCase(
      //   labels = Seq(PositiveLabelTestCase("TimeExpression"), "TimeUnit"),
      //   text = "The week ending October 12th" 
      // )
    )

    testCases foreach { tc =>
      val results = system.extract(tc.text)
      results should not be empty
      checkMention(tc, results) should be (true)
    }  
  }

  // it should "identify Vessel mentions" in {

  //   val testCases = Seq(
  //     MentionTestCase(
  //       labels = Seq("Vessel"),
  //       text = "cargo vessel"
  //     ),
  //     MentionTestCase(
  //       labels = Seq("Vessel"),
  //       text = "SS Bandirma"
  //     ),
  //     MentionTestCase(
  //       labels = Seq("Vessel"),
  //       text = "MV Colombo Express"
  //     ),
  //     MentionTestCase(
  //       labels = Seq("Vessel"),
  //       text = "CMA CGM Thalassa"
  //     ),
  //     MentionTestCase(
  //       labels = Seq("Vessel"),
  //       text = "LNG Finima"
  //     )
  //   )

  //   testCases foreach { tc =>
  //     val results = system.extract(tc.text)
  //     results should not be empty
  //     checkEntity(tc, results) should be (true)
  //   }  
  // }

  // it should "identify Cargo mentions" in {

  //   val testCases = Seq(
  //     MentionTestCase(
  //       labels = Seq("QuantifiedCargo"),
  //       text = "TEUs of ostrich feathers"
  //     ),
  //     NegativeEntityTestCase(
  //       labels = Seq("QuantifiedCargo"),
  //       text = "TUs of ostrich feathers"
  //     ),
  //     NegativeEntityTestCase(
  //       labels = Seq("QuantifiedCargo"),
  //       text = "of ostrich feathers"
  //     ),
  //     MentionTestCase(
  //       labels = Seq("QuantifiedCargo"),
  //       text = "Metric tons of preserved duck eggs"
  //     )
  //   )

  //   testCases foreach { tc =>
  //     val results = system.extract(tc.text)
  //     results should not be empty
  //     checkEntity(tc, results) should be (true)
  //   }  
  // }


  // // it should "identify Concept mentions" in {

  // //   val testCases = Seq(
  // //     MentionTestCase(
  // //       labels = Seq("Vessel"),
  // //       text = "SS Bandirma"
  // //     ),
  // //     MentionTestCase(
  // //       labels = Seq("Vessel"),
  // //       text = "MV Colombo Express"
  // //     ),
  // //     MentionTestCase(
  // //       labels = Seq("Vessel"),
  // //       text = "CMA CGM Thalassa"
  // //     ),
  // //     MentionTestCase(
  // //       labels = Seq("Vessel"),
  // //       text = "LNG Finima"
  // //     )
  // //   )

  // //   testCases foreach { tc =>
  // //     val results = system.extract(tc.text)
  // //     results should not be empty
  // //     checkEntity(tc, results) should be (true)
  // //   }  
  // // }


  // it should "not identify Cargo mentions" in {

  //   val testCases = Seq(
  //     NegativeEntityTestCase(
  //       labels = Seq("QuantifiedCargo"),
  //       text = "of ostrich feathers"
  //     ),
  //     NegativeEntityTestCase(
  //       labels = Seq("QuantifiedCargo"),
  //       text = "TUs of ostrich feathers"
  //     ),
  //     // NegativeEntityTestCase(
  //     //   labels = Seq("Vessel"),
  //     //   text = "week"
  //     // ),
  //     NegativeEntityTestCase(
  //       labels = Seq("QuantifiedCargo"),
  //       text = "despite metric tons of preserved duck eggs"
  //     )
  //   )

  //   testCases foreach { tc =>
  //     val results = system.extract(tc.text)
  //     results should not be empty
  //     checkEntity(tc, results) should be (true)
  //   }  
  // }
}

//     val text2 = "What is the probability that Hamburgers and Mustard will be in the same container in a port in Germany?"
