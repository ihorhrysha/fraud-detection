package ua.ucu.edu.kafka

import java.io.FileNotFoundException
import java.util.Properties

import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerConfig, ProducerRecord, RecordMetadata}
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.{Logger, LoggerFactory}
import ua.ucu.edu.model.{BlackData}

import scala.io.Source

// delete_me - for testing purposes
object DummyDataProducer {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  def pushTestData(): Unit = {
    val BrokerList: String = System.getenv(Config.KafkaBrokers)

    val Topic = System.getenv(Config.EnrichmentTopic)
    val BlackIPList = System.getenv(Config.BlackIPUrl)
    val BlackEmailList = System.getenv(Config.BlackMailUrl)

    val props = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BrokerList)
    props.put(ProducerConfig.CLIENT_ID_CONFIG, "black-data-provider")
    props.put("schema.registry.url", System.getenv(Config.SchemaRegistry))
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getCanonicalName)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[KafkaAvroSerializer].getCanonicalName)
    props.put(ProducerConfig.ACKS_CONFIG, "all")
    props.put(ProducerConfig.RETRIES_CONFIG, "0")

    logger.info("initializing producer")

    val producer = new KafkaProducer[String, BlackData](props)

    logger.info("reaching raw files")

    try {

      val blackIpList: List[String] = Source.fromURL(BlackIPList).mkString.split("\r\n").toList
      val blackEmailList: List[String] = Source.fromURL(BlackEmailList).mkString.split("\r\n").toList

      while (true) {
        blackIpList.foreach(ip => sendMessage(producer, Topic, ip, BlackData("IP", ip)))
        blackEmailList.foreach(mail => sendMessage(producer, Topic, mail, BlackData("EMAIL", mail)))

        Thread.sleep(1000*60*60*24)

      }

    } catch {
      case e: FileNotFoundException => logger.error("File not found " + e)
      case e: Exception => println(e)
    }

    producer.close()
  }

  def sendMessage(prod: KafkaProducer[String, BlackData], topic: String, key: String, value: BlackData): Unit = {

    val recordBlackData = new ProducerRecord[String, BlackData](topic, key, value)
    prod.send(recordBlackData, (metadata: RecordMetadata, exception: Exception) => {
      logger.info("topic: " + topic + " key: " + key + " BlackData: " + value)
    })

    Thread.sleep(200)

  }

}

object Config {
  val KafkaBrokers = "KAFKA_BROKERS"
  val EnrichmentTopic = "ENRICHMENT_TOPIC"
  val BlackIPUrl = "BLACKIPURL"
  val BlackMailUrl = "BLACKEMAILURL"
  val SchemaRegistry = "SCHEMA_REGISTRY"
  val BlackIPListPublic = "BLACKIPURL_PUBLIC"
}