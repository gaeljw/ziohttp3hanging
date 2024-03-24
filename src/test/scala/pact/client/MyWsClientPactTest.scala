package pact.client

import au.com.dius.pact.consumer.ConsumerPactBuilder
import au.com.dius.pact.consumer.dsl.LambdaDsl._
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.core.model.RequestResponsePact
import com.mycompany.Config.SomeConfig
import com.mycompany.{MyModel, MyWsClient}
import org.scalatest.matchers.must.Matchers.{contain, convertToAnyMustWrapper}
import org.scalatest.wordspec.AnyWordSpec
import pact4s.scalatest.RequestResponsePactForger
import zio.Unsafe.unsafe
import zio.http.Client
import zio.{ZIO, ZLayer}

class MyWsClientPactTest extends AnyWordSpec with RequestResponsePactForger {

  private val CONSUMER = "consumer"
  private val PROVIDER = "provider"

  private val pactBase: PactDslWithProvider = ConsumerPactBuilder
    .consumer(CONSUMER)
    .hasPactWith(PROVIDER)

  private val myModel = newJsonArray { array =>
    array.`object` { profile =>
      profile.id("id", 11)
      profile.stringType("country", "fr")
      ()
    }
    ()
  }

  override val pact: RequestResponsePact = pactBase
    .`given`("A model")
    .uponReceiving("a request to retrieve a model")
    .path("/myModel")
    .method("GET")
    .willRespondWith()
    .status(200)
    .body(myModel.build())
    .toPact

  "MyWsClient" should {
    "retrieve a model" in {
      val fakeConfig: ZLayer[Any, Nothing, SomeConfig] =
        ZLayer.fromZIO(ZIO.succeed(SomeConfig(url = mockServer.getUrl)))

      val services = ZLayer.make[MyWsClient](Client.default, MyWsClient.live, fakeConfig)

      val program = for {
        myWsClient <- ZIO.service[MyWsClient]
        models <- myWsClient.getModels()
      } yield models

      val programWithDependencies = program.provide(services)

      val actualModels = unsafe { implicit u =>
        zio.Runtime.default.unsafe.run(programWithDependencies).getOrThrow()
      }

      val expectedModels = Seq(MyModel(id = 11, country = "fr"))

      actualModels must contain theSameElementsAs expectedModels
    }
  }
}
