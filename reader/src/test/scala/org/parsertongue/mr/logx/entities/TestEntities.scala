package org.parsertongue.mr.logx.entities

//import TestUtils._
import org.parsertongue.mr.TestUtils._
import org.scalatest.{ FlatSpec, Matchers }


class TestEntities extends FlatSpec with Matchers {
    
  "LogX MachineReadingSystem" should "identify TimeExpression mentions" in {

    val testCases = Seq(
      PositiveEntityTestCase(
        labels = Seq("TimeExpression"),
        text = "August 24th 2020"
      ),
      PositiveEntityTestCase(
        labels = Seq("TimeExpression", "OnTimeExpression"),
        text = "on August 24th 2020" 
      ),
      PositiveEntityTestCase(
        labels = Seq("TimeExpression", "AfterTimeExpression"),
        text = "after August 24, 2020" 
      ),
      PositiveEntityTestCase(
        labels = Seq("TimeExpression", "BeforeTimeExpression"),
        text = "by August 24th 2020" 
      ),
      PositiveEntityTestCase(
        labels = Seq("TimeExpression", "IntervalTimeExpression"),
        text = "from August 12, 2020 to August 19, 2020" 
      ),
      // EntityTestCase(
      //  labels = Seq("TimeExpression", "IntervalTimeExpression"),
      //  text = "during the week of October 12"
      // ),
      PositiveEntityTestCase(
        labels = Seq("TimeExpression", "IntervalTimeExpression"),
        text = "during the week"
      ),
      PositiveEntityTestCase(
        labels = Seq("TimeExpression", "IntervalTimeExpression"),
        text = "throughout 1991"
      ),
      PositiveEntityTestCase(
        labels = Seq("TimeExpression", "TimeUnit"),
        text = "the next few days" 
      ),
      PositiveEntityTestCase(
        labels = Seq("TimeExpression", "Date"),
        text = "August 2020" 
      ),
      PositiveEntityTestCase(
        labels = Seq("TimeExpression", "Date"),
        text = "August 12 2020" 
      ),
      PositiveEntityTestCase(
        labels = Seq("TimeExpression", "Date"),
        text = "12/02/1986" 
      ),
      PositiveEntityTestCase(
        labels = Seq("TimeExpression", "Date"),
        text = "05/1986" 
      ),
      PositiveEntityTestCase(
        labels = Seq("TimeExpression", "Date"),
        text = "05/86" 
      ),
      PositiveEntityTestCase(
        labels = Seq("TimeExpression", "Date"),
        text = "1986-12-21" 
      ),
      PositiveEntityTestCase(
        labels = Seq("TimeExpression", "Date"),
        text = "2012 11 30" 
      ),
      PositiveEntityTestCase(
        labels = Seq("TimeExpression", "Date"),
        text = "12 JUN 2021" 
      ),
      PositiveEntityTestCase(
        labels = Seq("TimeExpression", "Date"),
        text = "12 Jun 2021" 
      ),
      PositiveEntityTestCase(
        labels = Seq("TimeExpression", "Date"),
        text = "12-Jun-2021" 
      ),
      //EntityTestCase(
      //  labels = Seq("TimeExpression", "IntervalTimeExpression"),
      //  text = "During the week of October 12" 
      //),
      //EntityTestCase(
      //  labels = Seq("TimeExpression", "IntervalTimeExpression"),
      //  text = "During the week of October 12th" 
      //),
      PositiveEntityTestCase(
        labels = Seq("TimeExpression", "TimeUnit"),
        text = "The week ending October 12th" 
      )
    )

    testCases foreach { tc =>
      val results = system.extract(tc.text)
      results should not be empty
      checkEntity(tc, results) should be (true)
    }  
  }
}
//     val text1 = "How many TEUs of DoD frozen meat is heading to Hamburg?"

//     // "How many TEUs" with "Query" as a label.
//     val LABEL_OF_INTEREST = "Query"
//     val mns1 = system.extract(text1).filter(_ matches LABEL_OF_INTEREST)
    
//     mns1.size should be (1)

//     mns1.head.text should equal ("How many TEUs")

//     val text2 = "What is the probability that Hamburgers and Mustard will be in the same container in a port in Germany?"

//     // "What is the probability" as a label
//     val mns2 = system.extract(text2).filter(_ matches LABEL_OF_INTEREST)

//     mns2.size should be (1)

//     mns2.head.text should equal ("What is the probability")


//   }
//   // WhatQuery -> "What cargo was shipped from Los Angeles on August 12, 2014?"
//   // Date -> "August 12, 2014"
//   // Date -> "August 12 2014"
//   // Date -> "August 2014"
//   // Date -> 8/12/2014
//   // Date -> 8/12/14
//   // Date -> 8/14
//   // UnspecifiedCargo -> "What cargo was shipped from Los Angeles on August 12, 2014?"
// }


//   "MachineReadingSystem" should "find ????" in {
//     val ms = system.extract("????")
//     // Ensure correct taxonomic label is assigned
//     val label = "???"
//     val results = ms filter (_.matches(label))
//     results should not be empty
//     results map (_.text) should contain (label)
//   }

//   it should "ignore ???" in {
//     val results = system.extract("???")
//     results map (_.text) should not contain "???"
//   }

// }