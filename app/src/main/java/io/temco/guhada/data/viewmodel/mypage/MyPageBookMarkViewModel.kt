package io.temco.guhada.data.viewmodel.mypage

import android.content.Context
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

/**
 * 19.07.22
 * @author park jungho
 *
 * 찜한상품
    - 제품상세에 북마크 한 제품의 리스트가 보여짐
    - 각 제품별로 삭제(x) 누르면 별도의 팝업 없이 바로 삭제
    - 웹 프론트 작업 된 내용 공유
        ○ 내 찜한상품 // /my-page-controller/getUserLikeProductsUsingGET
        ○ 내 찜한상품 // getUserLikeProductsUsingGET
 *
 */
class MyPageBookMarkViewModel (val context : Context) : BaseObservableViewModel() {



}