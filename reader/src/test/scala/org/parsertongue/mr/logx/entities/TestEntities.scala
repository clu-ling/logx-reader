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
        text = "during the week",
        foundBy = Some("interval-time-expression")
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
      ),
      ExistsMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("TimeExpression"),
          PositiveLabelTestCase("IntervalTime")
        ),
        mentionSpan = PositiveTextTestCase("for the next week"),
        text = "for the next week"
      ),
      ExistsMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("ComplexIntervalTime")
        ),
        mentionSpan = PositiveTextTestCase("daily for the next week"),
        text = "daily for the next week",
        foundBy = Some("complex-interval-time-expression")
      ),
      ExistsMentionTestCase(
        labels = Seq(
          PositiveLabelTestCase("FiscalYear")
        ),
        mentionSpan = PositiveTextTestCase("FY2017"),
        text = "FY2017",
        foundBy = Some("fiscal-year")
      ),
      // ForAllMentionTestCase(
      //   labels = Seq(NegativeLabelTestCase("FiscalYear")),
      //   mentionSpan = NegativeTextTestCase("FYI"),
      //   text = "FYI the planes left"
      // ),
      ExistsMentionTestCase(
        labels = Seq(PositiveLabelTestCase("Month")),
        mentionSpan = PositiveTextTestCase("Jul"),
        text = "Jul",
        foundBy = Some("month")
      ),
      ForAllMentionTestCase(
        labels = Seq(NegativeLabelTestCase("Month")),
        mentionSpan = NegativeTextTestCase("Jul"),
        text = "Julia"
      ),
      ExistsMentionTestCase(
        labels = Seq(PositiveLabelTestCase("Quantity")),
        mentionSpan = PositiveTextTestCase("202k"),
        text = "202k",
        foundBy = Some("quantity-1")
      ),
      ForAllMentionTestCase(
        labels = Seq(NegativeLabelTestCase("Quantity")),
        mentionSpan = NegativeTextTestCase("k"),
        text = "truck"
      ),
      ExistsMentionTestCase(
        labels = Seq(PositiveLabelTestCase("IntervalTime")),
        mentionSpan = PositiveTextTestCase("between 2001 and 2007"),
        text = "between 2001 and 2007",
        foundBy = Some("interval-time-expression")
      ),
      ForAllMentionTestCase(
        labels = Seq(NegativeLabelTestCase("IntervalTime")),
        mentionSpan = NegativeTextTestCase("between 1900 and 2000"),
        text = "between 1900 and 2000 tons of shipping"
      ),
      ExistsMentionTestCase(
        labels = Seq(PositiveLabelTestCase("AfterTime")),
        mentionSpan = PositiveTextTestCase("post 2012"),
        text = "post 2012",
        foundBy = Some("after-time-expression")
      ),
      ForAllMentionTestCase(
        labels = Seq(NegativeLabelTestCase("AfterTime")),
        mentionSpan = NegativeTextTestCase("post 2000"),
        text = "you should post 2000 articles by July"
      ),
      ExistsMentionTestCase(
        labels = Seq(PositiveLabelTestCase("Date")),
        mentionSpan = PositiveTextTestCase("2 November 2016"),
        text = "2 November 2016",
        foundBy = Some("date")
      ),
      ExistsMentionTestCase(
        labels = Seq(PositiveLabelTestCase("ApproximateTime")),
        mentionSpan = PositiveTextTestCase("circa Nov 2016"),
        text = "circa Nov 2016",
        foundBy = Some("approx-time") //FIXME: Implement me
      ),
      ExistsMentionTestCase(
        labels = Seq(PositiveLabelTestCase("AfterTime")),
        mentionSpan = PositiveTextTestCase("as of Jul 2017"),
        text = "as of Jul 2017",
        foundBy = Some("after-time-expression")
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
      ForAllMentionTestCase(
        labels = Seq(NegativeLabelTestCase("QuantifiedCargo")),
        mentionSpan = NegativeTextTestCase("TUs of ostrich feathers"), 
        text = "TUs of ostrich feathers"
      ),
      ForAllMentionTestCase(
        labels = Seq(NegativeLabelTestCase("QuantifiedCargo")), 
        mentionSpan = NegativeTextTestCase("of ostrich feathers"), 
        text = "of ostrich feathers"
      ),
      ForAllMentionTestCase(
        labels = Seq(NegativeLabelTestCase("WhatQuery")), 
        mentionSpan = NegativeTextTestCase("of ostrich feathers"), 
        text = "TEUs of ostrich feathers"
      ),
    )

    testCases foreach { tc =>
      val results = system.extract(tc.text)
      results should not be empty
      checkMention(tc, results) should be (true)
    }  
  }

  it should "identify systematic mentions" in {
    //systematic: all allowed cases.
    val testCases = Seq(
      // ForAll(NegLbl, NegTxt)
      ForAllMentionTestCase(
        labels = Seq(NegativeLabelTestCase("QuantifiedCargo")),
        mentionSpan = NegativeTextTestCase("TUs of ostrich feathers"),
        text = "TUs of ostrich feathers"
      ),
      // Exists(PosL, PosT)
      ExistsMentionTestCase( 
        labels = Seq(PositiveLabelTestCase("Vessel")),
        mentionSpan = PositiveTextTestCase("LNG Finima"),
        text = "LNG Finima"
      ),
      // Exists(NegL, NegT) <- dubious usefulness.
      ExistsMentionTestCase(
        labels = Seq(NegativeLabelTestCase("QuantifiedCargo")), // wrong label
        mentionSpan = NegativeTextTestCase("TEUs of ostrich feathers"), // mentionSpan doesn't match text
        text = "of ostrich feathers"
      ),
      // Exists(NegL, PosT)
      ExistsMentionTestCase( 
        labels = Seq(NegativeLabelTestCase("Date")), // wrong label
        mentionSpan = PositiveTextTestCase("LNG Finima"),
        text = "LNG Finima"
      ),
      // Exists(PosL, NegT)
      ExistsMentionTestCase( 
        labels = Seq(PositiveLabelTestCase("Vessel")),
        mentionSpan = NegativeTextTestCase("Finima"), // mentionSpan doesn't match text
        text = "LNG Finima"
      )
    )

    testCases foreach { tc =>
      val results = system.extract(tc.text)
      results should not be empty
      checkMention(tc, results) should be (true)
    }  
  }
  
}

//     val text2 = "What is the probability that Hamburgers and Mustard will be in the same container in a port in Germany?"
