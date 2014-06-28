package onesky.entity

case class Order(
  id: Int,
  status: String,
  orderedAt: String,
  orderedAtTimestamp: Long
)

case class OrderFile(name: String)

case class OrderTranslator(name: String)

case class OrderTask(
  status: String,
  toLanguage: Language,
  translator: OrderTranslator,
  stringCount: Int,
  wordCount: Int,
  willCompeteAt: String,
  willCompeteAtTimestamp: Long,
  secondsToComplete: Long,
  competedAt: String,
  competedAtTimestamp: Long
)

case class OrderDetails(
  id: Int,
  status: String,
  amount: String,
  files: List[File],
  fromLanguage: Language,
  toLanguage: Language,
  tasks: List[OrderTask],
  orderType: String,
  tone: String,
  specialization: String,
  note: String,
  orderedAt: String,
  orderedAtTimestamp: Long
)

