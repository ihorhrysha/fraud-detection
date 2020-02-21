/** MACHINE-GENERATED FROM AVRO SCHEMA. DO NOT EDIT DIRECTLY */
package ua.ucu.edu.model

import scala.annotation.switch

case class EnrichedUser(var name: String, var email: String, var IP: String, var warning: String) extends org.apache.avro.specific.SpecificRecordBase {
  def this() = this("", "", "", "")
  def get(field$: Int): AnyRef = {
    (field$: @switch) match {
      case pos if pos == 0 => {
        name
        }.asInstanceOf[AnyRef]
      case pos if pos == 1 => {
        email
        }.asInstanceOf[AnyRef]
      case pos if pos == 2 => {
        IP
        }.asInstanceOf[AnyRef]
      case pos if pos == 3 => {
        warning
        }.asInstanceOf[AnyRef]
      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
    }
  }
  def put(field$: Int, value: Any): Unit = {
    (field$: @switch) match {
      case pos if pos == 0 => this.name = {
        value.toString
        }.asInstanceOf[String]
      case pos if pos == 1 => this.email = {
        value.toString
        }.asInstanceOf[String]
      case pos if pos == 2 => this.IP = {
        value.toString
        }.asInstanceOf[String]
      case pos if pos == 3 => this.warning = {
        value.toString
        }.asInstanceOf[String]
      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
    }
    ()
  }
  def getSchema: org.apache.avro.Schema = EnrichedUser.SCHEMA$
}

object EnrichedUser {
  val SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"EnrichedUser\",\"namespace\":\"ua.ucu.edu.model\",\"fields\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"email\",\"type\":\"string\"},{\"name\":\"IP\",\"type\":\"string\"},{\"name\":\"warning\",\"type\":\"string\"}]}")
}