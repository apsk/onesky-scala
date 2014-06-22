package onesky.util

import org.json4s.CustomSerializer
import org.json4s.JsonAST._

object UnitSerializer extends CustomSerializer[Unit](_ => (
  { case _ => () },
  { case _: Unit => JObject(List()) }
))