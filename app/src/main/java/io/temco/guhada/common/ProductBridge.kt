package io.temco.guhada.common

import io.temco.guhada.view.activity.MainActivity

/**
 * 메인과 상품 상세 페이지 연결을 위한 임시 클래스
 * @author Hyeyeon Park
 */
class ProductBridge {
    companion object {
        var isOpen = false
        lateinit var mainActivity: MainActivity

        fun addProductDetailView(dealId: Long) {
            if (::mainActivity.isInitialized)
                mainActivity.addProductDetailView(dealId)
        }

        fun detachProductDetailView() {
            if (::mainActivity.isInitialized)
                mainActivity.detachProductDetailView()
        }

        fun showSideMenu() {
            if (::mainActivity.isInitialized) mainActivity.showSideMenu(!isOpen)
        }
    }
}