package io.temco.guhada.data.viewmodel

import androidx.databinding.Bindable
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.auth0.android.jwt.JWT
import io.temco.guhada.common.enum.BookMarkTarget
import io.temco.guhada.common.enum.ProductOrderType
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.*
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.model.seller.Seller
import io.temco.guhada.data.model.seller.SellerSatisfaction
import io.temco.guhada.data.server.ProductServer
import io.temco.guhada.data.server.SearchServer
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class SellerInfoViewModel : BaseObservableViewModel() {
    var mSellerId = 0L
    var mOrder = ProductOrderType.DATE.type
    var mSeller: MutableLiveData<Seller> = MutableLiveData()
    var mSellerBookMark: MutableLiveData<BookMark> = MutableLiveData()
    var mSellerSatisfaction: MutableLiveData<SellerSatisfaction> = MutableLiveData()
    var mSellerFollowerCount: MutableLiveData<BookMarkCountResponse> = MutableLiveData()
    var mSellerProductList: MutableLiveData<ProductList> = MutableLiveData()

    var mSelectedTabPos = ObservableInt(0)
        @Bindable
        get() = field

    var mPage = 1
    var UNIT_PER_PAGE = 14

    enum class SellerInfoMore(val pos: Int) {
        SELLER_STORE(0),
        SELLER_INFO(1),
        REVIEW(2),
        BOARD(3),
        COMMENT(4),
        REPORT(5)
    }

    fun getSellerInfo() {
        if (mSellerId > 0) {
            UserServer.getSellerById(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            mSeller.postValue(it.data as Seller)
                        })
            }, sellerId = mSellerId)
        }
    }

    fun getSellerBookMark() {
        ServerCallbackUtil.callWithToken(
                task = {
                    val userId = JWT(it.split("Bearer ")[1]).getClaim("userId").asLong()
                    if (userId != null) {
                        UserServer.getLike(OnServerListener { success, o ->
                            ServerCallbackUtil.executeByResultCode(success, o,
                                    successTask = { result ->
                                        mSellerBookMark.postValue(result.data as BookMark)
                                    }
                            )
                        }, accessToken = it, target = BookMarkTarget.SELLER.target, targetId = mSellerId, userId = userId)
                    }
                }, invalidTokenTask = {})
    }

    fun getSellerSatisfaction() {
        UserServer.getSellerSatisfaction(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        mSellerSatisfaction.postValue(it.data as SellerSatisfaction)
                    })
        }, sellerId = mSellerId)
    }

    fun getSellerFollowCount() {
        UserServer.getBookMarkCount(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        mSellerFollowerCount.postValue(it.data as BookMarkCountResponse)
                    })
        }, target = BookMarkTarget.SELLER.target, targetId = mSellerId)
    }

    fun getSellerProductList() {
        SearchServer.getSellerProductList(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o, successTask = {
                this.mSellerProductList.postValue(it.data as ProductList)
            })
        }, sellerId = mSellerId, order = mOrder, page = mPage, unitPerPage = UNIT_PER_PAGE)
    }

    fun onClickFollow() {
        ServerCallbackUtil.callWithToken(task = { accessToken ->
            if (mSellerBookMark.value?.content?.isEmpty() == true) {
                val bookMarkResponse = BookMarkResponse(BookMarkTarget.SELLER.target, mSellerId)
                UserServer.saveBookMark(OnServerListener { success, o ->
                    ServerCallbackUtil.executeByResultCode(success, o,
                            successTask = { getSellerBookMark() })
                }, accessToken = accessToken, response = bookMarkResponse.getProductBookMarkRespose())
            } else {
                UserServer.deleteBookMark(OnServerListener { success, o ->
                    ServerCallbackUtil.executeByResultCode(success, o,
                            successTask = { getSellerBookMark() })
                }, accessToken = accessToken, target = BookMarkTarget.SELLER.target, targetId = mSellerId)
            }
        })
    }

    fun onClickMore() {
        mPage++
        getSellerProductList()
    }

}