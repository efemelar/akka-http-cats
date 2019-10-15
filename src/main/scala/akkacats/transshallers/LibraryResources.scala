package akkacats
package transshallers

import akka.http.scaladsl.server._, Directives._

abstract class LibraryResources[F[_]](lib: Library[F])
    extends LibraryMarshallers
    with CatsMarshallers[F] {

  def routes: Route =
    ( post &
      path("library" / LongNumber / "loan") &
      parameters("borrowerId".as[Long])
    ) { (itemId, borrowerId) =>
      complete { lib.lend(itemId, borrowerId) }
    }
}


