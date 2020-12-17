package sttp.tapir.json.json4s

import com.cmartin.learn.configuration.ComponentLogging
import org.json4s.native.{JsonMethods, Serialization}
import org.json4s.{DefaultFormats, JValue}
import sttp.tapir.Codec.JsonCodec
import sttp.tapir.DecodeResult.{Error, Value}
import sttp.tapir.SchemaType.{SCoproduct, SObjectInfo}
import sttp.tapir.{EndpointIO, _}

import scala.util.{Failure, Success, Try}

trait TapirJsonJson4s extends ComponentLogging {

  def jsonBody[T: Schema](implicit m: Manifest[T]): EndpointIO.Body[String, T] =
    anyFromUtf8StringBody(json4sCodec[T])

  implicit val formats = DefaultFormats

  /*
    - decoder only for [JValue], todo extract to case class
    - encoder for [T]
   */
  implicit def json4sCodec[T: Schema](implicit m: Manifest[T]): JsonCodec[T] =
    Codec.json { s =>
      log.debug(s"json4s.codec.decode.string: $s")
      Try(JsonMethods.parse(s).extract[T]) match {
        case Success(value) => Value(value)
        case Failure(e) => Error("json4s decoder failed", e)
      }
    } { t =>
      log.debug(s"json4s.codec.encode.type: ${t.getClass.getName}")
      Serialization.write(t)
    }

  // Json is a coproduct with unknown implementations
  implicit val schemaForJson4s: Schema[JValue] =
    Schema(
      SCoproduct(
        SObjectInfo("org.json4s.JValue"),
        List.empty,
        None
      )
    )
}
