package io.temco.guhada.data.viewmodel.mypage

import android.content.Context
import android.util.Log
import androidx.databinding.Bindable
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
    var UNIT_PER_PAGE = 6
    var mSellerList: MutableList<Seller> = mutableListOf()
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

                                for (item in list) {
                                    getSeller(item.targetId)
                                }
                            })
                }, accessToken = token, target = BookMarkTarget.SELLER.target, userId = userId)
        })
    }

    fun getSeller(sellerId: Long) {
        //  GlobalScope.launch {
        UserServer.getSellerById(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        Log.e("ㅇㅇㅇ4", "${(it.data as Seller).id} / ${(it.data as Seller).storeName}")
                        mSellerList.add(it.data as Seller)

                        if (mSellerList.size < UNIT_PER_PAGE || (mSellerList.size >= UNIT_PER_PAGE && mSellerList.size % UNIT_PER_PAGE == 0))
                            notifyPropertyChanged(BR.mSellerList)
                    })
        }, sellerId = sellerId)
        // }
    }
}