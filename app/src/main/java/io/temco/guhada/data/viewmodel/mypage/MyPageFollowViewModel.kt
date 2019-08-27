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
                                setEmptyViewVisible(list)
                            })
                }, accessToken = token, target = BookMarkTarget.SELLER.target, userId = userId)
        })
    }

    fun onClickDeleteAll() {
        ServerCallbackUtil.callWithToken(task = { accessToken ->
            for (item in mFollowList.value ?: mutableListOf()) {
                UserServer.deleteBookMark(OnServerListener { success, o ->
                    ServerCallbackUtil.executeByResultCode(success, o,
                            successTask = {
                                mFollowList.value?.remove(item)

                                if(mFollowList.value?.isEmpty() == true){
                                    mEmptyViewVisible = ObservableBoolean(true)
                                    mFollowList.postValue(mutableListOf())
                                    notifyPropertyChanged(BR.mEmptyViewVisible)
                                }
                            })
                }, accessToken = accessToken, target = BookMarkTarget.SELLER.target, targetId = item.targetId)
            }
        })
    }

    private fun setEmptyViewVisible(list: MutableList<BookMark.Content>) {
        mEmptyViewVisible = ObservableBoolean(list.isEmpty())
        notifyPropertyChanged(BR.mEmptyViewVisible)
    }

}