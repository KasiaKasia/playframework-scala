import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {
  
  import models._

  // -- Date helpers
  
  def dateIs(date: java.util.Date, str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").format(date) == str
  
  // --
  
  "Application" should {
    
    "redirect to the computer list on /" in {
      
      val result = controllers.Application.index(FakeRequest())
      
      status(result) must equalTo(SEE_OTHER)
      redirectLocation(result) must beSome.which(_ == "/computers")
      
    }
    
    "list computers on the the first page" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        
        val result = controllers.Application.list(0, 2, "")(FakeRequest())

        status(result) must equalTo(OK)
        contentAsString(result) must contain("574 computers found")
        
      }      
    }
    
    "filter computer by name" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        
        val result = controllers.Application.list(0, 2, "Apple")(FakeRequest())

        status(result) must equalTo(OK)
        contentAsString(result) must contain("13 computers found")
        
      }      
    }
    

    
  }
  
}