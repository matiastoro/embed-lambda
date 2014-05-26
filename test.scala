object test {
  def main(args: Array[String]):Unit = {
    def f(g: Int => Int) = {
      g(10)
    }

    def f2(x: Int) = x
    f2(1)
  }
}
