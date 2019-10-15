package akkacats
package transformers

import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.model._, StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import cats.Id
import org.scalatest._

class LibraryResourcesSpec
    extends WordSpec
    with Matchers
    with ScalatestRouteTest
    with JsonSupport
{

  val lib = new LibraryId
  val sut = new LibraryResources(lib) {
    def fMarshaller[A: ToResponseMarshaller]: ToResponseMarshaller[Id[A]] =
      implicitly[ToResponseMarshaller[A]]
  }

  "Transformers Library" should {

    "let borrow items" in {
      Post("/library/42/loan?borrowerId=1") ~>
      sut.routes ~>
      check {
        status should ===(OK)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[Loan] should ===(Loan(42, 1))
      }
    }

    "not let borrow already lent items" in {
      Post("/library/42/loan?borrowerId=2") ~>
      sut.routes ~>
      check {
        status should ===(Conflict)
      }
    }

  }
}
