package akkacats
package effects

import cats.Id
import scala.concurrent.Future

trait Library[F[_]] {
  def lend(id: Long, borrowerId: Long): F[Either[Deny, Loan]]
}

class LibraryId extends Library[Id] {
  private var loans = collection.concurrent.TrieMap.empty[Long, Loan]

  def lend(id: Long, borrowerId: Long): Id[Either[Deny, Loan]] = {
    loans.get(id) match {
      case None =>
        val loan = Loan(id, borrowerId)
        loans += (id -> loan)
        Right(loan)
      case Some(l) =>
        Left(Deny(s"To borrow wait until it brought back ${l.borrowerId}"))
    }
  }
}

class LibraryFut extends Library[Future] {
  private val lib = new LibraryId

  def lend(id: Long, borrowerId: Long): Future[Either[Deny, Loan]] =
    Future.successful {
      lib.lend(id, borrowerId)
    }
}
