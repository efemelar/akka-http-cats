package akkacats.transshallers

import akka.http.scaladsl.marshalling._
import cats.Id
import cats.data.EitherT
import scala.concurrent.Future

trait CatsMarshallers[F[_]] {

  implicit def liftTRM[A: ToResponseMarshaller]: ToResponseMarshaller[F[A]]

  implicit def eitherTMarshaller
      [L: ToResponseMarshaller, R: ToResponseMarshaller]
      : ToResponseMarshaller[EitherT[F, L, R]] =
    liftTRM[Either[L, R]].compose(_.value)
    //  Marshaller.combined(_.value)

}


object CatsMarshallers {
  trait ForId extends CatsMarshallers[Id] {
    implicit def liftTRM[A: ToResponseMarshaller]: ToResponseMarshaller[Id[A]] =
      implicitly[ToResponseMarshaller[A]]
  }

  trait ForFuture extends CatsMarshallers[Future] {
    implicit def liftTRM[A: ToResponseMarshaller]: ToResponseMarshaller[Future[A]] =
      GenericMarshallers.futureMarshaller
  }
}



