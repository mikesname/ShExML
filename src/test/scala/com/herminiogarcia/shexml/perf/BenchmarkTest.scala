package com.herminiogarcia.shexml.perf

import com.herminiogarcia.shexml.{ParallelConfigInferenceDatatypesNormaliseURIsFixture, RDFStatementCreator}
import com.herminiogarcia.shexml.helper.SourceHelper
import org.apache.jena.datatypes.xsd.XSDDatatype
import org.apache.jena.rdf.model.Model
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfter, ConfigMap}

import java.io.ByteArrayInputStream

class BenchmarkTest extends AnyFunSuite
  with Matchers with RDFStatementCreator
  with BeforeAndAfter with ParallelConfigInferenceDatatypesNormaliseURIsFixture{

  private val example =
    """
      |IMPORT <src/test/resources/benchmark/partial/HoldingsHeader.shexml>
      |FUNCTIONS transformers <scala: src/test/resources/benchmark/functions/Transformers.scala>
      |FUNCTIONS validators <scala: src/test/resources/benchmark/functions/Validators.scala>
      |SOURCE holdings <stdin>
      |IMPORT <src/test/resources/benchmark/partial/HoldingsIterators.shexml>
      |IMPORT <src/test/resources/benchmark/partial/MatcherLanguages.shexml>
      |IMPORT <src/test/resources/benchmark/partial/MatcherLanguagesCodes.shexml>
      |IMPORT <src/test/resources/benchmark/partial/MatcherLanguageCode2Digit.shexml>
      |IMPORT <src/test/resources/benchmark/partial/HoldingsShapes.shexml>
      |
    """.stripMargin

  private var output: Model = _

  test("Run the benchmark mapping 15 times") {
    val ois = System.in
    val stream = new ByteArrayInputStream(new SourceHelper().getContentFromRelativePath("./src/test/resources/benchmark/holdings_example.json").fileContent.getBytes())
    val start = System.currentTimeMillis()
    for (_ <- 1 to 15) {
      stream.reset()
      try {
        System.setIn(stream)
        output = mappingLauncher.launchMapping(example).getDefaultModel
      } catch {
        case e: Exception => e.printStackTrace()
      } finally {
        stream.reset()
        System.setIn(ois)
      }
    }
    assert(output.size == 72)
    val end = System.currentTimeMillis()
    val duration = end - start
    println(s"Benchmark completed in $duration ms")
  }
}
