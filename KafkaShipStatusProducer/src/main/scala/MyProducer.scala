package com.example
import java.util.Properties
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import org.apache.kafka.clients.producer.ProducerRecord

trait A
final case class workSend(status: String) extends A
final case object StopMessage extends A
final case object  masterSend extends A
final case object  workDone extends A

object AkkaQuickstart extends App {
  val system = ActorSystem("ShipStatusSenderSystem")
  val worker = system.actorOf(Props[Worker], name = "worker")
  val master = system.actorOf(Props(new Master(worker)), name = "master")
  master ! masterSend


  class Master(worker : ActorRef) extends Actor {
    def receive = {
      case masterSend => {
        val status = Console.in.readLine()
        worker ! workSend(status)
      }
      case workDone => {
        KafkaProducer.close()
        system.terminate()


      }
    }
  }

  class Worker extends Actor {
    def receive = {
      case workSend(message) =>
        if(message.equalsIgnoreCase("stop"))
          sender ! workDone
        else {
          KafkaProducer.send(message)
          sender ! masterSend
        }
    }
  }
}

object KafkaProducer {
  private val TOPIC_NAME = "streams-plaintext-input"
  private val KAFKA_CLUSTER_ENV_VAR_NAME = "KAFKA_CLUSTER"

  import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig}
  val kafkaProps = new Properties()
  val defaultClusterValue = "localhost:9092"
  val kafkaCluster: String = System.getProperty(KAFKA_CLUSTER_ENV_VAR_NAME, defaultClusterValue)
  kafkaProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaCluster)
  kafkaProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  kafkaProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  kafkaProps.put(ProducerConfig.ACKS_CONFIG, "0")

  val producer =  new KafkaProducer[String,String](kafkaProps)


  def send(message :String): Unit = {
    val key = "machine-1"
    val record = new ProducerRecord(TOPIC_NAME, key, message)
    producer.send(record)

  } 
  def close() = {
    producer.close()
  }
}