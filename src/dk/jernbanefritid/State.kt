package dk.jernbanefritid

import arrow.core.*

data class Store(
    val members: Map<String, Member> = mapOf()
)

object EventStore {
    fun addEvent(r: IEvent) {
        r.eventId = events.count().toString()
        events.add(r)
    }

    val events = mutableListOf<IEvent>()

    init {
        addEvent(
            MemberCreated(CprNumber("2805922037").orNull()!!, Name("krizzmp").orNull()!!)
        )
        addEvent(
            MemberCreated(CprNumber("2805922038").orNull()!!, Name("keyser").orNull()!!)
        )
        addEvent(
            NameUpdated(
                CprNumber("2805922037").orNull()!!,
                Name("kristoffer").orNull()!!,
                oldName = Name("krizzmp").orNull()!!
            )
        )
    }

    val state: Store get() = events.fold(Store(), ::rootReducer)
}


data class Member(val id: CprNumber, val name: Name)

fun rootReducer(state: Store, action: IEvent): Store = Store(members = membersReducer(state.members, action))

private fun membersReducer(members: Map<String, Member>, action: IEvent): Map<String, Member> = when (action) {
    is MemberCreated -> members + (action.id.value to Member(action.id, action.name))
    is NameUpdated -> members.update(action.id.value) { it.copy(name = action.name) }
    else -> members
}


