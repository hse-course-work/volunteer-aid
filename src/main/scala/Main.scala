import api.MainRouter
import api.rating.RatingRouter
import api.task.TaskRouter
import api.user.UserRouter
import cats.syntax.all._
import models.dao.task.UserTask
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
import repositories.rating.LikeDaoImpl
import repositories.task.TaskDaoImpl
import services.rating.RatingServiceImpl
import services.task.TaskServiceImpl
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
      MainRouter.live,

      // task
      TaskDaoImpl.live,
      TaskServiceImpl.live,
      TaskRouter.live,

      // rating
      LikeDaoImpl.live,
      RatingServiceImpl.live,
      RatingRouter.live
    )

  def getEndpoints(router: MainRouter):  List[ZServerEndpoint[Any, Any]] =
    List(
      router.getUser.tag("Users"),
      router.authenticateUser.tag("Users"),
      router.sigInUser.tag("Users"),
      router.updateProfile.tag("Users"),
      router.deleteUser.tag("Users"),
      // ----
      router.getTask.tag("Tasks"),
      router.createTaskByCreator.tag("Tasks"),
      router.updateTaskWithStatus.tag("Tasks"),
      router.getSomeTasksByStatus.tag("Tasks"),
      router.getSomeTasksByCreator.tag("Tasks"),
      router.deleteTask.tag("Tasks"),
      // ----
      router.getBy.tag("Rating"),
      router.putLike.tag("Rating"),
      router.deleteLike.tag("Rating")
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
