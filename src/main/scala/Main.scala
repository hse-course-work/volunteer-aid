import api.MainRouter
import api.push.PushRouter
import api.rating.RatingRouter
import api.report.ReportRouter
import api.task.TaskRouter
import api.user.UserRouter
import cats.syntax.all._
import models.dao.task.UserTask
import org.http4s._
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import repositories.DbConfigs
import services.user.UserServiceImpl
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir._
import zio.interop.catz._
import pureconfig.generic.auto._
import repositories.hashtags.HashtagDaoImpl
import repositories.push.PushDaoImpl
import repositories.rating.LikeDaoImpl
import repositories.reports.ReportDaoImpl
import repositories.task.TaskDaoImpl
import repositories.user.{UserDao, UserDaoImpl}
import services.hashtag.HashtagServiceImpl
import services.push.PushServiceImpl
import services.rating.RatingServiceImpl
import services.reports.ReportServiceImpl
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
      RatingRouter.live,

      // hashtag
      HashtagDaoImpl.live,
      HashtagServiceImpl.live,

      // push
      PushDaoImpl.live,
      PushServiceImpl.live,
      PushRouter.live,

      // report
      ReportDaoImpl.live,
      ReportServiceImpl.live,
      ReportRouter.live,
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
      router.getTakenTask.tag("Task"),
      router.takeTaskInWork.tag("Task"),
      router.removeFromWork.tag("Task"),
      // ----
      router.getLikesBy.tag("Rating"),
      router.putLike.tag("Rating"),
      router.deleteLike.tag("Rating"),
      // ----
      router.addHashtag.tag("Hashtag"),
      router.deleteHashtag.tag("Hashtag"),
      router.searchByTags.tag("Hashtag"),
      // ----
      router.getPushesForUser.tag("Push"),
      // ----
      router.addUserReport.tag("Report"),
      router.deleteUserReport.tag("Report"),
      router.getReportForUser.tag("Report"),
      router.getTasksReportForUser.tag("Report"),
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
