package dk.jernbanefritid
import arrow.core.Left
import arrow.core.Right
import io.ktor.http.HttpStatusCode

interface IStringValue {
    val value: String
}
class CprNumber private constructor(override val value: String) : IStringValue {
    companion object {
        operator fun invoke(t: String): HttpResult<CprNumber> = when {
            t.length != 10 -> Left(HttpStatusCode.BadRequest)
            t.matches("[0-9]+".toRegex()) -> Left(HttpStatusCode.BadRequest)
            else -> Right(CprNumber(t))
        }
    }
}




class Name private constructor(override val value: String) : IStringValue {
    companion object {
        operator fun invoke(t: String): HttpResult<Name> = when {
            t.length < 3 -> Left(HttpStatusCode.BadRequest)
            else -> Right(Name(t))
        }

    }
}