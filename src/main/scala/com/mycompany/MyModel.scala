package com.mycompany

import zio.json.{DeriveJsonDecoder, JsonDecoder}

case class MyModel(id: Long, country: String)

object MyModel {
  implicit val decoder: JsonDecoder[MyModel] = DeriveJsonDecoder.gen[MyModel]
}
