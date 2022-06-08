package zstreams.service

import com.gu.contentapi.client.model.v1.Content
import com.gu.contentapi.client.{ContentApiClient, GuardianContentClient}
import zio._
import zstreams.Failure

trait Capi {
  def searchForContent(query: String): ZIO[Any, Failure, List[Content]]
}

object Capi {
  def searchForContent(query: String): ZIO[Capi, Failure, List[Content]] =
    ZIO.serviceWithZIO(_.searchForContent(query))
}

object CapiLive {
  val layer: ZLayer[Config, Failure, Capi] =
    ZLayer.fromZIO(for {
      config <- ZIO.service[Config]
      client = new GuardianContentClient(config.apiKey)
    } yield new Capi {
      override def searchForContent(query: String): ZIO[Any, Failure, List[Content]] =
        ZIO
          .fromFuture { implicit ec =>
            val search = ContentApiClient.search.q(query)
            client.getResponse(search).map(_.results.toList)
          }
          .mapError(Failure.fromThrowable)
    })
}
