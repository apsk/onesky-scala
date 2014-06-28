# onesky-scala

## Installation

### sbt

```scala
// not yet
```

## Usage

### Get the list of projects for the given group:

```scala
import onesky._

object Main extends App {
  val OneSky = new OneSky(
    apiKey = "XfeAaswL1JlE7WejcRMeKugaw26AwpVk",
    apiSecret = "q2wTMvn5LznKzyKKlpNDc3Rbzcv5d9su"
  )

  import OneSky._

  Project.list(4201) match {
    case Success(_, projects) => projects.foreach { p => println(s"Project #${p.id}: ${p.name}") }
    case Failure(status, message) => println(s"Error $status: $message")
  }
}
```

##### *example output:*
```
Project #11048: Quux
Project #11049: Bar-Baz
```
