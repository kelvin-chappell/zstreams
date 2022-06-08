package zstreams

import com.gu.contentapi.client.model.v1.Content
import zio._
import zstreams.service.{Capi, CapiLive, ConfigLive}

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

object Main extends ZIOAppDefault {

  private val maxResults = 15

  private def show(index: Long, content: Content) =
    Console
      .printLine(
        Seq(
          s"${index + 1}.",
          content.webPublicationDate
            .map(dateTime => OffsetDateTime.parse(dateTime.iso8601).format(DateTimeFormatter.ofPattern("d MMMM yyyy")))
            .getOrElse("Undated"),
          content.webTitle,
          content.webUrl
        ).mkString("\n", "\n", "\n")
      )
      .mapError(Failure.fromThrowable)

  private val program =
    for {
      _     <- Console.print("Query: ").mapError(Failure.fromThrowable)
      query <- Console.readLine.mapError(Failure.fromThrowable)
      _ <- Capi.searchForContent(query).take(maxResults).zipWithIndex.foreach { case (content, index) =>
        show(index, content)
      }
    } yield ()

  override def run: ZIO[Any, Failure, Unit] = program.provide(ConfigLive.layer, CapiLive.layer)
}
