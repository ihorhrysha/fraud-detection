package ua.ucu.edu.kafka

import java.util.Properties

import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}
import org.slf4j.{Logger, LoggerFactory}

import scala.io.Source

// delete_me - for testing purposes
object DummyDataProducer {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  def pushTestData(): Unit = {
    //val BrokerList: String = System.getenv(Config.KafkaBrokers)
    val BrokerList: String = "localhost:9092"
    //val Topic = System.getenv(Config.EnrichmentTopic)
    val Topic = "black-data"
    val props = new Properties()
    props.put("bootstrap.servers", BrokerList)
    props.put("client.id", "black-data-provider")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    logger.info("initializing producer")

    val producer = new KafkaProducer[String, String](props)

    val testMsg = "i am black"

    val blackIpList : List[String] = Source.fromResource("black_ip.csv").getLines().toList
    val blackEmailList : List[String] = Source.fromResource("black_email.csv").getLines().toList

    while (true) {
      //Thread.sleep(1000)
      logger.info(s"[$Topic] $testMsg")
      //val data = new ProducerRecord[String, String](Topic, testMsg)
      blackIpList.foreach(ip => sendMessage(producer, Topic, ip))
      blackEmailList.foreach(ip => sendMessage(producer, Topic, ip))
     /* producer.send(data, (metadata: RecordMetadata, exception: Exception) => {
        logger.info(metadata.toString, exception)
      }) */
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