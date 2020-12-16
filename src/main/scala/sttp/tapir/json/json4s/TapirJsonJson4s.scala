package sttp.tapir.json.json4s

import scala.util.Failure
import scala.util.Success
import scala.util.Try

import com.cmartin.learn.configuration.ComponentLogging
import org.json4s.DefaultFormats
import org.json4s.Extraction
import org.json4s.JValue
import org.json4s.native.JsonMethods
import sttp.tapir.Codec.JsonCodec
import sttp.tapir.DecodeResult.Error
import sttp.tapir.DecodeResult.Value
import sttp.tapir.EndpointIO
import sttp.tapir.SchemaType.SCoproduct
import sttp.tapir.SchemaType.SObjectInfo
import sttp.tapir._

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
        case Failure(e)     => Error("json4s decoder failed", e)
      }
    } { t =>
      log.debug(s"json4s.codec.encode.type: ${t.getClass.getName}")
      JsonMethods.compact(
        JsonMethods.render(
          Extraction.decompose(t)
        )
      )
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
