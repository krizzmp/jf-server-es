package dk.jernbanefritid

import arrow.core.Either
import arrow.data.getOption
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond

typealias HttpResult<T> = Either<HttpStatusCode, T>

fun <T, R> Map<T, R>.update(key: T, transform: (R) -> R) = this.mapValues {
    when (key) {
        it.key -> transform(it.value)
        else -> it.value
    }
}

suspend fun <T : Any> ApplicationCall.respondEither(a: HttpResult<T>) {
    a.bimap({ this.respond(it, "") }, { this.respond(it) })
}

fun <K, V, L> Map<K, V>.getEither(key: K, ifLeft: () -> L): Either<L, V> {
    return this.getOption(key).toEither(ifLeft)
}