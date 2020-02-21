/** MACHINE-GENERATED FROM AVRO SCHEMA. DO NOT EDIT DIRECTLY */
package ua.ucu.edu.model

import scala.annotation.switch

case class BlackData(var fieldType: String, var data: String) extends org.apache.avro.specific.SpecificRecordBase {
  def this() = this("", "")
  def get(field$: Int): AnyRef = {
    (field$: @switch) match {
      case pos if pos == 0 => {
        fieldType
        }.asInstanceOf[AnyRef]
      case pos if pos == 1 => {
        data
        }.asInstanceOf[AnyRef]
      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
    }
  }
  def put(field$: Int, value: Any): Unit = {
    (field$: @switch) match {
      case pos if pos == 0 => this.fieldType = {
        value.toString
        }.asInstanceOf[String]
      case pos if pos == 1 => this.data = {
        value.toString
        }.asInstanceOf[String]
      case _ => new org.apache.avro.AvroRuntimeException("Bad index")
    }
    ()
  }
  def getSchema: org.apache.avro.Schema = BlackData.SCHEMA$
}

object BlackData {
  val SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"BlackData\",\"namespace\":\"ua.ucu.edu.model\",\"fields\":[{\"name\":\"fieldType\",\"type\":\"string\"},{\"name\":\"data\",\"type\":\"string\"}]}")
}