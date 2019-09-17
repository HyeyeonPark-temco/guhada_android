package io.temco.guhada.common.util

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.view.activity.CustomWebViewActivity
import io.temco.guhada.view.activity.ImageDetailViewActivity
import io.temco.guhada.view.activity.UserClaimGuhadaActivity
import io.temco.guhada.view.activity.UserClaimSellerActivity

object CommonUtilKotlin  {

    /**
     * 구하다 문의하기
     * @param activity current Activity notNull
     *
     */
    fun startActivityUserClaimGuhada(activity: AppCompatActivity) {
        if(CommonUtil.checkToken()){
            val intent = Intent(activity, UserClaimGuhadaActivity::class.java)
            activity.startActivityForResult(intent, Flag.RequestCode.USER_CLAIM_GUHADA)
        }else{
            CommonViewUtil.showDialog(activity,"회원가입한 사용자만 가능합니다.",false,null)
        }
    }


    /**
     * 판매자 문의하기
     * @param activity current Activity notNull
     *
     */
    fun startActivityUserClaimSeller(activity: AppCompatActivity, sellerId : Long, productId : Long, orderProdGroupId : Long) {
        if(CommonUtil.checkToken()){
            val intent = Intent(activity, UserClaimSellerActivity::class.java)
            intent.putExtra("sellerId",sellerId)
            intent.putExtra("productId",productId)
            intent.putExtra("orderProdGroupId",orderProdGroupId)
            activity.startActivityForResult(intent, Flag.RequestCode.USER_CLAIM_SELLER)
        }else{
            CommonViewUtil.showDialog(activity,"회원가입한 사용자만 가능합니다.",false,null)
        }
    }

    /**
     *1. 구매이용 약관 /terms/purchase
     *2. 판매 이용약관 /terms/sale
     *3. 개인정보 수집 및 이용 /terms/personal
     *4. 위치 기반 이용 약관 /terms/location
     *5. 청소년 보호법 이용 약관 /terms/youth
     *6. 개인 정보 보호 조치 /terms/privacy
     *7. 채무 보증확인 /terms/guarantee
     */

    fun startTermsPurchase(activity: AppCompatActivity)=startActivityWebview(activity,activity.resources.getString(R.string.join_agreebuy), Type.Server.getUrl(Type.Server.WEB)+"terms/purchase")
    fun startTermsSale(activity: AppCompatActivity)=startActivityWebview(activity,activity.resources.getString(R.string.join_agreesell), Type.Server.getUrl(Type.Server.WEB)+"terms/sale")
    fun startTermsPersonal(activity: AppCompatActivity)=startActivityWebview(activity,activity.resources.getString(R.string.join_agreeprivacy), Type.Server.getUrl(Type.Server.WEB)+"terms/personal")
    fun startTermsLocation(activity: AppCompatActivity)=startActivityWebview(activity,activity.resources.getString(R.string.join_agreeprivacy), Type.Server.getUrl(Type.Server.WEB)+"terms/location")
    fun startTermsYouth(activity: AppCompatActivity)=startActivityWebview(activity,activity.resources.getString(R.string.join_agreeprivacy), Type.Server.getUrl(Type.Server.WEB)+"terms/youth")
    fun startTermsPrivacy(activity: AppCompatActivity)=startActivityWebview(activity,activity.resources.getString(R.string.join_agreeprivacy), Type.Server.getUrl(Type.Server.WEB)+"terms/privacy")
    fun startTermsGuarantee(activity: AppCompatActivity)=startActivityWebview(activity,activity.resources.getString(R.string.join_agreeprivacy), Type.Server.getUrl(Type.Server.WEB)+"terms/guarantee")



    fun startActivityWebview(activity: AppCompatActivity, title : String, url : String) {
        val intent = Intent(activity, CustomWebViewActivity::class.java)
        intent.putExtra("title",title)
        intent.putExtra("url",url)
        activity.startActivityForResult(intent, Flag.RequestCode.BASE)
    }


    fun startActivityImageDetail(activity: AppCompatActivity, title : String?, path : String) {
        val intent = Intent(activity, ImageDetailViewActivity::class.java)
        if(title!=null)intent.putExtra("title",title)
        intent.putExtra("path",path)
        activity.startActivityForResult(intent, Flag.RequestCode.BASE)
    }


}