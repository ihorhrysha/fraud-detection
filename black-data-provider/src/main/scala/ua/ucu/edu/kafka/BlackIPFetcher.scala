package ua.ucu.edu.kafka

import java.io.FileNotFoundException
import java.util.Properties

import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord, RecordMetadata}
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.{Logger, LoggerFactory}
import ua.ucu.edu.model.BlackData

import scala.io.Source

class BlackIPFetcher extends Runnable {

  val logger: Logger = LoggerFactory.getLogger(getClass)
  val BrokerList: String = System.getenv(Config.KafkaBrokers)
  val Topic = System.getenv(Config.EnrichmentTopic)
  val BlackIPListPublic = System.getenv(Config.BlackIPListPublic)

  val props = new Properties()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BrokerList)
  props.put(ProducerConfig.CLIENT_ID_CONFIG, "black-data-provider")
  props.put("schema.registry.url", System.getenv(Config.SchemaRegistry))
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getCanonicalName)
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[KafkaAvroSerializer].getCanonicalName)
  props.put(ProducerConfig.ACKS_CONFIG, "all")
  props.put(ProducerConfig.RETRIES_CONFIG, "0")

  logger.info("initializing producer for public ip")


  val producer = new KafkaProducer[String, BlackData](props)

  logger.info("reaching publick ip blacklist file")

  def run {
    while (true) {
      try {

        val blackIPList = Source.fromURL(BlackIPListPublic).mkString.split(System.getProperty("line.separator")).toList
        blackIPList.filter(ipString => ipString.matches("[0-9].+")).foreach(ip => sendMessage(producer, Topic, ip, BlackData("IP", ip)))
        //blackIPList.filter(ipString => ipString.matches("[0-9].+")).foreach(ip => {println(ip); Thread.sleep(1000)})

        //once a day refresh
        Thread.sleep(1000*60*60*24)

      } catch {
        case e: FileNotFoundException => logger.error("File not found " + e)
        case e: Exception => println(e)
      }
    }
    producer.close()
  }

  def sendMessage(prod: KafkaProducer[String, BlackData], topic: String, key: String, value: BlackData): Unit = {
    val recordBlackData = new ProducerRecord[String, BlackData](topic, key, value)
    prod.send(recordBlackData, (metadata: RecordMetadata, exception: Exception) => {
      logger.info(metadata.toString, exception)
    })
  }

}
