package io.temco.guhada.data.viewmodel

import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.auth0.android.jwt.JWT
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.enum.LikeTarget
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.BookMark
import io.temco.guhada.data.model.BookMarkResponse
import io.temco.guhada.data.model.Deal
import io.temco.guhada.data.model.ProductList
import io.temco.guhada.data.model.seller.Criteria
import io.temco.guhada.data.model.seller.Seller
import io.temco.guhada.data.server.ProductServer
import io.temco.guhada.data.server.SearchServer
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class ProductDetailStoreViewModel : BaseObservableViewModel() {
    private val UNIT_PER_PAGE = 6
    var mRelatedProductList: MutableLiveData<ProductList> = MutableLiveData()
    var mRecommendProductList: MutableLiveData<ProductList> = MutableLiveData()
    var mSellerProductList: MutableLiveData<MutableList<Deal>> = MutableLiveData()
    var mSeller: Seller = Seller()
        @Bindable
        get() = field
    var mSellerBookMark: BookMark = BookMark()
        @Bindable
        get() = field
    lateinit var mCriteria: Criteria
    var mPage = 1

    fun getRelatedProductList() {
        SearchServer.getSellerRelatedProductList(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o, successTask = {
                this.mRelatedProductList.postValue(it.data as ProductList)
            })
        }, criteria = mCriteria, page = mPage, unitPerPage = UNIT_PER_PAGE)
    }

    fun getRecommendProductList() {
        SearchServer.getSellerPopularProductList(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o, successTask = {
                this.mRecommendProductList.postValue(it.data as ProductList)
            })
        }, criteria = mCriteria, page = mPage, unitPerPage = UNIT_PER_PAGE)
    }

    fun getSellerProductList() {
        ProductServer.getProductListBySellerId(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o, successTask = {
                this.mSellerProductList.postValue(it.data as MutableList<Deal>)
            })
        }, sellerId = mCriteria.sellerId, page = mPage, unitPerPage = UNIT_PER_PAGE)
    }

    fun getSellerInfo() {
        UserServer.getSellerById(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        this.mSeller = it.data as Seller
                        notifyPropertyChanged(BR.mSeller)
                    })
        }, sellerId = mCriteria.sellerId)
    }

    fun getSellerLike(target: String) {
        ServerCallbackUtil.callWithToken(
                task = {
                    val userId = JWT(it.split("Bearer ")[1]).getClaim("userId").asLong()
                    if (userId != null) {
                        UserServer.getLike(OnServerListener { success, o ->
                            ServerCallbackUtil.executeByResultCode(success, o,
                                    successTask = { result ->
                                        this.mSellerBookMark = result.data as BookMark
                                        notifyPropertyChanged(BR.mSellerBookMark)
                                    }
                            )
                        }, accessToken = it, target = target, targetId = mCriteria.sellerId, userId = userId)
                    }
                })
    }

    fun onClickSellerBookMark() {
        ServerCallbackUtil.callWithToken(
                task = {
                    val userId = JWT(it.split("Bearer ")[1]).getClaim("userId").asLong()
                    if (userId != null) {
                        if (mSellerBookMark.content.isEmpty())
                            saveBookMark(accessToken = it, target = LikeTarget.SELLER.target, userId = userId)
                        else
                            deleteBookMark(accessToken = it, target = LikeTarget.SELLER.target, targetId = mCriteria.sellerId)
                    }

                },
                invalidTokenTask = {
                    ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.login_message_requiredlogin))
                })
    }

    private fun saveBookMark(accessToken: String, target: String, userId: Long) {
        val bookMarkResponse = BookMarkResponse(target, mCriteria.sellerId)
        UserServer.saveBookMark(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        this.mSellerBookMark.content = mutableListOf(BookMark().Content().apply {
                            this.target = target
                            this.targetId = mCriteria.sellerId
                            this.userId = userId
                        })
                        notifyPropertyChanged(BR.mSellerBookMark)
                    }
            )
        }, accessToken = accessToken, response = bookMarkResponse.getProductBookMarkRespose())
    }

    private fun deleteBookMark(accessToken: String, target: String, targetId: Long) {
        UserServer.deleteBookMark(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        this.mSellerBookMark.content = mutableListOf()
                        notifyPropertyChanged(BR.mSellerBookMark)
                    }
            )
        }, accessToken = accessToken, target = target, targetId = targetId)
    }
}