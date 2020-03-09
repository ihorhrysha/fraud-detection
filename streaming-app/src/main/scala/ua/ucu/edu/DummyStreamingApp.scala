package ua.ucu.edu

import java.util.{Collections, Properties}
import java.util.concurrent.TimeUnit

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import ua.ucu.edu.model.{BlackData, EnrichedUser, User}
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.kstream.{GlobalKTable}
import org.apache.kafka.streams.scala.{Serdes, StreamsBuilder}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.kstream.KStream
import org.slf4j.LoggerFactory

// dummy app for testing purposes
object DummyStreamingApp extends App {

  val logger = LoggerFactory.getLogger(getClass)

  val props = new Properties()
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streaming_app")
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv(Config.KafkaBrokers))
  props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, Long.box(5 * 1000))
  props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, Long.box(0))
  props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
  props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, classOf[SpecificAvroSerde[_ <: SpecificRecord]])
  props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, System.getenv(Config.SchemaRegistry))
  //  props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val builder: StreamsBuilder = new StreamsBuilder
  val builder2: StreamsBuilder = new StreamsBuilder

  implicit val stringSerde: Serde[String] = Serdes.String
  implicit val blackDataSerde: Serde[BlackData] = new SpecificAvroSerde[BlackData]
  implicit val usersSerde: Serde[User] = new SpecificAvroSerde[User]
  implicit val enrichedUsersSerde: Serde[EnrichedUser] = new SpecificAvroSerde[EnrichedUser]

  usersSerde.configure(Collections.singletonMap(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, System.getenv(Config.SchemaRegistry)), false)
  blackDataSerde.configure(Collections.singletonMap(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, System.getenv(Config.SchemaRegistry)), false)
  enrichedUsersSerde.configure(Collections.singletonMap(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, System.getenv(Config.SchemaRegistry)), false)

  val blackDataTable: GlobalKTable[String, BlackData] = builder.globalTable(System.getenv(Config.EnrichmentTopic))
  val userActivityStream = builder.stream[String, User](System.getenv(Config.MainTopic))

  // branching stream
  val predicates: List[(String, User) => Boolean] = List(
    (k, v) => !v.email.isEmpty,
    (k, v) => !v.IP.isEmpty
  )

  val branches: Array[KStream[String, User]] = userActivityStream.branch(predicates: _*)

  branches(0)
    .leftJoin(blackDataTable)(
      (_, user) => user.email,
      (user, blackData) => if (blackData != null) EnrichedUser(user.name, user.email, user.IP, "bad email") else null
      //(user, blackData) => if (blackData != null) user.IP+" bad IP" else null
    ).filter((_, enrichedUser) => enrichedUser != null).peek((k, v) => {
    logger.info(s"Enriched record mail processed $k->$v")
  }).to(System.getenv(Config.EnrichedTopic))

  branches(1)
    .leftJoin(blackDataTable)(
      (_, user) => user.IP,
      (user, blackData) => if (blackData != null) EnrichedUser(user.name, user.email, user.IP, "bad IP") else null
      //(user, blackData) => if (blackData != null) user.email+"bad email" else null
    ).filter((_, enrichedUser) => enrichedUser != null).peek((k, v) => {
    logger.info(s"Enriched record IP processed $k->$v")
  }).to(System.getenv(Config.EnrichedTopic))

  val streams = new KafkaStreams(builder.build(), props)

  streams.cleanUp()
  streams.start()

  sys.addShutdownHook {
    streams.close(10, TimeUnit.SECONDS)
  }

  object Config {
    val KafkaBrokers = "KAFKA_BROKERS"
    val SchemaRegistry = "SCHEMA_REGISTRY"
    val MainTopic = "MAIN_TOPIC"
    val EnrichmentTopic = "ENRICHMENT_TOPIC"
    val EnrichedTopic = "ENRICHED_TOPIC"
  }

}
