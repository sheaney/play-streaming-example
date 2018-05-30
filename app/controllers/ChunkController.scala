package controllers

import java.util.UUID
import javax.inject._

import akka.stream.scaladsl.Source
import domain.Domain
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.duration._
import scala.util.Random

@Singleton
class ChunkController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  private val rnd = new Random()

  def infinite() = Action {
    Ok.chunked(infiniteStream)
  }

  def bounded() = Action {
    Ok.chunked(boundedStream)
  }

  private val infiniteStream =
    Source.tick(1.second, 3.seconds, ()).map(_ => Json.prettyPrint(rndJsonArray))

  private val boundedStream =
    infiniteStream.take(3)

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