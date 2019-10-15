package akkacats
package eithers

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server._, Directives._

class LibraryResources(lib: Library)
    extends JsonSupport {

  def routes: Route =
    ( post &
      path("library" / LongNumber / "loan") &
      parameters("borrowerId".as[Long])
    ) { (itemId, borrowerId) =>
      complete {
        lib.lend(itemId, borrowerId) match {
          case Right(r) => (OK, r)
          case Left(l) => (Conflict, l)
        }
      }
    }
}
