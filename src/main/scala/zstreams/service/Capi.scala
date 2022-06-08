package zstreams.service

import com.gu.contentapi.client.model.v1.Content
import com.gu.contentapi.client.{ContentApiClient, GuardianContentClient}
import zio._
import zio.stream.ZStream
import zstreams.Failure

trait Capi {
  def searchForContent(query: String): ZStream[Any, Failure, Content]
}

object Capi {
  def searchForContent(query: String): ZStream[Capi, Failure, Content] =
    ZStream.serviceWithStream(_.searchForContent(query))
}

object CapiLive {
  val layer: ZLayer[Config, Failure, Capi] =
    ZLayer.fromZIO(for {
      config <- ZIO.service[Config]
      client = new GuardianContentClient(config.apiKey)
    } yield new Capi {

      private def fetchPage(query: String, pageNumber: Int) =
        ZIO
          .fromFuture { implicit ec =>
            val search = ContentApiClient.search.q(query).page(pageNumber)
            client.getResponse(search)
          }
          .mapError(Failure.fromThrowable)

      private def fetchPages(query: String)(pageNumber: Int) =
        for {
          page <- fetchPage(query, pageNumber)
        } yield Chunk.from(page.results) -> (if (page.currentPage < page.pages) Some(pageNumber + 1) else None)

      override def searchForContent(query: String): ZStream[Any, Failure, Content] =
        ZStream.paginateChunkZIO(1)(fetchPages(query))
    })
}
