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

import play.api.Play._
import uk.gov.hmrc.play.config.ServicesConfig

object AppContext extends ServicesConfig {
  lazy val appName = current.configuration.getString("appName").getOrElse(throw new RuntimeException("appName is not configured"))
  lazy val appUrl = current.configuration.getString("appUrl").getOrElse(throw new RuntimeException("appUrl is not configured"))
  lazy val baseUrl = current.configuration.getString(s"$env.baseUrl").getOrElse(throw new RuntimeException(s"Missing Key $env.baseUrl"))
  lazy val desAuthToken = current.configuration.getString("desauthtoken").getOrElse(throw new RuntimeException(s"Missing Key $env.desauthtoken"))
  lazy val desUrlHeaderEnv: String =  current.configuration.getString("environment").getOrElse(throw new RuntimeException(s"Missing Key $env.environment"))
}
