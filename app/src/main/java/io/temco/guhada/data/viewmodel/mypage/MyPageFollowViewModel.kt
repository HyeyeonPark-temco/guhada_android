package io.temco.guhada.data.viewmodel.mypage

import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.auth0.android.jwt.JWT
import io.temco.guhada.BR
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.enum.BookMarkTarget
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.BookMark
import io.temco.guhada.data.model.seller.Seller
import io.temco.guhada.data.model.seller.SellerStore
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
class MyPageFollowViewModel : BaseObservableViewModel() {
    private var mFollowList: MutableLiveData<MutableList<BookMark.Content>> = MutableLiveData(mutableListOf())
    var mFollowStore: MutableLiveData<MutableList<SellerStore>> = MutableLiveData()
    var mTempSellerList: MutableList<Seller> = mutableListOf()

    var mNotifyDataChangedTask: () -> Unit = {}
    var mNotifyItemInsertedTask: (startPos: Int, endPos: Int) -> Unit = { startPos, endPos -> }

    var mEmptyViewVisible = ObservableBoolean(false)
        @Bindable
        get() = field
    var mMoreButtonVisible = ObservableBoolean(false)
        @Bindable
        get() = field

    fun getFollowingStores() {
        val token = Preferences.getToken().accessToken
        if (!token.isNullOrEmpty()) {
            val userId = JWT(token).getClaim("userId").asInt()
            if (userId != null)
                UserServer.getFollowingStores(OnServerListener { success, o ->
                    ServerCallbackUtil.executeByResultCode(success, o,
                            successTask = {
                                val list = it.data as MutableList<SellerStore>
                                mFollowStore.postValue(list)
                                mEmptyViewVisible = ObservableBoolean(list.isEmpty())
                                notifyPropertyChanged(BR.mEmptyViewVisible)
                            },
                            dataNotFoundTask = {
                                val list = mutableListOf<SellerStore>()
                                mFollowStore.postValue(list)
                                mEmptyViewVisible = ObservableBoolean(list.isEmpty())
                                notifyPropertyChanged(BR.mEmptyViewVisible)
                            })
                }, userId = userId.toLong())
        }
    }

    /**
     * [미사용]
     * 팔로우한 스토어 전체 삭제
     * 사용 시 수정 필요
     * @author Hyeyeon Park
     * @since 2019.10.14
     */
    fun onClickDeleteAll() {
        ServerCallbackUtil.callWithToken(task = { accessToken ->
            for (item in mFollowList.value ?: mutableListOf()) {
                UserServer.deleteBookMark(OnServerListener { success, o ->
                    ServerCallbackUtil.executeByResultCode(success, o,
                            successTask = {
                                mFollowList.value?.remove(item)

                                if (mFollowList.value?.isEmpty() == true) {
                                    mEmptyViewVisible = ObservableBoolean(true)
                                    mFollowList.postValue(mutableListOf())
                                    notifyPropertyChanged(BR.mEmptyViewVisible)
                                }
                            })
                }, accessToken = accessToken, target = BookMarkTarget.SELLER.target, targetId = item.targetId)
            }
        })
    }

}