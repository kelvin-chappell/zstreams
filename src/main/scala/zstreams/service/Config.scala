package zstreams.service

import zio._
import zstreams.Failure

case class Config(apiKey: String)

object ConfigLive {

  private val load =
    (for {
      optApiKey <- zio.System.env("API_KEY")
      apiKey    <- ZIO.fromOption(optApiKey).orElseFail(new IllegalArgumentException("No API key in environment"))
    } yield Config(apiKey))
      .mapError(Failure.fromThrowable)

  val layer: ZLayer[Any, Failure, Config] = ZLayer.fromZIO(load)
}
