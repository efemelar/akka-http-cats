package akkacats
package futures

import scala.concurrent.Future

trait Library {
  def lend(id: Long, borrowerId: Long): Future[Either[Deny, Loan]]
}

class LibraryInMem extends Library {
  private var loans = collection.concurrent.TrieMap.empty[Long, Loan]

  def lend(id: Long, borrowerId: Long): Future[Either[Deny, Loan]] =
    Future.successful {
      loans.get(id) match {
        case None =>
          val loan = Loan(id, borrowerId)
          loans += (id -> loan)
          Right(loan)
        case Some(l) =>
          Left(Deny(s"To borrow wait until it's brought back by ${l.borrowerId}"))
      }
    }
}
