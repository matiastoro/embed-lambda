
import scala.tools.nsc
import nsc.Global
import nsc.Phase
import nsc.plugins.Plugin
import nsc.plugins.PluginComponent
import nsc.transform.Transform
import nsc.transform.InfoTransform
import nsc.transform.TypingTransformers
import nsc.symtab.Flags._
import nsc.ast.TreeDSL
import nsc.typechecker

class EmbedLambdaPlugin(val global: Global) extends Plugin {
  val name = "embed-lambda-plugin"
  val description = "transform left-side of a function application into a lambda"
  //val components = new TemplateTransformComponent(this, global) :: Nil
  val components = new EmbedLambda(this, global) :: Nil
  //val components = Nil
     
}
  
class EmbedLambda(plugin:Plugin, val global:Global) extends PluginComponent
with Transform with TypingTransformers with TreeDSL {
  import global._

  val runsAfter = "typer" :: Nil
  val phaseName = "embed-lambda"

  def newTransformer(unit:CompilationUnit) = new MyTransformer(unit)
  class MyTransformer(unit:CompilationUnit) extends TypingTransformer(unit) {
   
    override def transform(tree: Tree): Tree = {
      super.transform(transformLambda(tree))
    }
    case class Transformed(b: Boolean)

    def transformLambda(tree: Tree): Tree = {
      import EmbedLambda.this.global._

      tree match{
        case Apply(fun, args) =>

          val params = fun.symbol.paramss.flatten

          //For simplicity, only transform 1 parameter arguments . Don't transform if we already transformed it
          if(params.size>0 && !tree.attachments.get[Transformed].isDefined){

            val applyFun = Apply(fun, List(Ident(newTermName("x$1"))))

            applyFun.updateAttachment[Transformed](Transformed(true))

            val lambda =
              Function(
                List(ValDef(Modifiers(scala.reflect.internal.Flags.PARAM | scala.reflect.internal.Flags.SYNTHETIC), newTermName("x$1"), TypeTree(), EmptyTree)),
                applyFun)
            val typedLambda = typer.typed(lambda)

            //apply the lambda to the argument
            val newTree = typer.typed(Apply(Select(typedLambda, newTermName("apply")), args))
            println(tree, newTree.symbol.owner, tree.symbol.owner)
            newTree.changeOwner((newTree.symbol.owner, tree.symbol.owner))

            newTree

          } else tree
        case _ => tree
      }

    }
  }
}
