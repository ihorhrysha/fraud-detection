package ua.ucu.edu.kafka

import java.io.FileNotFoundException
import java.util.Properties

import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord, RecordMetadata}
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.{Logger, LoggerFactory}
import com.github.javafaker.Faker
import com.github.javafaker.service.FakeValuesService
import com.github.javafaker.service.RandomService
import java.util.Locale

import ua.ucu.edu.model.User

import scala.io.Source

object UserDataProducer {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  // This is just for testing purposes
  def pushTestData(): Unit = {
    val BrokerList: String = System.getenv(Config.KafkaBrokers)

    val Topic = System.getenv(Config.MainTopic)
    val BlackIPList = System.getenv(Config.BlackIPUrl)
    val BlackEmailList = System.getenv(Config.BlackMailUrl)

    val props = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BrokerList)
    props.put(ProducerConfig.CLIENT_ID_CONFIG, "user-data-provider")
    props.put("schema.registry.url", System.getenv(Config.SchemaRegistry))
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getCanonicalName)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[KafkaAvroSerializer].getCanonicalName)
    props.put(ProducerConfig.ACKS_CONFIG, "all")
    props.put(ProducerConfig.RETRIES_CONFIG, "0")

    logger.info("initializing producer")

    val producer = new KafkaProducer[String, User](props)

    logger.info("reaching raw files")

    try {
      //println(Source.fromURL(BlackIPList).mkString.split(System.getProperty("line.separator")).toList)

      val blackIpList: List[String] = Source.fromURL(BlackIPList).mkString.split(System.getProperty("line.separator")).toList
      val blackEmailList: List[String] = Source.fromURL(BlackEmailList).mkString.split(System.getProperty("line.separator")).toList

      val fakeValuesService = new FakeValuesService(new Locale("en-GB"), new RandomService)
      val fname = new Faker().name()
      val r = scala.util.Random

      while (true) {
        val firstName = fname.firstName()
        val surname = fname.lastName()
        val name = firstName + " " + surname
        var email = firstName.toLowerCase() + "." + surname.toLowerCase() + "@gmail.com"
        var ip = fakeValuesService.regexify("(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])")

        //val user  = new User(name, email, ip)

        var ran = r.nextInt(100)
        if(ran<10) {
          logger.info("Black data injection")
          if (ran < 5) {
            logger.info("Black email injected")
            email = blackEmailList(r.nextInt(blackEmailList.length)).trim()
          } else {
            logger.info("Black ip injected")
            ip = blackIpList(r.nextInt(blackIpList.length)).trim()
          }
        }

        val user  = new User(name, email, ip)

        logger.info("topic: " + Topic + " key: " + name + " User: " + user)
        val recordUserData = new ProducerRecord[String, User](Topic, name, user)
        producer.send(recordUserData, (metadata: RecordMetadata, exception: Exception) => {
          logger.info(metadata.toString, exception)
        })
        Thread.sleep(r.nextInt(1000))

      }

    } catch {
      case e: FileNotFoundException => logger.error("File not found " + e)
      case e: Exception => println(e)
    }

    producer.close()
  }
}

object Config {
  val KafkaBrokers = "KAFKA_BROKERS"
  val EnrichmentTopic = "ENRICHMENT_TOPIC"
  val BlackIPUrl = "BLACKIPURL"
  val BlackMailUrl = "BLACKEMAILURL"
  val SchemaRegistry = "SCHEMA_REGISTRY"
  val BlackIPListPublic = "BLACKIPURL_PUBLIC"
  val MainTopic= "MAIN_TOPIC"
}