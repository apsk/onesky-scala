package onesky.entity

case class Language(
  code: String,
  englishName: String,
  localName: String,
  locale: String,
  region: String,
  isBaseLanguage: Option[Boolean]
)
