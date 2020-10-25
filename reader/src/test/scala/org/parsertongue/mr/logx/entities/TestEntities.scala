package org.parsertongue.mr.logx.entities

//import TestUtils._
import org.parsertongue.mr.TestUtils._
import org.scalatest.{ FlatSpec, Matchers }


class TestEntities extends FlatSpec with Matchers {
    
  "LogX MachineReadingSystem" should "identify TimeExpression mentions" in {

    val testCases = Seq(
      EntityTestCase(
        labels = Seq("TimeExpression"),
        text = "August 24th 2020"
      ),
      EntityTestCase(
        labels = Seq("TimeExpression", "OnTime"),
        text = "on August 24th 2020" 
      ),
      EntityTestCase(
        labels = Seq("TimeExpression", "AfterTime"),
        text = "after August 24, 2020" 
      ),
      EntityTestCase(
        labels = Seq("TimeExpression", "BeforeTime"),
        text = "by August 24th 2020" 
      ),
      EntityTestCase(
        labels = Seq("TimeExpression", "IntervalTime"),
        text = "from August 12, 2020 to August 19, 2020" 
      ),
      // EntityTestCase(
      //  labels = Seq("TimeExpression", "IntervalTimeExpression"),
      //  text = "during the week of October 12"
      // ),
      EntityTestCase(
        labels = Seq("TimeExpression", "IntervalTime"),
        text = "during the week"
      ),
      EntityTestCase(
        labels = Seq("TimeExpression", "IntervalTime"),
        text = "throughout 1991"
      ),
      EntityTestCase(
        labels = Seq("TimeExpression", "TimeUnit"),
        text = "the next few days" 
      ),
      EntityTestCase(
        labels = Seq("TimeExpression", "Date"),
        text = "August 2020" 
      ),
      EntityTestCase(
        labels = Seq("TimeExpression", "Date"),
        text = "August 12 2020" 
      ),
      EntityTestCase(
        labels = Seq("TimeExpression", "Date"),
        text = "12/02/1986" 
      ),
      EntityTestCase(
        labels = Seq("TimeExpression", "Date"),
        text = "05/1986" 
      ),
      EntityTestCase(
        labels = Seq("TimeExpression", "Date"),
        text = "05/86" 
      ),
      EntityTestCase(
        labels = Seq("TimeExpression", "Date"),
        text = "1986-12-21" 
      ),
      EntityTestCase(
        labels = Seq("TimeExpression", "Date"),
        text = "2012 11 30" 
      ),
      EntityTestCase(
        labels = Seq("TimeExpression", "Date"),
        text = "12 JUN 2021" 
      ),
      EntityTestCase(
        labels = Seq("TimeExpression", "Date"),
        text = "12 Jun 2021" 
      ),
      EntityTestCase(
        labels = Seq("TimeExpression", "Date"),
        text = "12-Jun-2021" 
      ),
      //EntityTestCase(
      //  labels = Seq("TimeExpression", "IntervalTime"),
      //  text = "During the week of October 12" 
      //),
      //EntityTestCase(
      //  labels = Seq("TimeExpression", "IntervalTime"),
      //  text = "During the week of October 12th" 
      //),
      EntityTestCase(
        labels = Seq("TimeExpression", "TimeUnit"),
        text = "The week ending October 12th" 
      )
    )

    testCases foreach { tc =>
      val results = system.extract(tc.text)
      results should not be empty
      hasEntity(tc, results) should be (true)
    }  
  }
  it should "identify Vessel mentions" in {

    val testCases = Seq(
      EntityTestCase(
        labels = Seq("Vessel"),
        text = "cargo vessel"
      )
    )

    testCases foreach { tc =>
      val results = system.extract(tc.text)
      results should not be empty
      hasEntity(tc, results) should be (true)
    }  
  }

  it should "identify Cargo mentions" in {

    val testCases = Seq(
      EntityTestCase(
        labels = Seq("QuantifiedCargo"),
        text = "TEUs of ostrich feathers"
      ),
      EntityTestCase(
        labels = Seq("QuantifiedCargo"),
        text = "Metric tons of preserved duck eggs"
      )
    )

    testCases foreach { tc =>
      val results = system.extract(tc.text)
      results should not be empty
      hasEntity(tc, results) should be (true)
    }  
  }
}

//     val text2 = "What is the probability that Hamburgers and Mustard will be in the same container in a port in Germany?"
