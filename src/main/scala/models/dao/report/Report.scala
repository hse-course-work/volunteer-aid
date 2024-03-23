package models.dao.report

case class Report(
    id: Long,
    taskIdFor: Long,
    reportAuthorId: Long,
    comment: Option[String],
    photoData: Option[List[Byte]])
