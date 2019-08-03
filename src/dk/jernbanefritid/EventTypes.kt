package dk.jernbanefritid

abstract class IEvent {
    abstract val commitMsg: String
    val type: String = this.javaClass.simpleName
    var eventId: String? = null
}

class MemberCreated(
    val id: CprNumber,
    val name: Name,
    commitMsg: String? = "Member created"
) : IEvent() {
    override val commitMsg = commitMsg ?: "Member created"
}

class NameUpdated(
    val id: CprNumber,
    val name: Name,
    val oldName: Name,
    commitMsg: String? = "Name updated"
) : IEvent() {
    override val commitMsg = commitMsg ?: "Member created"
}