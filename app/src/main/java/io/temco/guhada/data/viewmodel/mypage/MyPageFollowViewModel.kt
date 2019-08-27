package io.temco.guhada.data.viewmodel.mypage

import android.content.Context
import android.util.Log
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

//                                callGetSeller()
                            })
                }, accessToken = token, target = BookMarkTarget.SELLER.target, userId = userId)
        })
    }

    private fun setEmptyViewVisible() {
        mEmptyViewVisible = ObservableBoolean(mFollowList.value?.isNotEmpty() ?: true)
        notifyPropertyChanged(BR.mEmptyViewVisible)
    }

    fun getSeller(sellerId: Long) {
        UserServer.getSellerById(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        val seller = it.data as Seller
                        mSeller.postValue(seller)
                        mSellerList.add(seller)

//                            notifyAdapter(seller)
//                            setMoreButtonVisible()
                    })
        }, sellerId = sellerId)
    }

    // 미사용
    private fun callGetSeller() {
        val list = mFollowList.value
        if (list != null) {
            for (i in page - 1 until UNIT_PER_PAGE * page) {
                if (i < list.size) {
                    val seller = list[i]
                    getSeller(seller.targetId)
                } else break
            }
        }
    }

    // 미사용
    private fun notifyAdapter(seller: Seller) {
        val addedSellerSize = mSellerList.size
        val totalCount = mFollowList.value?.size ?: 0

        if (totalCount > 0) {
            if (totalCount <= UNIT_PER_PAGE) {
                if (addedSellerSize == totalCount)
                    mNotifyDataChangedTask()
            } else {
//                if (addedSellerSize % UNIT_PER_PAGE == 0 || addedSellerSize == totalCount) {
//                    if (page == 1 && mTempSellerList.isEmpty()) {
//                        mNotifyDataChangedTask()
//                    } else {
//                        mTempSellerList.add(seller)
//                        mNotifyItemInsertedTask(UNIT_PER_PAGE * (page - 1), addedSellerSize - 1)
//                    }
//                } else
//                    mTempSellerList.add(seller)
            }
        }
    }

    // 미사용
    private fun setMoreButtonVisible() {
        val addedSellerSize = mSellerList.size
        val totalCount = mFollowList.value?.size ?: 0
        mMoreButtonVisible = ObservableBoolean(addedSellerSize < totalCount)
        notifyPropertyChanged(BR.mMoreButtonVisible)
    }

    // 미사용
    fun onClickMore() {
        page++

        val addedSellerSize = mSellerList.size
        val totalCount = mFollowList.value?.size ?: 0
        val lastIdx = if (addedSellerSize < UNIT_PER_PAGE * page) addedSellerSize else UNIT_PER_PAGE * page
        for (i in page - 1 until lastIdx) {
            if (i < totalCount) {
                val seller = mFollowList.value?.get(i)
                if (seller != null)
                    getSeller(seller.targetId)
                else return
            } else return
        }
    }
}