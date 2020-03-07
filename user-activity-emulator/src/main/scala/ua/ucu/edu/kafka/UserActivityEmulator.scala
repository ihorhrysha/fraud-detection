package ua.ucu.edu.kafka

import java.util.{Locale, Properties}

import com.github.javafaker.Faker
import com.github.javafaker.service.{FakeValuesService, RandomService}
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord, RecordMetadata}
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.{Logger, LoggerFactory}
import ua.ucu.edu.model.User

// delete_me - for testing purposes
object UserActivityEmulator {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  // This is just for testing purposes
  def emulate(): Unit = {
    val BrokerList: String = System.getenv(Config.KafkaBrokers)
    val Topic = System.getenv(Config.MainTopic)

    val props = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BrokerList)
    props.put(ProducerConfig.CLIENT_ID_CONFIG, "web-site-1")
    props.put("schema.registry.url", System.getenv(Config.SchemaRegistry))
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getCanonicalName)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[KafkaAvroSerializer].getCanonicalName)
    props.put(ProducerConfig.ACKS_CONFIG, "all")
    props.put(ProducerConfig.RETRIES_CONFIG, "0")

    logger.info("initializing producer")

    val producer = new KafkaProducer[String, User](props)

    while (true) {
      Thread.sleep(10000)

      val faker = new Faker()

      val newUser = User(faker.funnyName().name(), faker.internet().emailAddress(), faker.internet().ipV4Address())

      logger.info(s"[$Topic] $newUser")

      val recordUserData = new ProducerRecord[String, User](Topic, faker.internet().uuid(), newUser)
      producer.send(recordUserData, (metadata: RecordMetadata, exception: Exception) => {
        logger.info(metadata.toString, exception)
      })

    }

    producer.close()
  }
}

object Config {
  val KafkaBrokers = "KAFKA_BROKERS"
  val MainTopic = "MAIN_TOPIC"
  val SchemaRegistry = "SCHEMA_REGISTRY"
}