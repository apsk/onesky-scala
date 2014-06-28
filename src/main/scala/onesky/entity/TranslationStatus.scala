package onesky.entity

case class TranslationStatus(
  fileName: String,
  locale: Language,
  progress: String,
  stringCount: Int,
  wordCount: Int
)