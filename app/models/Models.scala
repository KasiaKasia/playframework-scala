package models

import java.util.Date
import anorm.SqlParser._
import anorm._
import play.api.Play.current
import play.api.db._
import scala.language.postfixOps


case class Project(id: Option[Long] = None, name: String, version: BigDecimal)
case class Person(id: Option[Long] = None, first_name: String, last_name: String, website: String, email: String, is_active: Boolean, date_joined: Date, projectId: Option[Long])
case class Task(id: Option[Long] = None, name: String, projectId: Option[Long], personId: Option[Long])

case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

object Task {

  val simple = {
      get[Option[Long]]("task.id") ~
      get[String]("task.name") ~
      get[Option[Long]]("task.project_id") ~
      get[Option[Long]]("task.person_id") map {
      case id ~ name ~ projectId ~ personId => Task(id, name, projectId, personId)
    }
  }

  val withTaskProjectPerson = Task.simple ~
    (Person.simple ?) ~
    (Project.simple ?) map {
    case task ~ person ~ project  => (task, person, project)
  }

  def listTask(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[(Task, Option[Person] , Option[Project])] = {

    val offest = pageSize * page

    DB.withConnection { implicit connection =>

      val tasks = SQL(
        """
          select * from task
          left join project on task.project_id = project.id
          left join person on task.person_id = person.id
          where task.name like {filter}
          order by {orderBy} nulls last
          limit {pageSize} offset {offset}
        """
      ).on(
          'pageSize -> pageSize,
          'offset -> offest,
          'filter -> filter,
          'orderBy -> orderBy
        ).as(Task.withTaskProjectPerson *)

      val totalRowsTasks = SQL(
        """
          select count(*) from task
          left join project on task.project_id = project.id
          left join person on task.person_id = person.id
          where task.name like {filter}
        """
      ).on(
          'filter -> filter
        ).as(scalar[Long].single)

      Page(tasks, page, offest, totalRowsTasks)

    }
  }



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
      SQL("select * from person where id = {id}").on(
        'id -> id
      ).as(Person.simple.singleOpt)
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

  def deletePerson(id: Long) = {
    DB.withConnection { implicit connection =>
      SQL("delete from person where id = {id}").on('id -> id).executeUpdate()
    }
  }

}

object Project {

  val simple = {
      get[Option[Long]]("project.id") ~
      get[String]("project.name") ~
      get[BigDecimal]("project.version")  map {
      case id ~ name ~ version => Project(id, name,version)
    }
  }

  def findByProject_id(id: Long): Option[Project] = {
    DB.withConnection { implicit connection =>
      SQL("select * from project where id = {id}").on(
        'id -> id
      ).as(Project.simple.singleOpt)
    }
  }

  def insertProject(project: Project) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into project values (
            (select next value for project_seq),
            {name}, {version}
          )
        """
      ).on(
          'name -> project.name,
          'version -> project.version
        ).executeUpdate()
    }
  }

  /**
   *
   * @param id ID Project
   * @param project rekordy Projects
   */
  def updateProject(id: Long, project: Project) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          update project
            set name = {name},
            version = {version}
            where id = {id}
        """
      ).on(
          'id -> id,
          'name -> project.name,
          'version -> project.version
        ).executeUpdate()
    }
  }

  def deleteProject(id: Long) = {
    DB.withConnection { implicit connection =>
      SQL("delete from project where id = {id}").on('id -> id).executeUpdate()
    }
  }

  def options: Seq[(String,String)] = DB.withConnection { implicit connection =>
    SQL("select * from project order by name").as(Project.simple *).
      foldLeft[Seq[(String, String)]](Nil) { (cs, c) =>
      c.id.fold(cs) { id => cs :+ (id.toString -> c.name) }
    }
  }

}