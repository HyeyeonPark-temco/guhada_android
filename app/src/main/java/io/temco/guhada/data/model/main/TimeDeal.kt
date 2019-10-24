package io.temco.guhada.data.model.main

import io.temco.guhada.data.model.Deal

class TimeDeal(index :Int,
               type: HomeType = HomeType.TimeDeal,
               var deal: Deal, // 상품정보
               var endTime: Long, // 서버에서 받아온 남은 종료 [SECOND]
               var expiredTimeLong: Long // 단말의 현재시간 + endTime*1000  [MILLISECOND]
) : MainBaseModel(0, type, 2) {

    enum class Status(val code :String, val label: String){
        OUT_OF_STOCK_IMMINENT("OUT_OF_STOCK_IMMINENT", "품절임박"),
        NORMAL("NORMAL", "정상"),
        OUT_OF_STOCK("OUT_OF_STOCK", "품절"),
        READY("READY","판매예정")
    }

    var displayTime: Long = 0L // 화면에 표시 될 시간 , expiredTimeLong - 현재시간   [MILLISECOND]
}