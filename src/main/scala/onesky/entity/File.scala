package onesky.entity

case class FileImport(
  id: Int,
  status: String
)

case class File(
  name: String,
  stringCount: Int,
  lastImport: Option[FileImport],
  uploadedAt: Option[String],
  uploadedAtTimestamp: Option[String]
)
