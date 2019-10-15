package io.temco.guhada.common.util

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.data.db.GuhadaDB
import io.temco.guhada.view.activity.CustomWebViewActivity
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

    fun startTermsPurchase(activity: Activity)=startActivityWebview(activity,activity.resources.getString(R.string.join_agreebuy), Type.Server.getUrl(Type.Server.WEB)+"terms/purchase")
    fun startTermsSale(activity: Activity)=startActivityWebview(activity,activity.resources.getString(R.string.join_agreesell), Type.Server.getUrl(Type.Server.WEB)+"terms/sale")
    fun startTermsPersonal(activity: Activity)=startActivityWebview(activity,activity.resources.getString(R.string.join_agreeprivacy), Type.Server.getUrl(Type.Server.WEB)+"terms/personal")
    fun startTermsLocation(activity: Activity)=startActivityWebview(activity,"위치 기반 이용 약관", Type.Server.getUrl(Type.Server.WEB)+"terms/location")
    fun startTermsYouth(activity: Activity)=startActivityWebview(activity,"청소년 보호법 이용 약관", Type.Server.getUrl(Type.Server.WEB)+"terms/youth")
    fun startTermsPrivacy(activity: Activity)=startActivityWebview(activity,"개인 정보 보호 조치", Type.Server.getUrl(Type.Server.WEB)+"terms/privacy")
    fun startTermsGuarantee(activity: Activity)=startActivityWebview(activity,"채무 보증확인", Type.Server.getUrl(Type.Server.WEB)+"terms/guarantee")
    fun startTermsCompany(activity: Activity)=startActivityWebview(activity,"사업자정보확인", "http://ftc.go.kr/bizCommPop.do?wrkr_no=8768601259")


    fun startActivityWebview(activity: Activity, title : String, url : String) {
        val intent = Intent(activity, CustomWebViewActivity::class.java)
        intent.putExtra("title",title)
        intent.putExtra("url",url)
        activity.startActivityForResult(intent, Flag.RequestCode.BASE)
    }


//    fun startActivityImageDetail(activity: Activity, title : String?, path : String) {
//        val intent = Intent(activity, ImageDetailViewActivity::class.java)
//        if(title!=null)intent.putExtra("title",title)
//        intent.putExtra("path",path)
//        activity.startActivityForResult(intent, Flag.RequestCode.BASE)
//    }


    fun recentProductCount(disposable: CompositeDisposable, db : GuhadaDB, listener: OnCallBackListener) {
        disposable.add(io.reactivex.Observable.fromCallable<Int> {
            db.recentDealDao().getAll(20).size
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    listener.callBackListener(true,result)
                }
        )
    }

    fun setMobileNumber(mobile : String) : String {
        var mo = ""
        if(mobile.substring(0,2) == "01" && mobile.length in 10..11){
            when(mobile.length){
                10-> mo = mobile.substring(0,3)+"-"+mobile.substring(3,6)+"-"+mobile.substring(6,mobile.length)
                11-> mo = mobile.substring(0,3)+"-"+mobile.substring(3,7)+"-"+mobile.substring(7,mobile.length)
            }
        }else if(mobile.substring(0,2) == "02" && mobile.length in 9..10){
            when(mobile.length){
                9-> mo = mobile.substring(0,2)+"-"+mobile.substring(2,5)+"-"+mobile.substring(5,mobile.length)
                10-> mo = mobile.substring(0,2)+"-"+mobile.substring(2,6)+"-"+mobile.substring(6,mobile.length)
            }
        }else if(mobile.length in 10..11){
            when(mobile.length){
                10-> mo = mobile.substring(0,3)+"-"+mobile.substring(3,6)+"-"+mobile.substring(6,mobile.length)
                11-> mo = mobile.substring(0,3)+"-"+mobile.substring(3,7)+"-"+mobile.substring(7,mobile.length)
            }
        }else if(mobile.length == 8){
            mo = mobile.substring(0,4)+"-"+mobile.substring(4,mobile.length)
        }else{
            mo = mobile
        }
        return mo
    }

}