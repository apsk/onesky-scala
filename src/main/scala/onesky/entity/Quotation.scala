package onesky.entity

case class Quotation(
  files: List[QuotationFile],
  fromLanguage: Language,
  toLanguage: Language,
  isIncludingNotTranslated: Boolean,
  isIncludingNotApproved: Boolean,
  isIncludingOutdated: Boolean,
  specialization: String,
  translationOnly: QuotationInfo,
  translationAndReview: QuotationInfo,
  reviewOnly: QuotationInfo
)

case class QuotationFile(name: String)

case class QuotationTranslator(
  willCompleteAt: String,
  willCompleteAtTimestamp: Long,
  secondsToComplete: Long
)

case class QuotationInfo(
  stringCount: Int,
  wordCount: Int,
  perWordCost: String,
  totalCost: String,
  willCompleteAt: String,
  willCompleteAtTimestamp: Long,
  secondsToComplete: Long,
  preferredTranslator: QuotationTranslator
)
