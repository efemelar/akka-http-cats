package akkacats

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val LoanJsonFormat = jsonFormat2(Loan)
  implicit val DenyJsonFormat = jsonFormat1(Deny)
}