
import api.MainRouter
import api.user.UserRouter
import cats.syntax.all._
import org.http4s._
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import repositories.DbConfigs
import repositories.user.UserDaoImpl
import services.user.UserServiceImpl
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir._
import zio.interop.catz._
import zio.{Scope, Task, ZIO}

object Main extends zio.ZIOAppDefault {

    def swaggerRoutes(routes: ZServerEndpoint[Any, Any]): HttpRoutes[Task] =
      ZHttp4sServerInterpreter()
        .from(SwaggerInterpreter().fromServerEndpoints(List(routes), "Our pets", "1.0"))
        .toRoutes

  def run: ZIO[Environment with Scope, Any, Any] = {

    val userDao = new UserDaoImpl(DbConfigs.xa)
    val userService = new UserServiceImpl(userDao)
    val userRouter = new UserRouter(userService)
    val mainRouter = new MainRouter(userRouter)


    val routes: HttpRoutes[Task] = ZHttp4sServerInterpreter()
      .from(List(mainRouter.getUser))
      .toRoutes


    ZIO.executor.flatMap(executor =>
      BlazeServerBuilder[Task]
        .withExecutionContext(executor.asExecutionContext)
        .bindHttp(8080, "localhost")
        .withHttpApp(Router("/" -> (routes <+> swaggerRoutes(mainRouter.getUser))).orNotFound)
        .serve
        .compile
        .drain
    )
  }
}
