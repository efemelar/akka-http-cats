package akkacats
package plains

import akka.http.scaladsl.model._, StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest._

class LibraryResourcesSpec
    extends WordSpec
    with Matchers
    with ScalatestRouteTest
    with JsonSupport
{

  val lib = new LibraryInMem
  val sut = new LibraryResources(lib)

  "Plains library" should {

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
        status should ===(InternalServerError)
      }
    }

  }
}
