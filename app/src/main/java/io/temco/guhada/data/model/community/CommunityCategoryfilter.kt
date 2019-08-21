package io.temco.guhada.data.model.community

import java.io.Serializable

class CommunityCategoryfilter : Serializable {
    var id = 0
    var communityCategoryId = 0
    var name = ""

    override fun toString(): String {
        return "CommunityCategoryfilter(id=$id, communityCategoryId=$communityCategoryId, name='$name')"
    }

}