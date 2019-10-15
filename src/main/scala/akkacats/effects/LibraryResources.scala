package akkacats
package effects

import akka.http.scaladsl.marshalling._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server._, Directives._
import cats.Functor

abstract class LibraryResources[F[_]: Functor](lib: Library[F])
    extends JsonSupport {

  implicit def fMarshaller[A: ToResponseMarshaller]: ToResponseMarshaller[F[A]]

  def routes: Route =
    ( post &
      path("library" / LongNumber / "loan") &
      parameters("borrowerId".as[Long])
    ) { (itemId, borrowerId) =>
      complete {
        Functor[F].map(lib.lend(itemId, borrowerId)) {
          case Right(r) => (OK, r): ToResponseMarshallable
          case Left(l) => (Conflict, l): ToResponseMarshallable
        }
      }
    }


}
