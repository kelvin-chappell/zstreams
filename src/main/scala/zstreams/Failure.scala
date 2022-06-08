package zstreams

case class Failure(reason: String)

object Failure {
  def fromThrowable(t: Throwable): Failure = Failure(t.getMessage)
}
