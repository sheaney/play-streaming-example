package domain

import java.util.UUID

import play.api.libs.json.Json

object Domain {

  case class Test(id: UUID, id2: UUID, name: String, extra: Int)

  implicit val format = Json.format[Domain.Test]

}