package ua.ucu.edu.kafka

import java.io.FileNotFoundException
import java.util.Properties

import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}
import org.slf4j.{Logger, LoggerFactory}

import scala.io.Source

// delete_me - for testing purposes
object DummyDataProducer {

  val logger: Logger = LoggerFactory.getLogger(getClass)
  val BLACKIPURL = "https://raw.githubusercontent.com/ihorhrysha/fraud-detection/master/black-data-provider/src/main/resources/black_ip.csv"
  val BLACKEMAILURL = "https://raw.githubusercontent.com/ihorhrysha/fraud-detection/master/black-data-provider/src/main/resources/black_email.csv"

  def pushTestData(): Unit = {
    val BrokerList: String = System.getenv(Config.KafkaBrokers)

    val Topic = System.getenv(Config.EnrichmentTopic)

    val props = new Properties()
    props.put("bootstrap.servers", BrokerList)
    props.put("client.id", "black-data-provider")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    logger.info("initializing producer")


    val producer = new KafkaProducer[String, String](props)
    //val testMsg = "i am black"
    logger.info("reaching raw files")


    try {
      println(Source.fromURL(BLACKIPURL).mkString.split(System.getProperty("line.separator")).toList)

      val blackIpList : List[String] = Source.fromURL(BLACKIPURL).mkString.split(System.getProperty("line.separator")).toList
      val blackEmailList : List[String] = Source.fromURL(BLACKEMAILURL).mkString.split(System.getProperty("line.separator")).toList




      while (true) {
        blackIpList.foreach(ip => sendMessage(producer, Topic, ip))
        blackEmailList.foreach(mail => sendMessage(producer, Topic, mail))
      }

    } catch {
      case e: FileNotFoundException => logger.error("File not found " + e)
      case e: Exception =>println(e)
    }

    producer.close()
  }

  def sendMessage(prod: KafkaProducer[String, String], topic: String, msg: String) : Unit =  {
    val precord = new ProducerRecord[String, String](topic, msg)
    prod.send(precord, (metadata: RecordMetadata, exception: Exception) => {
      logger.info(metadata.toString, exception)
    })
    Thread.sleep(1000)
  }

}

object Config {
  val KafkaBrokers = "KAFKA_BROKERS"
  val EnrichmentTopic = "ENRICHMENT_TOPIC"
}