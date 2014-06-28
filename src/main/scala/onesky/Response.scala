package onesky

import onesky.entity.Language

case class ResponseMetadata(
  status: Int,
  message: Option[String] = None,
  recordCount: Option[Int] = None,
  language: Option[Language] = None
)

sealed trait Response[+T] {
  def status(): Int
}

case class Success[T](meta: ResponseMetadata, value: T) extends Response[T] {
  def status() = meta.status
}

case class Failure(status: Int, message: String) extends Response[Nothing]
