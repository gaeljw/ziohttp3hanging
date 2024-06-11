package com.mycompany

import Config.SomeConfig
import Defects.{ExternalServiceError, UrlError}
import org.slf4j.LoggerFactory
import zio.http.{Client, Header, Headers, Request, Response, Status, URL, MediaType}
import zio.json.{DecoderOps, JsonDecoder}
import zio.{UIO, IO, Task, ZIO, ZLayer}

object MyWsClient {

  val live: ZLayer[SomeConfig with Client, Nothing, MyWsClient] =
    ZLayer.fromFunction { (someConfig: SomeConfig, client: Client) =>
      new MyWsClient(someConfig, client)
    }

}

class MyWsClient(someConfig: SomeConfig, client: Client) {

  private val logger = LoggerFactory.getLogger(getClass)

  def getModels(): UIO[Seq[MyModel]] = {
    
    // These headers make the test hang forever
//    val headers: Headers = Headers(Header.Accept(MediaType.application.json), Header.Authorization.Unparsed("", "plop-key"))
    
    // These headers work fine
     val headers: Headers = Headers(Header.Accept(MediaType.application.json), Header.Custom("Authorization", "plop-key"))
    // val headers: Headers = Headers(Header.Accept(MediaType.application.json), Header.Authorization.Unparsed("something", "plop-key"))
    // val headers: Headers = Headers(Header.Accept(MediaType.application.json))
    
    val urlWithParams = URL.decode(s"${someConfig.url}/myModel")

    println(headers)

    callWithHeaders(urlWithParams, headers) { response =>
      response.status match {
        case Status.Ok =>
          parseAsJson[Seq[MyModel]](response).orDieWith(ex => ExternalServiceError(ex))
        case status =>
          ZIO.die(ExternalServiceError(new IllegalStateException(s"Unexpected status $status")))
      }
    }
  }

  private def callWithHeaders[E, A](urlEither: Either[Exception, URL], headers: Headers)(responseHandler: Response => IO[E, A]): IO[E, A] = {
    for {
      url <- ZIO.fromEither(urlEither).orDieWith(ex => UrlError(ex))
      urlString = url.toJavaURI.toString
      _ <- ZIO.succeed(logger.debug(s"Going to call url $urlString for service"))
      request = Request.get(url).addHeaders(headers)

      result <- callAndHandleResponse(request, responseHandler)
    } yield result
  }

  private def callAndHandleResponse[E, A](request: Request, handler: Response => IO[E, A]): IO[E, A] = {
    ZIO.scoped {
      for {
        response <- client.request(request).orDieWith(ex => ExternalServiceError(ex))
        result <- handler(response)
      } yield result
    }
  }

  private def parseAsJson[A: JsonDecoder](response: Response): Task[A] = {
    for {
      jsonString <- response.body.asString
      result <- ZIO.fromEither(jsonString.fromJson[A]).mapError(errorMessage => new RuntimeException(s"Unable to parse JSON: $errorMessage"))
    } yield result
  }
  
}
