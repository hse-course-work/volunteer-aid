import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import api.MainRouter
import api.user.UserRouter
import sttp.tapir.server.http4s.Http4sServerInterpreter
import zio.{Scope, Task, ZIO}
import zio.interop.catz._
object Main extends zio.ZIOAppDefault {


  def run: ZIO[Environment with Scope, Any, Any] = {
    val interpreter = Http4sServerInterpreter[Task]()

    val userRouter = new UserRouter
    val mainRouter = new MainRouter(userRouter)

    val routes = interpreter.toRoutes(List(
      mainRouter.getUser,
//      mainRouter.getTask
    ))

    BlazeServerBuilder[Task]
      .withHttpApp(Router(("/", routes)).orNotFound)
      .bindHttp(8080, "0.0.0.0")
      .serve
      .compile
      .drain
  }
}
