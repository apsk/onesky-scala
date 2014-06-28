# onesky-scala

## Installation

### sbt

```scala
// not yet
```

## Usage

### Example initialization (parameterized module style):

```scala
object MyOneSky extends OneSky(
  apiKey = readLine("public key: "),
  apiSecret = readLine("secret key: ")
)
```

### Get the list of projects for the given group:

```scala
object Main extends App {
  import MyOneSky._

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
