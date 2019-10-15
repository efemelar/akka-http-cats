package akkacats
package transformers

import akka.http.scaladsl.marshalling.{ToResponseMarshallable, ToResponseMarshaller}
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
        lib.lend(itemId, borrowerId).fold(
          deny => ToResponseMarshallable(Conflict, deny),
          loan => ToResponseMarshallable(OK, loan)
        )
      }
    }


}
