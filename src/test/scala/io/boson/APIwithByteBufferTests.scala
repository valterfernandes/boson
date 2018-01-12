package io.boson

import java.nio.ByteBuffer
import java.util.concurrent.CompletableFuture
import bsonLib.{BsonArray, BsonObject}
import io.boson.bson.Boson
import io.boson.bson.bsonValue.{BsSeq, BsValue}
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class APIwithByteBufferTests extends FunSuite{

  val br4: BsonArray = new BsonArray().add("Insecticida")
  val br1: BsonArray = new BsonArray().add("Tarantula").add("Aracnídius").add(br4)
  val obj1: BsonObject = new BsonObject().put("José", br1)
  val br2: BsonArray = new BsonArray().add("Spider")
  val obj2: BsonObject = new BsonObject().put("José", br2)
  val br3: BsonArray = new BsonArray().add("Fly")
  val obj3: BsonObject = new BsonObject().put("José", br3)
  val arr: BsonArray = new BsonArray().add(2.2f).add(obj1).add(obj2).add(obj3).add(br4)
  val bsonEvent: BsonObject = new BsonObject().put("StartUp", arr)

  val validatedByteArray: Array[Byte] = arr.encodeToBarray()
  val validatedByteArrayObj: Array[Byte] = bsonEvent.encodeToBarray()

  val validatedByteBuffer: ByteBuffer = ByteBuffer.allocate(validatedByteArray.length)
  validatedByteBuffer.put(validatedByteArray)
  validatedByteBuffer.flip()

  val validatedByteBufferObj: ByteBuffer = ByteBuffer.allocate(validatedByteArrayObj.length)
  validatedByteBufferObj.put(validatedByteArrayObj)
  validatedByteBufferObj.flip()

  test("extract PosV1 w/ key") {
    val expression: String = "[2 to 3]"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteBuffer)

    assertEquals(BsSeq(Seq(Seq(
      Map("José" -> Seq("Spider")),
      Map("José" -> Seq("Fly"))
    ))), future.join())
  }

  test("extract PosV2 w/ key") {
    val expression: String = "[2 until 3]"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteBuffer)

    assertEquals(BsSeq(Seq(Seq(
      Map("José" -> Seq("Spider"))
    ))), future.join())
  }

  test("extract PosV3 w/ key") {
    val expression: String = "[2 until end]"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteBuffer)

    assertEquals(BsSeq(Seq(Seq(
      Map("José" -> Seq("Spider")),
      Map("José" -> Seq("Fly"))
    ))), future.join())
  }

  test("extract PosV4 w/ key") {
    val expression: String = "[2 to end]"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteBuffer)

    assertEquals(BsSeq(Seq(Seq(
      Map("José" -> Seq("Spider")),
      Map("José" -> Seq("Fly")),
      Seq("Insecticida")
    ))), future.join())
  }

  test("extract PosV5 w/ key") {
    val expression: String = "[3]"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteBuffer)

    assertEquals(BsSeq(Seq(Seq(
      Map("José" -> Seq("Fly"))
    ))), future.join())
  }

  test("extract with 2nd Key PosV1 w/ key") {
    val expression: String = "[2 to 3].José"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteBuffer)

    assertEquals(BsSeq(Seq(Seq(
      Seq("Spider"),
      Seq("Fly")
    ))), future.join())
  }

  test("extract with 2nd Key PosV2 w/ key") {
    val expression: String = "[2 until 3].José"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteBuffer)

    assertEquals(BsSeq(Seq(Seq(
      Seq("Spider")
    ))), future.join())
  }

  test("extract with 2nd Key PosV3 w/ key") {
    val expression: String = "[2 until end].José"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteBuffer)

    assertEquals(BsSeq(Seq(Seq(
      Seq("Spider"),
      Seq("Fly")
    ))), future.join())
  }

  test("extract with 2nd Key PosV4 w/ key") {
    val expression: String = "[2 to end].José"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteBuffer)

    assertEquals(BsSeq(Seq(Seq(
      Seq("Spider"),
      Seq("Fly")
    ))), future.join())
  }

  test("extract with 2nd Key PosV5 w/ key") {
    val expression: String = "[3].José"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteBuffer)

    assertEquals(BsSeq(Seq(Seq(
      Seq("Fly")
    ))), future.join())
  }

  test("extract first w/ key") {
    val expression: String = ".first"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteBuffer)

    assertEquals(BsSeq(Seq(
      2.2f
    )), future.join())
  }

  test("extract last w/ key") {
    val expression: String = ".last"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteBuffer)

    assertEquals(BsSeq(Seq(
      Seq("Insecticida")
    )), future.join())
  }

  test("extract all w/ key") {
    val expression: String = ".all"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteBuffer)

    assertEquals(BsSeq(Seq(
      2.2f,
      Map("José" -> Seq("Tarantula", "Aracnídius", Seq("Insecticida"))),
      Map("José" -> Seq("Spider")),
      Map("José" -> Seq("Fly")),
      Seq("Insecticida")
    )), future.join())
  }

  test("extract PosV1") {
    val expression: String = "José.[0 until end]"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteArrayObj)
    assertEquals(BsSeq(Seq(Seq(
      "Tarantula", "Aracnídius"
    ))), future.join())
  }

  test("extract PosV2") {
    val expression: String = "José.[0 to end]"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteBuffer)
    assertEquals(BsSeq(Seq(
      Seq("Tarantula", "Aracnídius", Seq("Insecticida")),
      Seq("Spider"),
      Seq("Fly")
    )), future.join())
  }

  test("extract PosV3") {
    val expression: String = "José.[1 to 2]"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteBufferObj)
    assertEquals(BsSeq(Seq(Seq(
      "Aracnídius",
      Seq("Insecticida")
    ))), future.join())
  }

  test("extract PosV4") {
    val expression: String = "StartUp.[1 to 2]"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteBufferObj)
    assertEquals(BsSeq(Seq(Seq(
      Map("José" -> Seq("Tarantula", "Aracnídius", Seq("Insecticida"))),
      Map("José" -> Seq("Spider"))
    ))), future.join())
  }

  test("extract PosV5") {
    val expression: String = "StartUp.[3]"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteBufferObj)

    assertEquals(BsSeq(Seq(Seq(
      Map("José" -> Seq("Fly"))
    ))), future.join())
  }

  test("extract with 2nd Key PosV1") {
    val expression: String = "StartUp.[0 until end].José"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteArrayObj)
    assertEquals(BsSeq(Seq(Seq(
      Seq("Tarantula", "Aracnídius", Seq("Insecticida")),
      Seq("Spider"),
      Seq("Fly")
    ))), future.join())
  }

  test("extract with 2nd Key PosV2") {
    val expression: String = "StartUp.[2 to end].José"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteArrayObj)
    assertEquals(BsSeq(Seq(Seq(
      Seq("Spider"),
      Seq("Fly")
    ))), future.join())
  }

  test("extract with 2nd Key PosV3") {
    val expression: String = "StartUp.[2 to 3].José"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteArrayObj)
    assertEquals(BsSeq(Seq(Seq(
      Seq("Spider"),
      Seq("Fly")
    ))), future.join())
  }

  test("extract with 2nd Key PosV4") {
    val expression: String = "StartUp.[2 until 3].José"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteArrayObj)
    assertEquals(BsSeq(Seq(Seq(
      Seq("Spider")
    ))), future.join())
  }

  test("extract with 2nd Key PosV5") {
    val expression: String = "StartUp.[4].José"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteArrayObj)
    assertEquals(BsSeq(Seq(Seq())), future.join())
  }

  test("extract first") {
    val expression: String = "StartUp..first"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteBufferObj)

    assertEquals(BsSeq(Seq(
      2.200000047683716,
      Map("José" -> List("Tarantula", "Aracnídius", List("Insecticida"))),
      Map("José" -> List("Spider")),
      Map("José" -> List("Fly")),
      List("Insecticida")
    )), future.join())
  }

  test("extract last") {
    val expression: String = "José..last"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteBufferObj)

    assertEquals(BsSeq(Seq(
      "Fly"
    )), future.join())
  }

  test("extract all") {
    val expression: String = "José..all"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteBufferObj)

    assertEquals(BsSeq(Seq(
      Seq("Tarantula", "Aracnídius", Seq("Insecticida")),
      Seq("Spider"),
      Seq("Fly")
    )), future.join())
  }

  test("extract all elements containing partial key") {
    val expression: String = "*os"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteBufferObj)

    assertEquals(BsSeq(Seq(
      Seq("Tarantula", "Aracnídius", Seq("Insecticida")),
      Seq("Spider"),
      Seq("Fly")
    )), future.join())
  }

  test("extract everything") {
    val expression: String = "*"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteBuffer)

    assertEquals(BsSeq(Seq(
      2.2f,
      Map("José" -> Seq("Tarantula", "Aracnídius", Seq("Insecticida"))),
      Map("José" ->Seq("Spider")),
      Map("José" ->Seq("Fly")),
      Seq("Insecticida")
    )), future.join())
  }

  test("extract all elements of a key") {
    val expression: String = "José"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteArray)
    assertEquals(
      BsSeq(Seq(
        Seq("Tarantula", "Aracnídius", Seq("Insecticida")),
        Seq("Spider"),
        Seq("Fly")
      )),
      future.join())
  }

  test("extract objects with a certain element") {
    val br4: BsonArray = new BsonArray().add("Tarantula").add("Aracnídius")
    val obj4: BsonObject = new BsonObject().put("Joséééé", br4)
    val br5: BsonArray = new BsonArray().add("Spider")
    val obj5: BsonObject = new BsonObject().put("José", br5)
    val arr1: BsonArray = new BsonArray().add(2.2f).add(obj4).add(true).add(obj5)
    val bsonEvent1: BsonObject = new BsonObject().put("StartUp", arr1)
    val validatedByteArrayObj1: Array[Byte] = bsonEvent1.encodeToBarray()
    val validatedByteBufferObj1: ByteBuffer = ByteBuffer.allocate(validatedByteArrayObj1.length)
    validatedByteBufferObj1.put(validatedByteArrayObj1)
    validatedByteBufferObj1.flip()

    val expression: String = "StartUp.[@José]"
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson.go(validatedByteBufferObj1)
    assertEquals(
      BsSeq(Seq(Seq(
        Map("José" -> Seq("Spider"))
      ))),
      future.join())
  }

  test("Inject API Double => Double") {
    val bsonEvent: BsonObject = new BsonObject().put("fridgeTemp", 5.2f).put("fanVelocity", 20.5).put("doorOpen", false)
    val newFridgeSerialCode: Double = 1000.0
    val validBsonArray: ByteBuffer = bsonEvent.encode().getByteBuf.nioBuffer()
    val expression = "fanVelocity..first"
    val boson: Boson = Boson.injector(expression, (in: Double) => newFridgeSerialCode)
    val result: CompletableFuture[ByteBuffer] = boson.go(validBsonArray)

    // apply an extractor to get the new serial code as above.
    val resultValue: ByteBuffer = result.join()
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson1: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson1.go(resultValue)

    assertEquals(List(1000.0), future.join().getValue )
  }
  test("Inject API String => String") {
    val bsonEvent: BsonObject = new BsonObject().put("fridgeTemp", 5.2f).put("fanVelocity", 20.5).put("doorOpen", false).put("string", "the")
    val newFridgeSerialCode: String = " what?"
    val validBsonArray: ByteBuffer = bsonEvent.encode().getByteBuf.nioBuffer()
    val expression = "string..first"
    val boson: Boson = Boson.injector(expression, (in: String) => in.concat(newFridgeSerialCode))
    val result: CompletableFuture[ByteBuffer] = boson.go(validBsonArray)

    // apply an extractor to get the new serial code as above.
    val resultValue: ByteBuffer = result.join()
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson1: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson1.go(resultValue)

    assertEquals(List("the what?").head, new String(future.join().getValue.asInstanceOf[List[Array[Byte]]].head) )
  }
  test("Inject API Map => Map") {
    val bAux: BsonObject = new BsonObject().put("damnnn", "DAMMN")
    val bsonEvent: BsonObject = new BsonObject().put("fridgeTemp", 5.2f).put("fanVelocity", 20.5).put("doorOpen", false).put("string", "the").put("bson", bAux)

    val newFridgeSerialCode: String = " what?"
    val validBsonArray: ByteBuffer = bsonEvent.encode().getByteBuf.nioBuffer()
    val expression = "bson..first"
    val boson: Boson = Boson.injector(expression, (in: Map[String, Any]) => in.+(("WHAT!!!", 10)))
    val result: CompletableFuture[ByteBuffer] = boson.go(validBsonArray)

    // apply an extractor to get the new serial code as above.
    val resultValue: ByteBuffer = result.join()
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson1: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson1.go(resultValue)

    assertEquals(BsSeq(List(Map("damnnn" -> "DAMMN", "WHAT!!!" -> 10))),future.join() )
  }
  test("Inject API List => List") {
    val bAux: BsonArray = new BsonArray().add(12).add("sddd")
    val bsonEvent: BsonObject = new BsonObject().put("fridgeTemp", 5.2f).put("fanVelocity", 20.5).put("doorOpen", false).put("string", 1).put("bson", bAux)

    val newFridgeSerialCode: String = "MAIS EU"
    val validBsonArray: ByteBuffer = bsonEvent.encode().getByteBuf.nioBuffer()
    val expression = "bson..first"
    val boson: Boson = Boson.injector(expression, (in: List[Any]) => {
      val s: List[Any] = in.:+(newFridgeSerialCode)
      s})
    val result: CompletableFuture[ByteBuffer] = boson.go(validBsonArray)

    // apply an extractor to get the new serial code as above.
    val resultValue: ByteBuffer = result.join()
    val future: CompletableFuture[BsValue] = new CompletableFuture[BsValue]()
    val boson1: Boson = Boson.extractor(expression, (in: BsValue) => future.complete(in))
    boson1.go(resultValue)

    assertEquals(BsSeq(List(12, "sddd", "MAIS EU")),future.join() )
  }

}
