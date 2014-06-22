package onesky.util

import org.json4s.FieldSerializer
import org.json4s.JsonAST.JField

object SnakeCaseHelper {
  def mangle(jsonName: String): String = {
    val len = jsonName.length
    val sb = new StringBuilder(len, jsonName.substring(0, 1))
    for (i <- 1 until len) {
      val c = jsonName.charAt(i)
      if (jsonName.charAt(i - 1) == '_') {
        sb.append(c.toUpper)
      } else if (c != '_') {
        sb.append(c)
      }
    }
    sb.mkString
  }
}

object SnakeCaseMangler extends FieldSerializer[Any](
  { case kv => Some(kv) },
  { case JField(name, value) => JField(SnakeCaseHelper.mangle(name), value) }
)