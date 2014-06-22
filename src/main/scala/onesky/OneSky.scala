package onesky

import onesky.entity._
import java.security.MessageDigest
import onesky.util.{SnakeCaseMangler, UnitSerializer}

import scalaj.http.Http
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.DefaultWriters.StringWriter
import org.json4s.NoTypeHints
import org.json4s.native.Serialization

class OneSky(
  apiKey: String,
  apiSecret: String,
  apiURL: String = "https://platform.api.onesky.io/1/"
) {
  private implicit val formats = Serialization.formats(NoTypeHints) + UnitSerializer + SnakeCaseMangler
  private val md5 = MessageDigest.getInstance("MD5")

  def mkAuthParams(): Map[String, String] = {
    val timestamp = (System.currentTimeMillis / 1000).toString
    val devHash = new String(md5.digest((timestamp + apiSecret).getBytes))
    Map(
      "api_key" -> apiKey,
      "timestamp" -> timestamp,
      "dev_hash" -> devHash
    )
  }

  def performRequest[T](
    url: String,
    params: Map[String, String] = Map(),
    authRequired: Boolean = true,
    method: String = "GET"
  )(
    implicit mf: Manifest[T]
  ): Response[T] = {
    val json = Http(apiURL + url).method(method)
      .header("Content-Type", "application/json")
      .params(if (!authRequired) params else params ++ mkAuthParams)
      .asString.asJValue
    val meta = (json \ "meta").extract[ResponseMetadata]
    if (meta.statusCode > 199 && meta.statusCode < 301) {
      Success(meta, (json \ "data").extract[T])
    } else {
      Failure(meta.statusCode, meta.message.getOrElse("Unknown error"))
    }
  }

  object ProjectGroup {
    def list(page: Option[Int] = None, perPage: Option[Int] = None) = {
      require(page.forall(_ > 0), "page should be greater than zero")
      require(perPage.forall(x => 0 < x && x < 101), "perPage should be in range [1,100]")
      performRequest[List[ProjectGroup]]("project-groups", Map(
        "page" -> page.map(_.toString).orNull,
        "per_page" -> perPage.map(_.toString).orNull
      ))
    }

    def show(id: Int) = performRequest[ProjectGroupDetails]("project-groups/" + id)

    def create(name: String, locale: String = null) =
      performRequest[Unit]("project-groups",
        method = "POST",
        params = Map(
          "name" -> name,
          "locale" -> locale
        )
      )

    def delete(id: Int) = performRequest[Unit]("project-groups/" + id, method = "DELETE")

    def languages(id: Int) = performRequest[List[Language]](s"project-groups/$id/languages")
  }

  object Project {
    def list(groupID: Int, page: Option[Int] = None, perPage: Option[Int] = None) = {
      require(page.forall(_ > 0), "page should be greater than zero")
      require(perPage.forall(x => 0 < x && x < 101), "perPage should be in range [1,100]")
      performRequest[List[Project]](s"project-groups/$groupID/projects", Map(
        "page" -> page.map(_.toString).orNull,
        "per_page" -> perPage.map(_.toString).orNull
      ))
    }

    def show(id: Int) = performRequest[ProjectDetails]("projects/" + id)

    def create(groupID: Int, projectType: String, name: String = null, description: String = null) =
      performRequest[Unit](s"project-groups/$groupID/projects",
        method = "POST",
        params = Map(
          "project_type" -> projectType,
          "name" -> name,
          "description" -> description
        )
      )

    def update(id: Int, name: String = null, description: String = null) =
      performRequest[Unit]("projects/" + id,
        method = "PUT",
        params = Map(
          "name" -> name,
          "description" -> description
        )
      )

    def delete(id: Int) = performRequest[Unit]("projects/" + id, method = "DELETE")

    def languages(id: Int) = performRequest[List[Language]](s"projects/$id/languages")
  }

  object ProjectType {
    def list() = performRequest[List[ProjectType]]("projects-types")
  }

  object File {
    def list(projectID: Int, page: Option[Int] = None, perPage: Option[Int] = None) = {
      require(page.forall(_ > 0), "page should be greater than zero")
      require(perPage.forall(x => 0 < x && x < 101), "perPage should be in range [1,100]")
      performRequest[List[File]](s"projects/$projectID/files", Map(
        "page" -> page.map(_.toString).orNull,
        "per_page" -> perPage.map(_.toString).orNull
      ))
    }

    // 1

    // 2
  }

  object Translation {}

  object ImportTask {}

  object Screenshot {}

  object Quotation {}

  object Order {}

  object Locale {
    def list(page: Option[Int] = None, perPage: Option[Int] = None) = {
      require(page.forall(_ > 0), "page should be greater than zero")
      require(perPage.forall(x => 0 < x && x < 101), "perPage should be in range [1,100]")
      performRequest[List[ProjectGroup]]("locales", Map(
        "page" -> page.map(_.toString).orNull,
        "per_page" -> perPage.map(_.toString).orNull
      ))
    }
  }
}
