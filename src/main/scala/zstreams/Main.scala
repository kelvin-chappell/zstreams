package zstreams

import zio._
import zstreams.service.{Capi, CapiLive, ConfigLive}

object Main extends ZIOAppDefault {

  private val program =
    for {
      _       <- Console.print("Query: ").mapError(Failure.fromThrowable)
      query   <- Console.readLine.mapError(Failure.fromThrowable)
      results <- Capi.searchForContent(query)
      _ <- ZIO
        .foreachDiscard(results)(result =>
          Console.printLine(
            s"\n${result.webPublicationDate.map(_.iso8601).getOrElse("Undated")}\n${result.webTitle}\n${result.webUrl}\n",
          ),
        )
        .mapError(Failure.fromThrowable)
    } yield ()

  override def run: ZIO[ZIOAppArgs, Any, Any] = program.provide(ConfigLive.layer, CapiLive.layer)
}
