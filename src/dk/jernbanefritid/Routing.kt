package dk.jernbanefritid

import arrow.core.*
import arrow.core.extensions.either.monad.binding
import arrow.data.getOption
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing

fun badRequest(message: String): HttpStatusCode {
    return HttpStatusCode.BadRequest
}

@KtorExperimentalLocationsAPI
fun Routing.test() {
    post<Members.Member.NamePAth> {
        call.respondEither(it.editName(call.receive()))
    }
    post<Members.Revert> {
        call.respondEither(it.revertName(call.receive()))
    }
    post<Members> {
        call.respondEither(it.createMember(call.receive()))
    }
    get<Members> {
        call.respond(EventStore.state.members)
    }
    get<Members.Member> {
        call.respond(EventStore.state.members[it.id] as Any)
    }
    get<Members.Member.Events> {
        call.respond(EventStore.events.filter { g ->
            when {
                g is NameUpdated && g.id.value == it.member.id -> true
                g is MemberCreated && g.id.value == it.member.id -> true
                else -> false
            }
        })
    }
}

@KtorExperimentalLocationsAPI
@Location("/members")
class Members {
    fun createMember(t: CreateMember): HttpResult<List<IEvent>> {
        return binding {
            val (cprNumber) = CprNumber(t.id)
            val (safeName) = Name(t.name)
            if (EventStore.state.members.containsKey(cprNumber.value)) {
                bind { Left(badRequest("member already exists")) }
            }
            val r = MemberCreated(id = cprNumber, name = safeName, commitMsg = t.commitMsg)
            EventStore.addEvent(r)
            EventStore.events
        }
    }

    class CreateMember(
        val id: String,
        val name: String,
        val commitMsg: String?
    )

    @Location("/{id}")
    data class Member(val id: String) {
        @Location("/name")
        data class NamePAth(val member: Member) {
            fun editName(t: EditName): HttpResult<List<IEvent>> {
                return binding {
                    if (!EventStore.state.members.containsKey(member.id)) {
                        badRequest("member does not exists").left().bind()
                    }
                    val (cprNumber) = CprNumber(member.id)
                    val (safeName) = Name(t.name)
                    val (safeOldName) = EventStore.state.members.getEither(member.id){
                        badRequest(
                            "member does not exists"
                        )
                    }.map { it.name }
                    val r = NameUpdated(id = cprNumber, name = safeName, oldName = safeOldName, commitMsg = t.commitMsg)
                    EventStore.addEvent(r)
                    EventStore.events
                }
            }

            class EditName(val name: String, val commitMsg: String?)

        }

        @Location("/events")
        data class Events(val member: Member)
    }

    @Location("/revert/name")
    class Revert {
        fun revertName(t: NameUpdated): HttpResult<List<IEvent>> {
            return binding {
                if (!EventStore.state.members.containsKey(t.id.value)) {
                    badRequest("member does not exists").left().bind()
                }
                val cprNumber = t.id
                val safeOldName = t.name
                val safeName = t.oldName
                val r = NameUpdated(id = cprNumber, name = safeName, oldName = safeOldName, commitMsg = t.commitMsg)
                EventStore.addEvent(r)
                EventStore.events
            }
        }
    }
}
