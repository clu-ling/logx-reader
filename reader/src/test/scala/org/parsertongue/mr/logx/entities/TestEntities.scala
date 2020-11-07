package org.parsertongue.mr.logx.entities

//import TestUtils._
import org.parsertongue.mr.TestUtils._
import org.scalatest.{ FlatSpec, Matchers }


class TestEntities extends FlatSpec with Matchers {
    
  "LogX MachineReadingSystem" should "identify TimeExpression mentions" in {

    val testCases = Seq(
      GeneralMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression")
        ),
        mentionSpan = PositiveTextTestCase("August 24th 2020"),
        text = "August 24th 2020"
      ),
      GeneralMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"),
          PositiveLabelTestCase("OnTime")
        ),
        mentionSpan = PositiveTextTestCase("on August 24th 2020"),
        text = "on August 24th 2020" 
      ),
      GeneralMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"),
          PositiveLabelTestCase("AfterTime")
        ),
        mentionSpan = PositiveTextTestCase("after August 24, 2020"),
        text = "after August 24, 2020" 
      ),
      GeneralMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("BeforeTime")
        ),
        mentionSpan = PositiveTextTestCase("by August 24th 2020"),
        text = "by August 24th 2020" 
      ),
      GeneralMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("IntervalTime")
        ),
        mentionSpan = PositiveTextTestCase("from August 12, 2020 to August 19, 2020"),
        text = "from August 12, 2020 to August 19, 2020" 
      ),
      GeneralMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("IntervalTime")
        ),
        mentionSpan = PositiveTextTestCase("during the week"),
        text = "during the week"
      ),
      GeneralMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("IntervalTime")
        ),
        mentionSpan = PositiveTextTestCase("throughout 1991"),
        text = "throughout 1991"
      ),
      GeneralMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("TimeUnit")
        ),
        mentionSpan = PositiveTextTestCase("the next few days"),
        text = "the next few days" 
      ),
      GeneralMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("Date")
        ),
        mentionSpan = PositiveTextTestCase("August 2020"),
        text = "August 2020" 
      ),
      GeneralMentionTestCase(
        labels = Seq(PositiveLabelTestCase("TimeExpression"), 
        PositiveLabelTestCase("Date")
        ),
        mentionSpan = PositiveTextTestCase("August 12 2020"),
        text = "August 12 2020" 
      ),
      GeneralMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("Date")
        ),
        mentionSpan = PositiveTextTestCase("12/02/1986"),
        text = "12/02/1986" 
      ),
      GeneralMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("Date")
        ),
        mentionSpan = PositiveTextTestCase("05/1986"),
        text = "05/1986" 
      ),
      GeneralMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("Date")
        ),
        mentionSpan = PositiveTextTestCase("05/86"),
        text = "05/86" 
      ),
      GeneralMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("Date")
        ),
        mentionSpan = PositiveTextTestCase("1986-12-21"),
        text = "1986-12-21" 
      ),
      GeneralMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("Date")
        ),
        mentionSpan = PositiveTextTestCase("2012 11 30"),
        text = "2012 11 30" 
      ),
      GeneralMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("Date")
        ),
        mentionSpan = PositiveTextTestCase("12 JUN 2021"),
        text = "12 JUN 2021" 
      ),
      GeneralMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("Date")
        ),
        mentionSpan = PositiveTextTestCase("12 Jun 2021"),
        text = "12 Jun 2021" 
      ),
      GeneralMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("Date")
        ),
        mentionSpan = PositiveTextTestCase("12-Jun-2021"),
        text = "12-Jun-2021" 
      ),
      GeneralMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("IntervalTime")
        ),
        mentionSpan = PositiveTextTestCase("During the week of October 12"),
        text = "During the week of October 12" 
      ),
      GeneralMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"),
          PositiveLabelTestCase("TimeUnit")
        ),
        mentionSpan = PositiveTextTestCase("The week ending October 12th"),
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
      GeneralMentionTestCase(
        labels = Seq(PositiveLabelTestCase("Vessel")),
        mentionSpan = PositiveTextTestCase("cargo vessel"),
        text = "cargo vessel"
      ),
      GeneralMentionTestCase(
        labels = Seq(PositiveLabelTestCase("Vessel")),
        mentionSpan = PositiveTextTestCase("SS Bandirma"),
        text = "SS Bandirma"
      ),
      GeneralMentionTestCase(
        labels = Seq(PositiveLabelTestCase("Vessel")),
        mentionSpan = PositiveTextTestCase("MV Colombo Express"),
        text = "MV Colombo Express"
      ),
      GeneralMentionTestCase(
        labels = Seq(PositiveLabelTestCase("Vessel")),
        mentionSpan = PositiveTextTestCase("CMA CGM Thalassa"),
        text = "CMA CGM Thalassa"
      ),
      GeneralMentionTestCase(
        labels = Seq(PositiveLabelTestCase("Vessel")),
        mentionSpan = PositiveTextTestCase("LNG Finima"),
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
      // GeneralMentionTestCase( // point: check behavior of positive tests in should-not-identify block
      //   labels = Seq(PositiveLabelTestCase("QuantifiedCargo")),
      //   mentionSpan = PositiveTextTestCase("TEUs of ostrich feathers"),
      //   text = "TEUs of ostrich feathers"
      // ),
      NegativeMentionTestCase(
        labels = Seq(NegativeLabelTestCase("QuantifiedCargo")),
        mentionSpan = NegativeTextTestCase("TUs of ostrich feathers"),
        text = "TUs of ostrich feathers"
      ),
      // NegativeMentionTestCase( //point here; this test should fail; conditions for pos test to succeed.
      //   labels = Seq(NegativeLabelTestCase("QuantifiedCargo")),
      //   mentionSpan = NegativeTextTestCase("TEUs of ostrich feathers"),
      //   text = "TEUs of ostrich feathers" // this passes as 'TEUs' - should fail
      // ),
      NegativeMentionTestCase(
        labels = Seq(NegativeLabelTestCase("QuantifiedCargo")), //straightforward neg test
        mentionSpan = NegativeTextTestCase("of ostrich feathers"),
        text = "of ostrich feathers"
      ),
      // GeneralMentionTestCase(
      //   labels = Seq(PositiveLabelTestCase("QuantifiedCargo")),
      //   mentionSpan = PositiveTextTestCase("Metric tons of preserved duck eggs"),
      //   text = "Metric tons of preserved duck eggs"
      // ),
      GeneralMentionTestCase( //this example: mentionSpan doesn't match text
        labels = Seq(NegativeLabelTestCase("QuantifiedCargo")),
        mentionSpan = NegativeTextTestCase("TEUs of ostrich feathers"), 
        text = "of ostrich feathers"
      ),
      GeneralMentionTestCase( //point of this example: what happens if passing pos test for different mention?
        labels = Seq(PositiveLabelTestCase("Vessel")),
        mentionSpan = PositiveTextTestCase("LNG Finima"),
        text = "LNG Finima"
      ) //commented out trying to find failing case
      // GeneralMentionTestCase(
      //   labels = Seq(NegativeLabelTestCase("QuantifiedCargo")),
      //   mentionSpan = NegativeTextTestCase("despite metric tons of preserved duck eggs"),
      //   text = "despite metric tons of preserved duck eggs"
      // ) //now rerunning with this knocked out
    )

    testCases foreach { tc =>
      val results = system.extract(tc.text)
      results should not be empty
      checkMention(tc, results) should be (true)
    }  
  }


  // // it should "identify Concept mentions" in {

  // //   val testCases = Seq(
  // //     GeneralMentionTestCase(
  // //       labels = Seq("Vessel"),
  // //       text = "SS Bandirma"
  // //     ),
  // //     GeneralMentionTestCase(
  // //       labels = Seq("Vessel"),
  // //       text = "MV Colombo Express"
  // //     ),
  // //     GeneralMentionTestCase(
  // //       labels = Seq("Vessel"),
  // //       text = "CMA CGM Thalassa"
  // //     ),
  // //     GeneralMentionTestCase(
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


  
}

//     val text2 = "What is the probability that Hamburgers and Mustard will be in the same container in a port in Germany?"
