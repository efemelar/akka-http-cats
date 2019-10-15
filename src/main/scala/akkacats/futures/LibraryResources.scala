package akkacats
package futures

import akka.http.scaladsl.model.StatusCodes.{Success => _, _}
import akka.http.scaladsl.server._, Directives._
import scala.util._

class LibraryResources(lib: Library)
    extends JsonSupport {

  def routes: Route =
    ( post &
      path("library" / LongNumber / "loan") &
      parameters("borrowerId".as[Long])
    ) { (itemId, borrowerId) =>
      onComplete(lib.lend(itemId, borrowerId)) {
        case Success(s) =>
          complete {
            s match {
              case Right(r) => (OK, r)
              case Left(l) => (Conflict, l)
            }
          }
        case Failure(e) =>
          complete((InternalServerError, e.getMessage))
      }
    }
}
