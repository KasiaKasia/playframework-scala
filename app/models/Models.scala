package models

import java.util.Date
import anorm.SqlParser._
import anorm._
import play.api.Play.current
import play.api.db._
import scala.language.postfixOps


case class Project(id: Option[Long] = None, name: String)
case class Person(id: Option[Long] = None, first_name: String, last_name: String, website: String, email: String, is_active: Boolean, date_joined: Date, projectId: Option[Long])

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
      get[String]("person.email") ~
      get[Boolean]("person.is_active") ~
      get[Date]("person.date_joined") ~
      get[Option[Long]]("person.project_id") map {
      case id~first_name~last_name~website~email~is_active~date_joined~projectId => Person(id, first_name, last_name, website, email, is_active, date_joined, projectId)
    }
  }

  val withProject = Person.simple ~ (Project.simple ?) map {
    case person~project  => (person, project)
  }


  def findById(id: Long): Option[Person] = {
    DB.withConnection { implicit connection =>
      SQL("select * from person where id = {id}").on('id -> id).as(Person.simple.singleOpt)
    }
  }

  /**
   *
   * @param page Wyświetlenie strony
   * @param pageSize Liczba osob na stronie
   * @param orderBy Sortowanie
   * @param filter Filter
   */
  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[(Person, Option[Project])] = {

    val offest = pageSize * page

    DB.withConnection { implicit connection =>

      val persons = SQL(
        """
          select * from person
          left join project on person.project_id = project.id
          where person.first_name like {filter}
          order by {orderBy} nulls last
          limit {pageSize} offset {offset}
        """
      ).on(
          'pageSize -> pageSize,
          'offset -> offest,
          'filter -> filter,
          'orderBy -> orderBy
        ).as(Person.withProject *)

      val totalRows = SQL(
        """
          select count(*) from project
          left join person on person.project_id = project.id
          where person.first_name like {filter}
        """
      ).on(
          'filter -> filter
        ).as(scalar[Long].single)

      Page(persons, page, offest, totalRows)

    }

  }


  def insert(person: Person) = {
  DB.withConnection { implicit connection =>
    SQL(
      """
          insert into person values (
            (select next value for person_seq),
            {first_name}, {last_name}, {website}, {email}, {is_active}, {date_joined}, {project_id}
          )
      """
    ).on(
        'first_name -> person.first_name,
        'last_name -> person.last_name,
        'website -> person.website,
        'email -> person.email,
        'is_active -> person.is_active,
        'date_joined -> person.date_joined,
        'project_id -> person.projectId
      ).executeUpdate()
    }
  }

  /**
   *
   * @param id ID Person
   * @param person rekordy Person
   */
  def updatePerson(id: Long, person: Person) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          update person
            set first_name = {first_name},
            last_name = {last_name},
            website = {website},
            email = {email},
            is_active = {is_active},
            date_joined = {date_joined},
            project_id = {project_id}
            where id = {id}
        """
      ).on(
          'id -> id,
          'first_name -> person.first_name,
          'last_name -> person.last_name,
          'website -> person.website,
          'email -> person.email,
          'is_active -> person.is_active,
          'date_joined -> person.date_joined,
          'project_id -> person.projectId
        ).executeUpdate()
    }
  }


}

object Project {

  val simple = {
      get[Option[Long]]("project.id") ~
      get[String]("project.name") map {
      case id ~ name => Project(id, name)
    }
  }

  def insertProject(project: Project) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into project values (
            (select next value for project_seq),
            {name}
          )
        """
      ).on(
          'name -> project.name
        ).executeUpdate()
    }
  }


  def options: Seq[(String,String)] = DB.withConnection { implicit connection =>
    SQL("select * from project order by name").as(Project.simple *).
      foldLeft[Seq[(String, String)]](Nil) { (cs, c) =>
      c.id.fold(cs) { id => cs :+ (id.toString -> c.name) }
    }
  }

}