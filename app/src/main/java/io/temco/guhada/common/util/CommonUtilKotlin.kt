package io.temco.guhada.common.util

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import io.temco.guhada.common.Flag
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
}