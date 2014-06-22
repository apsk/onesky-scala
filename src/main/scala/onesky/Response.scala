package onesky

import onesky.entity.Language

case class ResponseMetadata(
  statusCode: Int,
  message: Option[String],
  recordCount: Option[Int],
  language: Option[Language]
)

sealed trait Response[+T] {
  def statusCode(): Int
}

case class Success[T](meta: ResponseMetadata, value: T) extends Response[T] {
  def statusCode() = meta.statusCode
}

case class Failure(statusCode: Int, message: String) extends Response[Nothing]
