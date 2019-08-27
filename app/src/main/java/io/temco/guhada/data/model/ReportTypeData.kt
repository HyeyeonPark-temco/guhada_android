package io.temco.guhada.data.model

import java.util.*

enum class ReportTarget {PRODUCT,BOARD,COMMENT,REVIEW,USER,NONE}

class ReportTypeData {

    var targets : Array<String> = arrayOf()
    var description : String = ""
    var name : String = ""

    override fun toString(): String {
        return "ReportTypeData(targets=${Arrays.toString(targets)}, description='$description', name='$name')"
    }
}