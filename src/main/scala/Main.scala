import api.MainRouter
import api.user.UserRouter
import cats.syntax.all._
import org.http4s.{HttpRoutes, _}
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import repositories.DbConfigs
import repositories.user.{UserDao, UserDaoImpl}
import services.user.UserServiceImpl
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir._
import zio.interop.catz._
import pureconfig.generic.auto._
import zio.{Scope, Task, ULayer, URLayer, ZIO, ZLayer}
import utils.{MasterTransactor, PureConfig}

object Main extends zio.ZIOAppDefault {

  type EnvIn = MainRouter

  def swaggerRoutes(routes: List[ZServerEndpoint[Any, Any]]): HttpRoutes[Task] =
    ZHttp4sServerInterpreter()
      .from(
        SwaggerInterpreter()
          .fromServerEndpoints(routes, "Volunteer Aid", "1.1")
      )
      .toRoutes

  def makeLayer: ULayer[EnvIn] =
    ZLayer.make[EnvIn](
      // load configs
      PureConfig.load[DbConfigs]("application.conf", "db"),

      // transactor for db
      MasterTransactor.live,

      // user
      UserDaoImpl.live,
      UserServiceImpl.live,
      UserRouter.live,

      // main
      MainRouter.live
    )

  def getEndpoints(router: MainRouter):  List[ZServerEndpoint[Any, Any]] =
    List(
      router.getUser,
      router.authenticateUser,
      router.sigInUser,
      router.updateProfile
    )

  def run: ZIO[Environment with Scope, Any, Any] =
    (for {
      mainRouter <- ZIO.service[MainRouter]
      endpoints = getEndpoints(mainRouter)
      routes: HttpRoutes[Task] = ZHttp4sServerInterpreter()
        .from(endpoints)
        .toRoutes
      _ <-
        ZIO.executor.flatMap(executor =>
          BlazeServerBuilder[Task]
            .withExecutionContext(executor.asExecutionContext)
            .bindHttp(8080, "0.0.0.0")
            .withHttpApp(Router("/" -> (routes <+> swaggerRoutes(endpoints))).orNotFound)
            .serve
            .compile
            .drain
        )
    } yield ())
      .provideLayer(makeLayer)
}
