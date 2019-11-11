package io.temco.guhada.data.model

/**
 * 무이자 할부 카드 정보
 * @author Hyeyeon Park
 * @since 2019..11.06
 */
class CardInterest {
    var id = ""
    var cardCompany = ""
    var freeApply = ""
    var startEventDate = ""
    var endEventDate = ""
    var note = ""
    var etc = ""
        get() {
            return if (field.isEmpty()) field
            else "※ $field"
        }
    var createDate = ""
    var eventMonth = ""
    var imgUrl = ""

    fun getDate(): String = "$startEventDate ~ $endEventDate\n$note"
}