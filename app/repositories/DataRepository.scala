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

package repositories

import javax.inject.{Inject, Singleton}
import models.DataModel
import play.api.libs.json.{JsObject, Json}
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.mongo.ReactiveRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DataRepository @Inject()(mongo: ReactiveMongoComponent,
                               implicit val ec: ExecutionContext) extends ReactiveRepository[DataModel, BSONObjectID](
    "data",
    mongo.mongoConnector.db,
    DataModel.formats
) {

  def create(data: DataModel): Future[WriteResult] = insert(data)

  def read(id: String): Future[DataModel] = find("_id" -> id) map (_.head)

  def update(data: DataModel): Future[DataModel] = findAndUpdate(
    query = Json.obj("_id" -> data._id),
    update = Json.toJson(data).as[JsObject]
  ).map(_ => data)

  def delete(id: String): Future[WriteResult] = remove("_id" -> id)

}

