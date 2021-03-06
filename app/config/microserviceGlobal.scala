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

package config

import akka.stream.Materializer
import com.typesafe.config.{Config, ConfigFactory}
import net.ceedubs.ficus.Ficus._
import play.api.Play.current
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{EssentialFilter, RequestHeader, Result, Results}
import play.api.{Application, Configuration, Play}
import uk.gov.hmrc.auth.filter.FilterConfig
import uk.gov.hmrc.play.audit.filters.AuditFilter
import uk.gov.hmrc.play.config.{AppName, ControllerConfig, RunMode}
import uk.gov.hmrc.play.filters.MicroserviceFilterSupport
import uk.gov.hmrc.play.http.logging.filters.LoggingFilter
import uk.gov.hmrc.play.microservice.bootstrap.DefaultMicroserviceGlobal

import scala.concurrent.Future


object ControllerConfiguration extends ControllerConfig {
  lazy val controllerConfigs = Play.current.configuration.underlying.as[Config]("controllers")
}


object MicroserviceAuditFilter extends AuditFilter with AppName with MicroserviceFilterSupport {
  override val auditConnector = MicroserviceAuditConnector

  override def controllerNeedsAuditing(controllerName: String) = ControllerConfiguration.paramsForController(controllerName).needsAuditing
}

object MicroserviceLoggingFilter extends LoggingFilter with MicroserviceFilterSupport {
  override def controllerNeedsLogging(controllerName: String) = ControllerConfiguration.paramsForController(controllerName).needsLogging
}


object MicroserviceGlobal extends DefaultMicroserviceGlobal with MicroserviceFilterSupport with RunMode {

  override val auditConnector = MicroserviceAuditConnector

  override def microserviceMetricsConfig(implicit app: Application): Option[Configuration] = app.configuration.getConfig(s"microservice.metrics")

  override val loggingFilter = MicroserviceLoggingFilter

  override val microserviceAuditFilter = MicroserviceAuditFilter

  override def authFilter: Option[EssentialFilter] = Some(AuthorisationFilter())

  val errorJson: JsValue = Json.parse("{\"code\": \"INTERNAL_SERVER_ERROR\", \"reason\": \"Internal Server Error\"}")


  override def onError(request: RequestHeader, ex: Throwable): Future[Result] = {
    super.onError(request, ex)
    Future.successful(Results.InternalServerError(errorJson))
  }
}

object AuthorisationFilter {
  def apply() = new uk.gov.hmrc.auth.filter.AuthorisationFilter {
    override def config: FilterConfig = FilterConfig(Play.current.configuration.getConfig("controllers")
      .map(_.underlying)
      .getOrElse(ConfigFactory.empty()))

    override def connector: uk.gov.hmrc.auth.core.AuthConnector = LisaAuthConnector

    override implicit def mat: Materializer = Play.materializer
  }
}

