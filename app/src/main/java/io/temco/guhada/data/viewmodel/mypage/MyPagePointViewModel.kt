package io.temco.guhada.data.viewmodel.mypage

import android.content.Context
import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.auth0.android.jwt.JWT
import io.temco.guhada.BR
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.point.Point
import io.temco.guhada.data.model.point.PointHistory
import io.temco.guhada.data.model.point.PointSummary
import io.temco.guhada.data.server.BenefitServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import java.util.*

/**
 * 19.07.22
 * @author park jungho
 *
 * 포인트
- 포인트 충전은 일단 제거함
- 할 수 있으면 일단 레이아웃은 일단 만들고 숨김으로 나중에는 추가됨
- 전체 / 충전 버튼 제거
- 사용가능한포인트 레이아웃 제거
- 적립예정/적립포인트/충전포인트 -> 적립예정/적립포인트
- 나머지 레이아웃은 그대로
- 웹 프론트 작업 된 내용 공유
○ 포인트 // /point-summary-controller/getPointSummaryUsingGET
○ 충전 조회  // /histories
○ 사용가능, 적립 /summary
 *
 */
class MyPagePointViewModel(val context: Context) : BaseObservableViewModel() {
    var pointSummary: ObservableField<PointSummary> = ObservableField()
        @Bindable
        get() = field
    var pointList: MutableLiveData<MutableList<Point>> = MutableLiveData()
    var pointHistory: ObservableField<PointHistory> = ObservableField()
        @Bindable
        get() = field
    var fromDate = ""
    var toDate = ""
    var page = 1

    private val UNIT_PER_PAGE = 10
    private val ORDER_TYPE_DESC = "DESC"
    private val ORDER_TYPE_ASC = "ASC"
    private val SORT_TYPE = "CREATED"

    fun getPointSummary() {
        ServerCallbackUtil.callWithToken(task = { token ->
            BenefitServer.getPointSummary(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            this.pointSummary = ObservableField(it.data as PointSummary)
                            notifyPropertyChanged(BR.pointSummary)
                        })
            }, token, expireDays = 30)
        })
    }

    fun getPoints() {
        ServerCallbackUtil.callWithToken(task = { token ->
            BenefitServer.getPoints(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            this.pointList.apply { it.list as MutableList<Point> }
                        })
            }, token, orderType = ORDER_TYPE_DESC, sortType = SORT_TYPE, page = page++, unitPerPage = UNIT_PER_PAGE)
        })
    }

    fun getPointHistories() {
        ServerCallbackUtil.callWithToken(task = { token ->
            val userId = JWT(token.split("Bearer ")[1]).getClaim("userId").asInt() ?: 0
            BenefitServer.getPointHistories(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            if (page > 2 && pointHistory.get()?.content?.isNotEmpty() ?: false) {
                                val history = it.data as PointHistory
                                pointHistory.get()?.content?.addAll(history.content)
                                pointHistory.get()?.last = history.last
                            } else {
                                pointHistory = ObservableField(it.data as PointHistory)
                            }
                            notifyPropertyChanged(BR.pointHistory)
                        })
            }, accessToken = token, fromAt = fromDate, toAt = toDate, historyStatus = "", charge = false, sortType = SORT_TYPE, orderType = ORDER_TYPE_DESC, page = page++, unitPerPage = 3, userId = userId)
        })
    }

    fun onClickMore() {
        getPointHistories()
    }
}