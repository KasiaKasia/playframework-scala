package controllers
import models._
import models.Person._
import models.Project._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import views._


object Application extends Controller {

  val projectForm = Form(
    mapping(
      "id" -> ignored(None:Option[Long]),
      "name" -> nonEmptyText
    )(Project.apply)(Project.unapply)
  )

  val personForm = Form(
    mapping(
      "id" -> ignored(None:Option[Long]),
      "first_name" -> nonEmptyText,
      "last_name" -> nonEmptyText,
      "website" -> text,
      "email" -> nonEmptyText,
      "is_active" -> boolean,
      "date_joined" -> date,
      "project_id" -> optional(longNumber)
    )(Person.apply)(Person.unapply)
  )

  val Home = Redirect(routes.Application.list(0, 2, ""))


  def index = Action { Home }

  /**
   *
   * @param page Aktualny numer strony (numerowany od 0)
   * @param orderBy Sortowanie kolumny
   * @param filter Filter na kolumnie
   *
   */
  def list(page: Int, orderBy: Int, filter: String) = Action { implicit request =>
    Ok(html.list(
      Person.list(page = page, orderBy = orderBy, filter = ("%" + filter + "%")),
       orderBy,  filter
    ))
  }

  def createPerson = Action {
     Ok(html.createPersonForm(personForm,Project.options))
  }

  def createProject = Action {
    Ok(html.createProjectForm(projectForm,Project.options))
  }



  def savePerson = Action { implicit request =>
    personForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.createPersonForm(formWithErrors, Project.options)),
      person => {
        insert(person)
        Home.flashing("success" -> "Person has been created" )
      }
    )
  }


  def saveProject = Action { implicit request =>
    projectForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.createProjectForm(formWithErrors, Project.options)),
      project => {
        insertProject(project)
        Home.flashing("success" -> "Project has been created" )
      }
    )
  }


  /**
   *
   * @param id Id of the computer to edit
   */
  def editPerson(id: Long) = Action {
    Person.findById(id).map { person =>
      Ok(html.editForm(id, personForm.fill(person), Project.options))
    }.getOrElse(NotFound)

  }

  /**
   *
   * @param id  Id Person edytowanej
   */
  def updatePerson(id: Long) = Action { implicit request =>
    personForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.editForm(id, formWithErrors, Project.options)),
      person => {
        Person.updatePerson(id, person)
        Home.flashing("success" -> "Person has been updated")
      }
    )
  }





}


