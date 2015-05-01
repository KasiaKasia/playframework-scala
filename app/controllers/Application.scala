package controllers


import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import views._

object Application extends Controller {

  val projectForm = Form(
    mapping(
      "id" -> ignored(None:Option[Long]),
      "name" -> nonEmptyText,
      "person_id" -> optional(longNumber)
    )(Project.apply)(Project.unapply)
  )
// mapowanie klasy Person
  val personForm = Form(
    mapping(
      "id" -> ignored(None:Option[Long]),
      "first_name" -> nonEmptyText,
      "last_name" -> nonEmptyText,
      "website" -> text,
      "email" -> nonEmptyText,
      "is_active" -> boolean,
      "date_joined" -> date
    )(Person.apply)(Person.unapply)
  )

  val Home = Redirect(routes.Application.list(0, 2, ""))


  def index = Action { Home }

  /**
   *
   * @param page Aktualny numer strony (numerowany od 0)
   * @param orderBy Sortowanie kolumny
   * @param filter Filter na kolumnie
   *   implicit - z ang. domniemany, niejawny
   */
  def list(page: Int, orderBy: Int, filter: String) = Action { implicit request =>
    Ok(html.list(
      Project.list(page = page, orderBy = orderBy, filter = ("%"+filter+"%")),
      orderBy, filter
    ))
  }
  // metoda do dodania Person
  def create = Action {
    // pobranie strony HTML
     Ok(html.createForm(personForm,Person.options))
  }
 // metoda zapisu Person pobranych danych z personForm
  def save = Action { implicit request =>
    personForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.createForm(formWithErrors, Person.options)),
      person => {
        // Pobranie metody insert
        Person.insert(person)
        Home.flashing("success" -> "Person %s has been created" )
      }
    )
  }

}
            
