package org.parsertongue.mr.logx.entities

//import TestUtils._
import org.parsertongue.mr.TestUtils._
import org.scalatest.{ FlatSpec, Matchers }


class TestEntities extends FlatSpec with Matchers {

  "LogX MachineReadingSystem" should "identify Query mentions" in {

    val text1 = "How many TEUs of DoD frozen meat is heading to Hamburg?"

    // "How many TEUs" with "Query" as a label.
    val LABEL_OF_INTEREST = "Query"
    val mns1 = system.extract(text1).filter(_ matches LABEL_OF_INTEREST)
    
    mns1.size should be (1)

    mns1.head.text should equal ("How many TEUs")

    val text2 = "What is the probability that Hamburgers and Mustard will be in the same container in a port in Germany?"

    // "What is the probability" as a label
    val mns2 = system.extract(text2).filter(_ matches LABEL_OF_INTEREST)

    mns2.size should be (1)

    mns2.head.text should equal ("What is the probability")


  }
  // WhatQuery -> "What cargo was shipped from Los Angeles on August 12, 2014?"
  // Date -> "August 12, 2014"
  // Date -> "August 12 2014"
  // Date -> "August 2014"
  // UnspecifiedCargo -> "What cargo was shipped from Los Angeles on August 12, 2014?"

}


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