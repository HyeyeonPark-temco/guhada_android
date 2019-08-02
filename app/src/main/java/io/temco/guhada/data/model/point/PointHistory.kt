package io.temco.guhada.data.model.point

import android.util.Log
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.enum.PointStatus
import io.temco.guhada.data.model.base.BasePageModel
import java.text.DecimalFormat

class PointHistory : BasePageModel() {
    var content = mutableListOf<PointHistoryContent>()

    /**
     * createdDate 현재 20190717로 내려옴
     * TimeStamp로 변경 예정
     * @author Hyeyeon Park
     * @since 2019.08.02
     */
    inner class PointHistoryContent {
        var id: Long = 0
        var title = ""
        var desc = ""
        var imgUrl = ""
        var status = ""
        var createdDate = ""
        var point: Int = 0

        fun getStatusText(): String {
            return when (status) {
                PointStatus.SAVED.status,
                PointStatus.DUE_SAVE.status -> BaseApplication.getInstance().getString(R.string.mypagepoint_status_accumulated)

                PointStatus.CONSUMPTION.status,
                PointStatus.DUE_CONSUMPTION.status -> BaseApplication.getInstance().getString(R.string.mypagepoint_status_used)

                PointStatus.DUE_SAVE_CANCEL.status,
                PointStatus.SAVED_CANCEL.status,
                PointStatus.DUE_CONSUMPTION_CANCEL.status,
                PointStatus.CONSUMPTION_CANCEL.status,
                PointStatus.RESTORE_CANCEL.status,
                PointStatus.EXPIRED_CANCEL.status -> BaseApplication.getInstance().getString(R.string.mypagepoint_status_canceled)

                PointStatus.RESTORE.status -> BaseApplication.getInstance().getString(R.string.mypagepoint_status_recovered)
                PointStatus.EXPIRED.status -> BaseApplication.getInstance().getString(R.string.mypagepoint_status_expired)
                else -> ""
            }
        }

        // 수정 예정
        fun getPointStr(): String {
            return point.toString()
//            val point = DecimalFormat("#,###,###").format(point)
//
//            Log.e("ㅇㅇㅇ" , point)
//            return if (status == PointStatus.SAVED.status || status == PointStatus.DUE_SAVE.status || status == PointStatus.RESTORE.status) {
//                "+$point"
//            } else {
//                "-$point"
//            }
        }
    }
}
