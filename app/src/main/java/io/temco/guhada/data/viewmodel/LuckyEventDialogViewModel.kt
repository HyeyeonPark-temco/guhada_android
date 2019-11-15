package io.temco.guhada.data.viewmodel

import android.content.Context
import io.reactivex.Observable
import io.temco.guhada.R
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.Deal
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
class LuckyEventDialogViewModel(val context: Context) : BaseObservableViewModel() {


    fun getUserDataCheck(listener: OnCallBackListener) {

    }
}
