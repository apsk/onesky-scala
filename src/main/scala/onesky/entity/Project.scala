package onesky.entity

case class Project(id: Int, name: String)

case class ProjectDetails(
  id: Int,
  name: String,
  description: String,
  projectType: ProjectType,
  stringCount: Int,
  wordCount: Int
)