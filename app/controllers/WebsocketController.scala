package controllers

import java.util.UUID
import javax.inject.{Inject, Singleton}

import akka.stream.ThrottleMode
import akka.stream.scaladsl.{Flow, Sink, Source}
import domain.Domain
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}

import scala.util.Random
import scala.concurrent.duration._

@Singleton
class WebsocketController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  private val rnd = new Random()

  def socket = WebSocket.accept[String, String] { request =>
    // Can receive inbound messages, ignores input
    val in = Sink.ignore

    // Send outbound JSON string messages
    val out =
      Source
        .fromIterator[JsValue](() => Iterator.continually(rndJsonArray))
        .take(10)
        .map(Json.prettyPrint)
        .throttle(1, 1.second, 1, ThrottleMode.Shaping)

    Flow.fromSinkAndSource(in, out)
  }

  private def rndJsonArray =
    Json.toJson(
      Seq.tabulate(rnd.nextInt(8))(_ =>
        Domain.Test(
          id = UUID.randomUUID(),
          id2 = UUID.randomUUID(),
          name = rnd.alphanumeric.take(8).mkString,
          extra = rnd.nextInt(10)
        )
      )
    )

}