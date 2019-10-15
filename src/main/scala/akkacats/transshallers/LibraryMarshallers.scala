package akkacats.transshallers

import akkacats.{Deny, JsonSupport}

trait LibraryMarshallers extends JsonSupport {
  import akka.http.scaladsl.marshalling._
  import akka.http.scaladsl.model._

  implicit val DenyMarshaller: ToResponseMarshaller[Deny] =
    Marshaller
      .fromStatusCodeAndValue[StatusCode, Deny]
      .compose((StatusCodes.Conflict, _))
}
