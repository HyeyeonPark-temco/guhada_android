package io.temco.guhada.data.viewmodel.main

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.temco.guhada.R
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.Deal
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.event.LuckyEvent
import io.temco.guhada.data.model.main.*
import io.temco.guhada.data.server.ProductServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.adapter.main.LuckyDrawAdapter
import io.temco.guhada.view.adapter.main.TimeDealListAdapter
import java.util.*

/**
 * @author park jungho
 * 19.07.18
 *
 * @author Hyeyeon Park
 * @since 2019.10.23
 *
 * 메인 홈 리스트 CustomView ViewModel
 */
class LuckyDrawViewModel(val context: Context) : BaseObservableViewModel() {

    val listData: ArrayList<MainBaseModel> = arrayListOf()

    lateinit var adapter: LuckyDrawAdapter
    lateinit var recyclerView: RecyclerView
    fun getListAdapter() = adapter

    fun getLuckyDraws(listener: OnCallBackListener) {
        if (listData.isNotEmpty()) listData.clear()
        ServerCallbackUtil.callWithToken(task = { accessToken ->
            getLuckyDrawList(accessToken, listener)
        },invalidTokenTask = {
            getLuckyDrawList(listener)
        })
    }


    private fun getLuckyDrawList(accessToken: String,listener: OnCallBackListener){
        ProductServer.getLuckyDraws(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        val data = it.data as LuckyEvent
                        setLuckyEventData(data, listener)
                    },
                    dataNotFoundTask = {
                        listener.callBackListener(false, "")
                    },
                    failedTask = {
                        listener.callBackListener(false, "")
                    }
            )
        },accessToken)
    }

    private fun getLuckyDrawList(listener: OnCallBackListener){
        ProductServer.getLuckyDraws(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        val data = it.data as LuckyEvent
                        setLuckyEventData(data, listener)
                    },
                    dataNotFoundTask = {
                        listener.callBackListener(false, "")
                    },
                    failedTask = {
                        listener.callBackListener(false, "")
                    }
            )
        })
    }


    private fun setLuckyEventData(data : LuckyEvent, listener: OnCallBackListener){
        if(data.luckyDrawList.size > 0){
            if(CustomLog.flag)CustomLog.L("LuckyDrawViewModel","data",data)
            val current = Calendar.getInstance().timeInMillis/* - (data.luckyDrawList[0].now*1000 - Calendar.getInstance().timeInMillis)*/
            val time = (data.luckyDrawList[0].remainedTimeForEnd) * 1000L
            listData.add(LuckyDrawTitle(0,HomeType.LuckyDrawTitle,data.titleList,
                    LuckyDrawTimeData(data.luckyDrawList[0].now,data.luckyDrawList[0].remainedTimeForStart,
                            data.luckyDrawList[0].remainedTimeForEnd,data.luckyDrawList[0].dealId,0),
                    current + time,0L))
            var index = 1
            Observable.fromIterable(data.luckyDrawList).map { event ->
                val current = Calendar.getInstance().timeInMillis
                val time = (event.remainedTimeForEnd) * 1000L
                LuckyDrawEvent(index = index++, type = HomeType.LuckyDrawEvent, eventData = event, endTime = event.remainedTimeForEnd * 1000L, expiredTimeLong = current + time)
            }.subscribe { event ->
                listData.add(event)
            }

            listData.add(LuckyDrawEventFooter(index, HomeType.LuckyDrawFooter))
            listener.callBackListener(true, "")
        }else{
            listener.callBackListener(false, "")
        }
    }
}
