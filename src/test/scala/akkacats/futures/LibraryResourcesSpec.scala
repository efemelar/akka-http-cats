package akkacats
package futures

import akka.http.scaladsl.model._, StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest._

class FuturesLibraryResourcesSpec
    extends WordSpec
    with Matchers
    with ScalatestRouteTest
    with JsonSupport
{

  val lib = new LibraryInMem
  val sut = new LibraryResources(lib)

  "Futures Library" should {

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
      Post("/library/42/loan?borrowerId=24") ~>
      sut.routes ~>
      check {
        status should ===(Conflict)
      }
    }

  }
}
