package controllers

import models._
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import views._

object Application extends Controller {


  val Home = Redirect(routes.Application.list(0, 2, ""))

  val projectForm = Form(
    mapping(
      "id" -> ignored(None:Option[Long]),
      "name" -> nonEmptyText,
      "person_id" -> optional(longNumber)
    )(Project.apply)(Project.unapply)
  )

  def index = Action { Home }

  /**
   * @param page Current page number (starts from 0)
   * @param orderBy Column to be sorted
   * @param filter Filter applied on computer names
   */
  def list(page: Int, orderBy: Int, filter: String) = Action { implicit request =>
    Ok(html.list(
      Project.list(page = page, orderBy = orderBy, filter = ("%"+filter+"%")),
      orderBy, filter
    ))
  }

}
            
