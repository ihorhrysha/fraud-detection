package ua.ucu.edu

import ua.ucu.edu.kafka.UserActivityEmulator
import scala.collection.JavaConverters._

object Main extends App {

  val environmentVars = System.getenv().asScala
  for ((k,v) <- environmentVars) println(s"key: $k, value: $v")

  UserActivityEmulator.emulate()

}
