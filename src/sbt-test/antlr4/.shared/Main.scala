import example.expr.{ExprLexer, ExprParser}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}

object Main {
  def main(args: Array[String]): Unit = {
    val lexer = new ExprLexer(CharStreams.fromString("1 + 2 + 3"))
    val parser = new ExprParser(new CommonTokenStream(lexer))
    val tree = parser.expr()
    assert(tree.INT().size() == 3, s"expected 3 INTs, got ${tree.INT().size()}")
    println("ok")
  }
}
