package io.temco.guhada.data.viewmodel.mypage

import android.content.Context
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.auth0.android.jwt.JWT
import io.temco.guhada.BR
import io.temco.guhada.common.enum.BookMarkTarget
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.BookMark
import io.temco.guhada.data.model.seller.Seller
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 19.07.22
 * @author park jungho
 *
 * 팔로우한 스토어
- 19.07.22 개발중인 화면
 *
 */
class MyPageFollowViewModel(val context: Context) : BaseObservableViewModel() {
    var mFollowList: MutableLiveData<MutableList<BookMark.Content>> = MutableLiveData(mutableListOf())
    var page = 1
    var mSellerList: MutableLiveData<MutableList<Seller>> = MutableLiveData(mutableListOf())
    var mSeller: MutableLiveData<Seller> = MutableLiveData()
    var mEmptyViewVisible = ObservableBoolean(false)
        @Bindable
        get() = field

    fun onClickMore() {

    }

    fun getFollowingSellerIds() {
        ServerCallbackUtil.callWithToken(task = { token ->
            val userId = JWT(token.split("Bearer ")[1]).getClaim("userId").asInt()
            if (userId != null)
                UserServer.getBookMarkWithoutTargetId(OnServerListener { success, o ->
                    ServerCallbackUtil.executeByResultCode(success, o,
                            successTask = {
                                val list = (it.data as BookMark).content
                                mFollowList.postValue(list)
                                mEmptyViewVisible = ObservableBoolean(list.isEmpty())
                                notifyPropertyChanged(BR.mEmptyViewVisible)

                                for (item in list) {
                                    getSeller(item.targetId)
                                }
                            })
                }, accessToken = token, target = BookMarkTarget.SELLER.target, userId = userId)
        })
    }

    fun getSeller(sellerId: Long) {
        GlobalScope.launch {
            UserServer.getSellerById(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            mSeller.postValue(it.data as Seller)
                        })
            }, sellerId = sellerId)
        }
    }
}