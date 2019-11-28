package io.temco.guhada.data.viewmodel.mypage

import androidx.lifecycle.MutableLiveData
import io.temco.guhada.common.enum.ResultCode
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.blockchain.TokenHistory
import io.temco.guhada.data.model.blockchain.TokenList
import io.temco.guhada.data.server.BlockChainTokenServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

/**
 * 마이페이지 토큰 ViewModel
 * @author Hyeyeon Park
 * @since 2019.11.27
 */
class MyPageTokenViewModel : BaseObservableViewModel() {
    val mTokenList = MutableLiveData<List<TokenList>>()
    var mTokenHistory = MutableLiveData<TokenHistory>()
    var mHistoryPage = 1
    var mHistoryUnitPerPage = 5
    lateinit var mToken: TokenList

    fun getTokenList() {
        ServerCallbackUtil.callWithToken(task = {
            BlockChainTokenServer.getTokenList(OnServerListener { success, o ->
                if (success && o is BaseModel<*>) {
                    if (o.resultCode == ResultCode.SUCCESS.flag)
                        if (o.list is List<*>)
                            mTokenList.postValue(o.list as List<TokenList>)
                        else
                            ToastUtil.showMessage(o.message)
                }
            }, accessToken = it)
        })
    }

    fun getTokenHistoryList(invalidTokenTask: () -> Unit) {
        if (::mToken.isInitialized)
            ServerCallbackUtil.callWithToken(task = {
                BlockChainTokenServer.getTokenHistoryList(OnServerListener { success, o ->
                    if (success && o is BaseModel<*>) {
                        if (o.resultCode == ResultCode.SUCCESS.flag)
                            if (o.data is TokenHistory)
                                mTokenHistory.postValue(o.data as TokenHistory)
                            else
                                ToastUtil.showMessage(o.message)
                    }
                }, accessToken = it, tokenName = mToken.tokenName, page = mHistoryPage, unitPerPage = mHistoryUnitPerPage)
            }, invalidTokenTask = { invalidTokenTask() })
        else invalidTokenTask()
    }

}