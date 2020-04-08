/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import models.DataModel
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.ControllerComponents
import play.api.test.FakeRequest
import reactivemongo.api.commands.{LastError, WriteResult}
import reactivemongo.core.errors.{DatabaseException, GenericDriverException}
import repositories.DataRepository
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.{ExecutionContext, Future}

class ApplicationControllerSpec extends UnitSpec with GuiceOneAppPerSuite with MockitoSugar {

  implicit val system: ActorSystem = ActorSystem("Sys")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val controllerComponents: ControllerComponents = app.injector.instanceOf[ControllerComponents]
  implicit val executionContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  val mockDataRepository: DataRepository = mock[DataRepository]

  object TestApplicationController extends ApplicationController(
    controllerComponents,
    mockDataRepository,
    executionContext
  )

  val dataModel: DataModel = DataModel(
    "abcd",
    "test name",
    "test description",
    100
  )

  val writeResult = LastError(ok = true, None, None, None, 0, None, updatedExisting = false, None, None, wtimeout = false, None, None)

  "ApplicationController .index" when {

    when(mockDataRepository.find(any())(any()))
      .thenReturn(Future(List(dataModel)))

    val result = TestApplicationController.index()(FakeRequest())

    "return OK" in {
       status(result) shouldBe Status.OK
    }
  }

  "ApplicationController .create" when {

    "the json body is valid" should {

      val jsonBody: JsObject = Json.obj(
        "_id" -> "abcd",
        "name" -> "test name",
        "description" -> "test description",
        "numSales" -> 100
      )

      val writeResult: WriteResult = LastError(ok = true, None, None, None, 0, None, updatedExisting = false, None, None, wtimeout = false, None, None)

      when(mockDataRepository.create(any()))
        .thenReturn(Future(writeResult))

      val result = TestApplicationController.create()(FakeRequest().withBody(jsonBody))

      "return CREATED" in {
        status(result) shouldBe Status.CREATED
      }
    }

    "the json body is not valid" should {

      val jsonBody: JsObject = Json.obj(
        "_id" -> "abcd",
        "blah" -> "test name"
      )

      val result = TestApplicationController.create()(FakeRequest().withBody(jsonBody))

      "return BAD_REQUEST" in {
        status(result) shouldBe Status.BAD_REQUEST
      }
    }

    "the mongo data creation failed" should {

      val jsonBody: JsObject = Json.obj(
        "_id" -> "abcd",
        "name" -> "test name",
        "description" -> "test description",
        "numSales" -> 100
      )

      when(mockDataRepository.create(any()))
        .thenReturn(Future.failed(GenericDriverException("Error")))

      "return an error" in {

        intercept[GenericDriverException] {
          val result = TestApplicationController.create()(FakeRequest().withBody(jsonBody))

          status(result) shouldBe Status.INTERNAL_SERVER_ERROR

          await(bodyOf(result)) shouldBe Json.obj("message" -> "Error adding item to Mongo")
        }
      }
    }
  }

  "ApplicationController .update" should {

  }
}
