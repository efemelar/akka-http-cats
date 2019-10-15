package akkacats
package transshallers

import cats.Id
import cats.arrow.FunctionK
import cats.data._
import scala.concurrent._

trait Library[F[_]] {
  def lend(id: Long, borrowerId: Long): EitherT[F, Deny, Loan]
}

class LibraryId extends Library[Id] {
  private var loans = collection.concurrent.TrieMap.empty[Long, Loan]

  def lend(id: Long, borrowerId: Long): EitherT[Id, Deny, Loan] = {
    def newLoan = {
      val loan = Loan(id, borrowerId)
      loans += (id -> loan)
      loan
    }

    EitherT
      .fromOption(loans.get(id), newLoan)
      .map(current => Deny(s"To borrow wait until it's brought back ${current.borrowerId}"))
      .swap
  }


}

class LibraryFut extends Library[Future] {
  private val lib = new LibraryId
  private val idToFuture = FunctionK.lift((a: Id[Loan]) => Future.successful(a))

  def lend(id: Long, borrowerId: Long)
  : EitherT[Future, Deny, Loan] =
    lib
      .lend(id, borrowerId)
      .mapK(idToFuture)

}
