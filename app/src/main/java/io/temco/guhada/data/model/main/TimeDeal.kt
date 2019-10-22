package io.temco.guhada.data.model.main

import io.temco.guhada.data.model.Deal


class TimeDeal(index: Int,
               type: HomeType,
               var deal : Deal,  var endTime : Long ) : MainBaseModel(index, type,2) {


}