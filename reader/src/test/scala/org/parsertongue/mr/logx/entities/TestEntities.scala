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
      ),
      MentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"),
          PositiveLabelTestCase("AfterTime")
        ),
        mentionSpan = PositiveMentionTextTestCase("after August 24, 2020"),
        text = "after August 24, 2020" 
      ),
      MentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("BeforeTime")
        ),
        mentionSpan = PositiveMentionTextTestCase("by August 24th 2020"),
        text = "by August 24th 2020" 
      ),
      MentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("IntervalTime")
        ),
        mentionSpan = PositiveMentionTextTestCase("from August 12, 2020 to August 19, 2020"),
        text = "from August 12, 2020 to August 19, 2020" 
      ),
      // // EntityTestCase(
      // //  labels = Seq(PositiveLabelTestCase("TimeExpression"), "IntervalTimeExpression"),
      // //  text = "during the week of October 12"
      // // ),
      MentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("IntervalTime")
        ),
        mentionSpan = PositiveMentionTextTestCase("during the week"),
        text = "during the week"
      ),
      MentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("IntervalTime")
        ),
        mentionSpan = PositiveMentionTextTestCase("throughout 1991"),
        text = "throughout 1991"
      ),
      MentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("TimeUnit")
        ),
        mentionSpan = PositiveMentionTextTestCase("the next few days"),
        text = "the next few days" 
      ),
      MentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("Date")
        ),
        mentionSpan = PositiveMentionTextTestCase("August 2020"),
        text = "August 2020" 
      ),
      MentionTestCase(
        labels = Seq(PositiveLabelTestCase("TimeExpression"), 
        PositiveLabelTestCase("Date")
        ),
        mentionSpan = PositiveMentionTextTestCase("August 12 2020"),
        text = "August 12 2020" 
      ),
      MentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("Date")
        ),
        mentionSpan = PositiveMentionTextTestCase("12/02/1986"),
        text = "12/02/1986" 
      ),
      MentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("Date")
        ),
        mentionSpan = PositiveMentionTextTestCase("05/1986"),
        text = "05/1986" 
      ),
      MentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("Date")
        ),
        mentionSpan = PositiveMentionTextTestCase("05/86"),
        text = "05/86" 
      ),
      MentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("Date")
        ),
        mentionSpan = PositiveMentionTextTestCase("1986-12-21"),
        text = "1986-12-21" 
      ),
      MentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("Date")
        ),
        mentionSpan = PositiveMentionTextTestCase("2012 11 30"),
        text = "2012 11 30" 
      ),
      MentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("Date")
        ),
        mentionSpan = PositiveMentionTextTestCase("12 JUN 2021"),
        text = "12 JUN 2021" 
      ),
      MentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("Date")
        ),
        mentionSpan = PositiveMentionTextTestCase("12 Jun 2021"),
        text = "12 Jun 2021" 
      ),
      MentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("Date")
        ),
        mentionSpan = PositiveMentionTextTestCase("12-Jun-2021"),
        text = "12-Jun-2021" 
      ),
      // // NegativeEntityTestCase(
      // //   labels = Seq(PositiveLabelTestCase("TimeExpression"), "AfterTime"),
      // //   text = "June 12 2100" //wrong behavior: this passes as tho postestcase. segregate in own "should not find mentions" block?
      // // ),
      MentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("IntervalTime")
        ),
        mentionSpan = PositiveMentionTextTestCase("During the week of October 12"),
        text = "During the week of October 12" 
      ),
      MentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"),
          PositiveLabelTestCase("TimeUnit")
        ),
        mentionSpan = PositiveMentionTextTestCase("The week ending October 12th"),
        text = "The week ending October 12th" 
      )
    )

    testCases foreach { tc =>
      val results = system.extract(tc.text)
      results should not be empty
      checkMention(tc, results) should be (true)
    }  
  }

  it should "identify Vessel mentions" in {

    val testCases = Seq(
      MentionTestCase(
        labels = Seq(PositiveLabelTestCase("Vessel")),
        mentionSpan = PositiveMentionTextTestCase("cargo vessel"),
        text = "cargo vessel"
      ),
      MentionTestCase(
        labels = Seq(PositiveLabelTestCase("Vessel")),
        mentionSpan = PositiveMentionTextTestCase("SS Bandirma"),
        text = "SS Bandirma"
      ),
      MentionTestCase(
        labels = Seq(PositiveLabelTestCase("Vessel")),
        mentionSpan = PositiveMentionTextTestCase("MV Colombo Express"),
        text = "MV Colombo Express"
      ),
      MentionTestCase(
        labels = Seq(PositiveLabelTestCase("Vessel")),
        mentionSpan = PositiveMentionTextTestCase("CMA CGM Thalassa"),
        text = "CMA CGM Thalassa"
      ),
      MentionTestCase(
        labels = Seq(PositiveLabelTestCase("Vessel")),
        mentionSpan = PositiveMentionTextTestCase("LNG Finima"),
        text = "LNG Finima"
      )
    )

    testCases foreach { tc =>
      val results = system.extract(tc.text)
      results should not be empty
      checkMention(tc, results) should be (true)
    }  
  }

  it should "not identify Cargo mentions" in {

    val testCases = Seq(
      // MentionTestCase(
      //   labels = Seq(PositiveLabelTestCase("QuantifiedCargo")),
      //   mentionSpan = PositiveMentionTextTestCase("TEUs of ostrich feathers"),
      //   text = "TEUs of ostrich feathers"
      // ),
      MentionTestCase(
        labels = Seq(NegativeLabelTestCase("QuantifiedCargo")),
        mentionSpan = NegativeMentionTextTestCase("TUs of ostrich feathers"),
        text = "TUs of ostrich feathers"
      ),
      MentionTestCase(
        labels = Seq(NegativeLabelTestCase("QuantifiedCargo")),
        mentionSpan = NegativeMentionTextTestCase("of ostrich feathers"),
        text = "of ostrich feathers"
      ),
      // MentionTestCase(
      //   labels = Seq(PositiveLabelTestCase("QuantifiedCargo")),
      //   mentionSpan = PositiveMentionTextTestCase("Metric tons of preserved duck eggs"),
      //   text = "Metric tons of preserved duck eggs"
      // ),
      MentionTestCase(
        labels = Seq(NegativeLabelTestCase("QuantifiedCargo")),
        mentionSpan = NegativeMentionTextTestCase("TEUs of ostrich feathers"), //this should cause fail
        text = "of ostrich feathers"
      ),
    )

    testCases foreach { tc =>
      val results = system.extract(tc.text)
      results should not be empty
      checkMention(tc, results) should be (true)
    }  
  }


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
