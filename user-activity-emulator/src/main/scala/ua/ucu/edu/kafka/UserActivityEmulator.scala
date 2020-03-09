package ua.ucu.edu.kafka

import java.io.FileNotFoundException
import java.util.concurrent.TimeUnit
import java.util.Properties

import com.github.javafaker.Faker
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord, RecordMetadata}
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.{Logger, LoggerFactory}
import ua.ucu.edu.model.User

import scala.io.Source
import scala.util.Random

// delete_me - for testing purposes
object UserActivityEmulator {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  // This is just for testing purposes
  def emulate(): Unit = {


    val BrokerList: String = System.getenv(Config.KafkaBrokers)

    val Topic = System.getenv(Config.MainTopic)

    val BlackIPList = System.getenv(Config.BlackIPUrl)
    val BlackEmailList = System.getenv(Config.BlackMailUrl)
    val SchemaRegistryURL =  System.getenv(Config.SchemaRegistry)

    val props = new Properties()
    logger.info("entering the application")
    logger.info(SchemaRegistryURL)

    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BrokerList)
    props.put(ProducerConfig.CLIENT_ID_CONFIG, "user-data-provider")
    props.put("schema.registry.url", SchemaRegistryURL)
    //props.put("schema.registry.url", "http://schema-registry:8081")

    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getCanonicalName)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[KafkaAvroSerializer].getCanonicalName)
    props.put(ProducerConfig.ACKS_CONFIG, "all")
    props.put(ProducerConfig.RETRIES_CONFIG, "0")

    logger.info("initializing producer")

    val producer = new KafkaProducer[String, User](props)

    val faker = new Faker()
    val rand = Random

    try {

      val blackIpList: List[String] = Source.fromURL(BlackIPList).mkString.split("\r\n").toList
      val blackEmailList: List[String] = Source.fromURL(BlackEmailList).mkString.split("\r\n").toList

      while (true) {

        val fname = faker.name()
        val name = fname.fullName()

        var email = fname.username() + "@" + faker.internet().domainName()
        var ip = faker.internet().ipV4Address()


        val randInt = rand.nextInt(100)
        if(randInt<10) {
          if (randInt < 5) {
            logger.info("---Black email injected---")
            email = blackEmailList(rand.nextInt(blackEmailList.length)).trim()
          } else {
            logger.info("---Black ip injected---")
            ip = blackIpList(rand.nextInt(blackIpList.length)).trim()
            // TODO fix with streaming app
            email = ""
          }
        }

        val user  = new User(name, email, ip)


        val recordUserData = new ProducerRecord[String, User](Topic, name, user)
        producer.send(recordUserData, (metadata: RecordMetadata, exception: Exception) => {
          logger.info("topic: " + Topic + " key: " + name + " User: " + user)
          //logger.info(metadata.toString, exception)
        })
        Thread.sleep(rand.nextInt(3000))
      }

      //Only way to stop app
      sys.addShutdownHook {
        producer.close(10, TimeUnit.SECONDS)
      }

    } catch {
      case e: FileNotFoundException => logger.error("File not found " + e)
      case e: Exception => println(e)
    }

  }
}

object Config {
  val KafkaBrokers = "KAFKA_BROKERS"
  val MainTopic = "MAIN_TOPIC"
  val SchemaRegistry = "SCHEMA_REGISTRY"
  val BlackIPUrl = "BLACKIPURL"
  val BlackMailUrl = "BLACKEMAILURL"
}