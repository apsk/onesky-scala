package onesky.entity

case class ImportTaskFile(name: String)

case class ImportTaskFileDetails(
  name: String,
  format: String,
  locale: Language
)

case class ImportTask(
  id: Int,
  file: ImportTaskFile,
  status: String,
  createdAt: String,
  createdAtTimestamp: Long
)

case class ImportTaskDetails(
  id: Int,
  file: ImportTaskFileDetails,
  stringCount: Int,
  wordCount: Int,
  status: String,
  createdAt: String,
  createdAtTimestamp: Long
)