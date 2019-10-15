package akkacats

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.{GenericMarshallers, ToResponseMarshaller}
import akka.http.scaladsl.server._
import RouteConcatenation.concat
import akka.http.scaladsl.model.HttpResponse
import akka.stream.ActorMaterializer
import cats._
import implicits._

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}


object Boot extends App with Directives {

  implicit val system: ActorSystem = ActorSystem("helloAkkaHttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  val plainsResources =
    new plains.LibraryResources(new plains.LibraryInMem)

  val eithersResources =
    new eithers.LibraryResources(new eithers.LibraryInMem)

  val futuresResources =
    new futures.LibraryResources(new futures.LibraryInMem)

  val effectsResources = {
    import effects._
    new LibraryResources(new LibraryFut) {
      def fMarshaller[A: ToResponseMarshaller]: ToResponseMarshaller[Future[A]] =
        GenericMarshallers.futureMarshaller
    }
  }

  val transformersResources = {
    import transformers._
    new LibraryResources(new LibraryFut) {
      def fMarshaller[A: ToResponseMarshaller]: ToResponseMarshaller[Future[A]] =
        GenericMarshallers.futureMarshaller
    }
  }

  val transshallersResources = {
    import transshallers._
    new LibraryResources(new LibraryFut) with CatsMarshallers.ForFuture
  }

  lazy val routes: Route =
    concat(
      pathPrefix("plains")(plainsResources.routes),
      pathPrefix("eithers")(eithersResources.routes),
      pathPrefix("futures")(futuresResources.routes),
      pathPrefix("effects")(effectsResources.routes),
      pathPrefix("transformers")(transformersResources.routes),
      pathPrefix("transshallers")(transshallersResources.routes),
    )

  val serverBinding: Future[Http.ServerBinding] = Http().bindAndHandle(routes, "localhost", 8080)

  serverBinding.onComplete {
    case Success(bound) =>
      println(s"Server online at http://${bound.localAddress.getHostString}:${bound.localAddress.getPort}/")
    case Failure(e) =>
      Console.err.println(s"Server could not start!")
      e.printStackTrace()
      system.terminate()
  }

  Await.result(system.whenTerminated, Duration.Inf)
}
