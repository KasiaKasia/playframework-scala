package models

import anorm.SqlParser._
import anorm._
import play.api.Play.current
import play.api.db._

import scala.language.postfixOps


case class Person(id: Option[Long] = None, first_name: String, last_name: String, website: String, email: String)
case class Project(id: Option[Long] = None, name: String, personId: Option[Long])

case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}


object Person {


  val simple = {
    get[Option[Long]]("person.id") ~
      get[String]("person.first_name") ~
      get[String]("person.last_name") ~
      get[String]("person.website") ~
      get[String]("person.email") map {
      case id~first_name~last_name~website~email => Person(id, first_name, last_name, website, email)
    }
  }

  def options: Seq[(String,String)] = DB.withConnection { implicit connection =>
    SQL("select * from person order by first_name").as(Person.simple *).
      foldLeft[Seq[(String, String)]](Nil) { (cs, c) =>
      c.id.fold(cs) { id => cs :+ (id.toString -> c.first_name) }
    }
  }

}

object Project {

  val simple = {
    get[Option[Long]]("project.id") ~
      get[String]("project.name") ~
      get[Option[Long]]("project.person_id") map {
      case id ~ name ~ personId => Project(id, name, personId)
    }
  }

  val withPerson = Project.simple ~ (Person.simple ?) map {
    case project ~ person => (project, person)
  }


  def findById(id: Long): Option[Project] = {
    DB.withConnection { implicit connection =>
      SQL("select * from project where id = {id}").on('id -> id).as(Project.simple.singleOpt)
    }
  }

  /**
   * @param page Page to display
   * @param pageSize Number of computers per page
   * @param orderBy Computer property used for sorting
   * @param filter Filter applied on the name column
   */
  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[(Project, Option[Person])] = {

    val offest = pageSize * page

    DB.withConnection { implicit connection =>

      val projects = SQL(
        """
          select * from project 
          left join person on project.person_id = person.id
          where project.name like {filter}
          order by {orderBy} nulls last
          limit {pageSize} offset {offset}
        """
      ).on(
          'pageSize -> pageSize,
          'offset -> offest,
          'filter -> filter,
          'orderBy -> orderBy
        ).as(Project.withPerson *)

      val totalRows = SQL(
        """
          select count(*) from project 
          left join person on project.person_id = person.id
          where project.name like {filter}
        """
      ).on(
          'filter -> filter
        ).as(scalar[Long].single)

      Page(projects, page, offest, totalRows)

    }

  }
}