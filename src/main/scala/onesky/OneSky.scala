package onesky

import onesky.entity._
import onesky.util.{SnakeCaseMangler, UnitSerializer}

import java.security.MessageDigest
import java.nio.file._
import scalaj.http.{MultiPart, Http}
import scalaj.http.Http.Request
import org.json4s._
import org.json4s.NoTypeHints
import org.json4s.native.Serialization
import org.json4s.native.JsonMethods._
import org.json4s.DefaultWriters.StringWriter

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
    req: Request,
    params: Map[String, String],
    authRequired: Boolean
  )(
    implicit mf: Manifest[T]
  ): Response[T] = {
    val json = req
      .params(if (!authRequired) params else params ++ mkAuthParams)
      .asString.asJValue
    val meta = (json \ "meta").extract[ResponseMetadata]
    if (meta.statusCode > 199 && meta.statusCode < 301) {
      Success(meta, (json \ "data").extract[T])
    } else {
      Failure(meta.statusCode, meta.message.getOrElse("Unknown error"))
    }
  }

  def performRequest[T](
    url: String,
    params: Map[String, String] = Map(),
    authRequired: Boolean = true,
    method: String = "GET"
  )(
    implicit mf: Manifest[T]
  ): Response[T] =
    performRequest[T](
      Http(apiURL + url).method(method)
        .header("Content-Type", "application/json"),
      params,
      authRequired
    )

  def performMultiPartRequest[T](
    url: String,
    parts: List[MultiPart] = Nil,
    params: Map[String, String] = Map(),
    authRequired: Boolean = true
  )(
    implicit mf: Manifest[T]
  ): Response[T] =
    performRequest(
      Http.multipart(apiURL + url, parts: _*),
      params,
      authRequired
    )

  def performFileDownloadRequest(
    url: String,
    saveTo: Path,
    params: Map[String, String] = Map(),
    authRequired: Boolean = true,
    method: String = "GET",
    copyOptions: List[CopyOption] = List(StandardCopyOption.REPLACE_EXISTING)
  ): Response[Path] = {
    val (statusCode, _, _) = Http(apiURL + url).asHeadersAndParse { in =>
      Files.copy(in, saveTo, copyOptions: _*)
    }
    if (statusCode > 199 && statusCode < 301) {
      Success(ResponseMetadata(statusCode), saveTo)
    } else {
      Failure(statusCode, "Unknown error")
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

    def create(name: String, locale: Option[String] = None) =
      performRequest[Unit]("project-groups",
        method = "POST",
        params = Map(
          "name" -> name,
          "locale" -> locale.orNull
        )
      )

    def delete(id: Int) = performRequest[Unit]("project-groups/" + id, method = "DELETE")

    def languages(id: Int) = performRequest[List[Language]](s"project-groups/$id/languages")
  }

  object Project {
    def list(groupID: Int) = performRequest[List[Project]](s"project-groups/$groupID/projects")

    def show(id: Int) = performRequest[ProjectDetails]("projects/" + id)

    def create(groupID: Int, projectType: String, name: Option[String] = None, description: Option[String] = None) =
      performRequest[Unit](s"project-groups/$groupID/projects",
        method = "POST",
        params = Map(
          "project_type" -> projectType,
          "name" -> name.orNull,
          "description" -> description.orNull
        )
      )

    def update(id: Int, name: Option[String] = None, description: Option[String] = None) =
      performRequest[Unit]("projects/" + id,
        method = "PUT",
        params = Map(
          "name" -> name.orNull,
          "description" -> description.orNull
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

    def upload(
      projectID: Int,
      localPath: String,
      remoteName: String,
      format: String,
      locale: Option[String] = None,
      keepAllStrings: Option[Boolean] = None
    ) =
      performMultiPartRequest[Unit](
        s"projects/$projectID/files",
        parts = List(MultiPart("attachment", remoteName, "text/plain", Files.readAllBytes(Paths.get(localPath)))),
        params = Map(
          "file" -> remoteName,
          "file_format" -> format,
          "locale" -> locale.orNull,
          "is_keeping_all_strings" -> keepAllStrings.map(_.toString).orNull
        ),
        authRequired = true
      )

    def delete(projectID: Int, fileName: String) = performRequest[Unit](s"projects/$projectID/files", method = "DELETE")
  }

  object Translation {
    def export(projectID: Int, locale: String, sourceName: String, saveTo: Path, exportName: Option[String] = None) =
      performFileDownloadRequest(s"projects/$projectID/files/translations", saveTo, Map(
        "locale" -> locale,
        "source_file_name" -> sourceName,
        "export_file_name" -> exportName.orNull
      ))
  }

  object ImportTask {

  }

  object Screenshot {

  }

  object Quotation {

  }

  object Order {

  }

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
