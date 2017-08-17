/*
 * Copyright 2017 HM Revenue & Customs
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

package services

import connectors.{EmailConnector, EmailNotSent, EmailStatus}
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.JsValue
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

trait NotificationService {

  def emailConnector: EmailConnector

  def sendMail(subscriptionId: String, emailAddress: String)(implicit hc: HeaderCarrier): Future[EmailStatus] = {

    val template = "lisa_application_submit"

    val params = Map("subscriptionId" -> subscriptionId,
          "email" -> emailAddress)
        emailConnector.sendTemplatedEmail(emailAddress, template, params = params)
    }
}
