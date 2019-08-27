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
import io.temco.guhada.data.model.BookMarkResponse
import io.temco.guhada.data.model.seller.Seller
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

/**
 * 19.07.22
 * @author park jungho
 *
 * 팔로우한 스토어
 * @author Hyeyeon Park
 * @since 2019.08.26
 *
 */
class MyPageFollowViewModel(val context: Context) : BaseObservableViewModel() {
    var mFollowList: MutableLiveData<MutableList<BookMark.Content>> = MutableLiveData(mutableListOf())
    var page = 1
    var UNIT_PER_PAGE = 1
    var mSellerList: MutableList<Seller> = mutableListOf()
    var mTempSellerList: MutableList<Seller> = mutableListOf()
    var mSeller: MutableLiveData<Seller> = MutableLiveData()
    var mNotifyDataChangedTask: () -> Unit = {}
    var mNotifyItemInsertedTask: (startPos: Int, endPos: Int) -> Unit = { startPos, endPos -> }
    var mEmptyViewVisible = ObservableBoolean(false)
        @Bindable
        get() = field
    var mMoreButtonVisible = ObservableBoolean(false)
        @Bindable
        get() = field

    fun getFollowingSellerIds() {
        ServerCallbackUtil.callWithToken(task = { token ->
            val userId = JWT(token.split("Bearer ")[1]).getClaim("userId").asInt()
            if (userId != null)
                UserServer.getBookMarkWithoutTargetId(OnServerListener { success, o ->
                    ServerCallbackUtil.executeByResultCode(success, o,
                            successTask = {
                                val list = (it.data as BookMark).content
                                mFollowList.postValue(list)
                                setEmptyViewVisible()

                            })
                }, accessToken = token, target = BookMarkTarget.SELLER.target, userId = userId)
        })
    }

    private fun setEmptyViewVisible() {
        mEmptyViewVisible = ObservableBoolean(mFollowList.value?.isNotEmpty() ?: true)
        notifyPropertyChanged(BR.mEmptyViewVisible)
    }

}