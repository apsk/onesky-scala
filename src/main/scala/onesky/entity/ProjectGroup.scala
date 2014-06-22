package onesky.entity

case class ProjectGroup(id: Int, name: String)

case class ProjectGroupDetails(
  id: Int,
  name: String,
  baseLanguage: Language,
  enabledLanguageCount: Int,
  projectCount: Int
)
