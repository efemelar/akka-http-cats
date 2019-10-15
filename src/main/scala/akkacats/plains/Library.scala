package akkacats
package plains

trait Library {
  def lend(itemId: Long, borrowerId: Long): Loan
}

class LibraryInMem extends Library {
  private var loans = collection.concurrent.TrieMap.empty[Long, Loan]

  def lend(itemId: Long, borrowerId: Long): Loan =
    loans.get(itemId) match {
      case None =>
        val loan = Loan(itemId, borrowerId)
        loans += (itemId -> loan)
        loan
      case Some(l) =>
        throw new Exception(s"To borrow wait until it's brought back by ${l.borrowerId}")
          with scala.util.control.NoStackTrace
    }

}
