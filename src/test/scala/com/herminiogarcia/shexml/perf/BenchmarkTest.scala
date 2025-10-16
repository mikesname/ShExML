package com.herminiogarcia.shexml.perf

import com.herminiogarcia.shexml.helper.SourceHelper
import com.herminiogarcia.shexml.visitor.FunctionHubExecutorCache
import com.herminiogarcia.shexml.{ParallelConfigInferenceDatatypesNormaliseURIsFixture, RDFStatementCreator}
import org.apache.jena.rdf.model.Model
import org.scalatest.BeforeAndAfter
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers

import java.io.ByteArrayInputStream

class BenchmarkTest extends AnyFunSuite
  with Matchers with RDFStatementCreator
  with BeforeAndAfter with ParallelConfigInferenceDatatypesNormaliseURIsFixture{

  private val runs = 1
  private val example =
    """
      |IMPORT <partial/HoldingsHeader.shexml>
      |FUNCTIONS transformers <scala: functions/Transformers.scala>
      |FUNCTIONS validators <scala: functions/Validators.scala>
      |SOURCE holdings <stdin>
      |IMPORT <partial/HoldingsIterators.shexml>
      |IMPORT <partial/MatcherLanguages.shexml>
      |IMPORT <partial/MatcherLanguagesCodes.shexml>
      |IMPORT <partial/MatcherLanguageCode2Digit.shexml>
      |IMPORT <partial/HoldingsShapes.shexml>
      |
    """.stripMargin

  private var output: Model = _

  test(s"Run the benchmark mapping a number of time(s)") {
    val ois = System.in
    val relPath = "./src/test/resources/benchmark"
    val stream = new ByteArrayInputStream(SourceHelper(relPath).getContentFromRelativePath("holdings_example.json").fileContent.getBytes())
    val start = System.currentTimeMillis()
    val launcher = mappingLauncher.withRelativeToPath(relPath)
    val (ast, varTable) = launcher.initializeAST(example)
    val functionCache = new FunctionHubExecutorCache
    for (_ <- 1 to runs) {
      try {
        System.setIn(stream)
        output = launcher.launchMapping(ast, varTable, functionCache).getDefaultModel
      } catch {
        case e: Exception => e.printStackTrace()
      } finally {
        stream.reset()
        System.setIn(ois)
      }
    }
    val end = System.currentTimeMillis()
    val duration = end - start
    println(s"Benchmark completed in $duration ms")
//    output.write(new FileOutputStream("out2.rdf"))
//    assert(output.size == 72)
  }
}
