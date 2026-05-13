import example.hello.{HelloLexer, HelloParser}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}

object Main {
  def main(args: Array[String]): Unit = {
    val lexer = new HelloLexer(CharStreams.fromString("hello world"))
    val parser = new HelloParser(new CommonTokenStream(lexer))
    val tree = parser.greet()
    assert(tree.ID().getText == "world", s"unexpected: ${tree.ID().getText}")
    println("ok")
  }
}
