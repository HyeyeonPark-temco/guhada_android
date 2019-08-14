package io.temco.guhada.data.viewmodel

import androidx.lifecycle.MutableLiveData
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.Deal
import io.temco.guhada.data.model.ProductList
import io.temco.guhada.data.model.seller.Criteria
import io.temco.guhada.data.server.ProductServer
import io.temco.guhada.data.server.SearchServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class ProductDetailStoreViewModel : BaseObservableViewModel() {
    private val UNIT_PER_PAGE = 6
    var mRelatedProductList: MutableLiveData<ProductList> = MutableLiveData()
    var mRecommendProductList: MutableLiveData<ProductList> = MutableLiveData()
    var mSellerProductList: MutableLiveData<MutableList<Deal>> = MutableLiveData()
    lateinit var mCriteria: Criteria
    var page = 1

    fun getRelatedProductList() {
        SearchServer.getSellerRelatedProductList(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o, successTask = {
                this.mRelatedProductList.postValue(it.data as ProductList)
            })
        }, criteria = mCriteria, page = page, unitPerPage = UNIT_PER_PAGE)
    }

    fun getRecommendProductList() {
        SearchServer.getSellerPopularProductList(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o, successTask = {
                this.mRecommendProductList.postValue(it.data as ProductList)
            })
        }, criteria = mCriteria, page = page, unitPerPage = UNIT_PER_PAGE)
    }

    fun getSellerProductList() {
        ProductServer.getProductListBySellerId(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o, successTask = {
                this.mSellerProductList.postValue(it.data as MutableList<Deal>)
            })
        }, sellerId = mCriteria.sellerId, page = page, unitPerPage = UNIT_PER_PAGE)
    }
}