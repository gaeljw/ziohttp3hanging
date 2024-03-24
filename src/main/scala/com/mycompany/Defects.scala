package com.mycompany

// Defects are errors we don't want to manage excepted at the top level, they are not part of the domain
object Defects {

  case class UrlError(cause: Throwable) extends RuntimeException(s"Error when creating an URL", cause)

  case class ExternalServiceError(cause: Throwable) extends RuntimeException(s"Error with an external service", cause)

}
