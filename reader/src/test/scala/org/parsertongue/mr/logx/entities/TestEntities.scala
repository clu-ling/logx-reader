package org.parsertongue.mr.logx.entities

//import TestUtils._
import org.parsertongue.mr.TestUtils._
import org.scalatest.{ FlatSpec, Matchers }


class TestEntities extends FlatSpec with Matchers {
    
  "LogX MachineReadingSystem" should "identify TimeExpression mentions" in {

    val testCases = Seq(
      ExistsMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression")
        ),
        mentionSpan = PositiveTextTestCase("August 24th 2020"),
        text = "August 24th 2020"
      ),
      ExistsMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"),
          PositiveLabelTestCase("OnTime")
        ),
        mentionSpan = PositiveTextTestCase("on August 24th 2020"),
        text = "on August 24th 2020" 
      ),
      ExistsMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"),
          PositiveLabelTestCase("AfterTime")
        ),
        mentionSpan = PositiveTextTestCase("after August 24, 2020"),
        text = "after August 24, 2020" 
      ),
      ExistsMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("BeforeTime")
        ),
        mentionSpan = PositiveTextTestCase("by August 24th 2020"),
        text = "by August 24th 2020" 
      ),
      ExistsMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("IntervalTime")
        ),
        mentionSpan = PositiveTextTestCase("from August 12, 2020 to August 19, 2020"),
        text = "from August 12, 2020 to August 19, 2020" 
      ),
      ExistsMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("IntervalTime")
        ),
        mentionSpan = PositiveTextTestCase("during the week"),
        text = "during the week"
      ),
      ExistsMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("IntervalTime")
        ),
        mentionSpan = PositiveTextTestCase("throughout 1991"),
        text = "throughout 1991"
      ),
      ExistsMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("TimeUnit")
        ),
        mentionSpan = PositiveTextTestCase("the next few days"),
        text = "the next few days" 
      ),
      ExistsMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("Date")
        ),
        mentionSpan = PositiveTextTestCase("August 2020"),
        text = "August 2020" 
      ),
      ExistsMentionTestCase(
        labels = Seq(PositiveLabelTestCase("TimeExpression"), 
        PositiveLabelTestCase("Date")
        ),
        mentionSpan = PositiveTextTestCase("August 12 2020"),
        text = "August 12 2020" 
      ),
      ExistsMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("Date")
        ),
        mentionSpan = PositiveTextTestCase("12/02/1986"),
        text = "12/02/1986" 
      ),
      ExistsMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("Date")
        ),
        mentionSpan = PositiveTextTestCase("05/1986"),
        text = "05/1986" 
      ),
      ExistsMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("Date")
        ),
        mentionSpan = PositiveTextTestCase("05/86"),
        text = "05/86" 
      ),
      ExistsMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("Date")
        ),
        mentionSpan = PositiveTextTestCase("1986-12-21"),
        text = "1986-12-21" 
      ),
      ExistsMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("Date")
        ),
        mentionSpan = PositiveTextTestCase("2012 11 30"),
        text = "2012 11 30" 
      ),
      ExistsMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("Date")
        ),
        mentionSpan = PositiveTextTestCase("12 JUN 2021"),
        text = "12 JUN 2021" 
      ),
      ExistsMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("Date")
        ),
        mentionSpan = PositiveTextTestCase("12 Jun 2021"),
        text = "12 Jun 2021" 
      ),
      ExistsMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("Date")
        ),
        mentionSpan = PositiveTextTestCase("12-Jun-2021"),
        text = "12-Jun-2021" 
      ),
      ExistsMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"), 
          PositiveLabelTestCase("IntervalTime")
        ),
        mentionSpan = PositiveTextTestCase("During the week of October 12"),
        text = "During the week of October 12" 
      ),
      ExistsMentionTestCase(
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
      ExistsMentionTestCase(
        labels = Seq(PositiveLabelTestCase("Vessel")),
        mentionSpan = PositiveTextTestCase("cargo vessel"),
        text = "cargo vessel"
      ),
      ExistsMentionTestCase(
        labels = Seq(PositiveLabelTestCase("Vessel")),
        mentionSpan = PositiveTextTestCase("SS Bandirma"),
        text = "SS Bandirma"
      ),
      ExistsMentionTestCase(
        labels = Seq(PositiveLabelTestCase("Vessel")),
        mentionSpan = PositiveTextTestCase("MV Colombo Express"),
        text = "MV Colombo Express"
      ),
      ExistsMentionTestCase(
        labels = Seq(PositiveLabelTestCase("Vessel")),
        mentionSpan = PositiveTextTestCase("CMA CGM Thalassa"),
        text = "CMA CGM Thalassa"
      ),
      ExistsMentionTestCase(
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
      // ExistsMentionTestCase( // point: check behavior of positive tests in should-not-identify block
      //   labels = Seq(PositiveLabelTestCase("QuantifiedCargo")),
      //   mentionSpan = PositiveTextTestCase("TEUs of ostrich feathers"),
      //   text = "TEUs of ostrich feathers"
      // ),
      ForAllMentionTestCase(
        labels = Seq(NegativeLabelTestCase("QuantifiedCargo")),
        mentionSpan = NegativeTextTestCase("TUs of ostrich feathers"), //changed to Neg: ForALL(-, -, -)
        text = "TUs of ostrich feathers"
      ),
      // ForAllMentionTestCase( //point here; this test should fail; conditions for pos test to succeed.
      //   labels = Seq(NegativeLabelTestCase("QuantifiedCargo")),
      //   mentionSpan = NegativeTextTestCase("TEUs of ostrich feathers"),
      //   text = "TEUs of ostrich feathers" // this passes as 'TEUs' - should fail
      // ),
      ForAllMentionTestCase(
        labels = Seq(NegativeLabelTestCase("QuantifiedCargo")), //straightforward neg test
        mentionSpan = NegativeTextTestCase("of ostrich feathers"),
        text = "of ostrich feathers"
      ),
      // ExistsMentionTestCase(
      //   labels = Seq(PositiveLabelTestCase("QuantifiedCargo")),
      //   mentionSpan = PositiveTextTestCase("Metric tons of preserved duck eggs"),
      //   text = "Metric tons of preserved duck eggs"
      // ),
      ExistsMentionTestCase( //this example: mentionSpan doesn't match text
        labels = Seq(NegativeLabelTestCase("QuantifiedCargo")),
        mentionSpan = NegativeTextTestCase("TEUs of ostrich feathers"), 
        text = "of ostrich feathers"
      ),
      ExistsMentionTestCase( //point of this example: what happens if passing pos test for different mention?
        labels = Seq(PositiveLabelTestCase("Vessel")),
        mentionSpan = PositiveTextTestCase("LNG Finima"),
        text = "LNG Finima"
      ) //commented out trying to find failing case
      // ExistsMentionTestCase(
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
  // //     ExistsMentionTestCase(
  // //       labels = Seq("Vessel"),
  // //       text = "SS Bandirma"
  // //     ),
  // //     ExistsMentionTestCase(
  // //       labels = Seq("Vessel"),
  // //       text = "MV Colombo Express"
  // //     ),
  // //     ExistsMentionTestCase(
  // //       labels = Seq("Vessel"),
  // //       text = "CMA CGM Thalassa"
  // //     ),
  // //     ExistsMentionTestCase(
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
