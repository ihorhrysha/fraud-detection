package ua.ucu.edu

import ua.ucu.edu.kafka.{BlackIPFetcher, DummyDataProducer}
import org.slf4j.LoggerFactory

import scala.concurrent.duration
import scala.language.postfixOps

object Main extends App {

  val logger = LoggerFactory.getLogger(getClass)

  logger.info("======== Black Data Provider App Init ========")

  import duration._

  //scheduler.schedule(5 seconds, 10 seconds,
  new Runnable {
    override def run(): Unit = {
      logger.debug("black data request")
      // ???
      // todo - ask weather api and send data to kafka topic - recommended format is json - or you can come up with simpler string-based protocol
    }
  }
  //)

  // for testing purposes only
  DummyDataProducer.pushTestData()
  new Thread(new BlackIPFetcher).start

}
