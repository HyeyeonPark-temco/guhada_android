package io.temco.guhada.common.util

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.temco.guhada.R
import io.temco.guhada.common.*
import io.temco.guhada.common.enum.ResultCode
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.data.db.GuhadaDB
import io.temco.guhada.view.activity.*

object CommonUtilKotlin  {

    /**
     * 구하다 문의하기
     * @param activity current Activity notNull
     *
     */
    @JvmStatic
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
    @JvmStatic
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


    fun startActivityWebview(activity: Activity, title : String, url : String, param : String = "") {
        val intent = Intent(activity, CustomWebViewEventActivity::class.java) //CustomWebViewEventActivity
        intent.putExtra("title",title)
        intent.putExtra("url",url)
        if(!TextUtils.isEmpty(param)){
            intent.putExtra("param",param)
        }
        activity.startActivityForResult(intent, Flag.RequestCode.BASE)
    }


    /**
     * 첫구매 관련 팝업
     * FIRST_VIEW - 첫구매 안내 팝업
     * FIRST_PURCHASE - 첫구매 후 팝업
     */
    @JvmStatic
    fun startActivityPopupDialog(activity: Activity, imgPath:String, state : PopupViewType) {
        if(state == PopupViewType.POPUP_VIEW_STOP){
            var str = Preferences.getMainBannerViewDialog(imgPath)
            if(TextUtils.isEmpty(str)){
                val intent = Intent(activity, MainBannerPopupActivity::class.java)
                intent.putExtra("state",state)
                intent.putExtra("imgPath",imgPath)
                activity.startActivityForResult(intent, Flag.RequestCode.BASE)
            }
        }else{
            val intent = Intent(activity, MainBannerPopupActivity::class.java)
            intent.putExtra("state",state)
            intent.putExtra("imgPath",imgPath)
            activity.startActivityForResult(intent, Flag.RequestCode.BASE)
        }
    }




//    fun startActivityImageDetail(activity: Activity, title : String?, path : String) {
//        val intent = Intent(activity, ImageDetailViewActivity::class.java)
//        if(title!=null)intent.putExtra("title",title)
//        intent.putExtra("path",path)
//        activity.startActivityForResult(intent, Flag.RequestCode.BASE)
//    }


    @JvmStatic
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


    /**
     * 스키마 이동 설정
     */
    @JvmStatic
    fun moveEventPage(act : Activity, param : String, param2 : String, isMainActivity : Boolean, isFinished : Boolean){
        if(TextUtils.isEmpty(param)) return
        if(CustomLog.flag)CustomLog.L("CommonUtilKotlin","moveEventPage param",param,"param2",param2)
        when(param){
            SchemeMoveType.MAIN.code->{
                if(isMainActivity){
                }else{
                    BaseApplication.getInstance().moveToMain = ActivityMoveToMain(ResultCode.GO_TO_MAIN_HOME.flag,true, isMainActivity)
                    act.setResult(Flag.ResultCode.GO_TO_MAIN_HOME)
                    act.onBackPressed()
                }
                if(isFinished)act.finish()
            }
            SchemeMoveType.JOIN.code->{  // 회원가입
                CommonUtil.startLoginPage(act)
                if(isFinished)act.finish()
            }

            SchemeMoveType.TIMEDEAL.code->{ // 타임딜
                if(isMainActivity){
                    EventBusHelper.sendEvent(EventBusData(Flag.RequestCode.HOME_MOVE, Info.MainHomeIndex.TIME_DEAL))
                }else{
                    BaseApplication.getInstance().moveToMain = ActivityMoveToMain(ResultCode.GO_TO_MAIN.flag, Info.MainHomeIndex.TIME_DEAL,true, isMainActivity)
                    act.setResult(Flag.ResultCode.GO_TO_MAIN_HOME)
                    act.onBackPressed()
                }
                if(isFinished)act.finish()
            }
            SchemeMoveType.LUCKYDRAW.code->{ // 럭키드로우
                if(isMainActivity){
                    EventBusHelper.sendEvent(EventBusData(Flag.RequestCode.HOME_MOVE, Info.MainHomeIndex.LUCKY_DRAW))
                }else{
                    BaseApplication.getInstance().moveToMain = ActivityMoveToMain(ResultCode.GO_TO_MAIN.flag, Info.MainHomeIndex.LUCKY_DRAW,true, isMainActivity)
                    act.setResult(Flag.ResultCode.GO_TO_MAIN_HOME)
                    act.onBackPressed()
                }
                if(isFinished)act.finish()
            }
            SchemeMoveType.EVENT.code->{ // 이벤트 리스트
                if(isMainActivity){
                    EventBusHelper.sendEvent(EventBusData(Flag.RequestCode.HOME_MOVE, Info.MainHomeIndex.EVENT_LIST))
                }else{
                    BaseApplication.getInstance().moveToMain = ActivityMoveToMain(ResultCode.GO_TO_MAIN.flag, Info.MainHomeIndex.EVENT_LIST,true, isMainActivity)
                    act.setResult(Flag.ResultCode.GO_TO_MAIN_HOME)
                    act.onBackPressed()
                }
                if(isFinished)act.finish()
            }
            SchemeMoveType.PLANNING.code->{ // 기획전 리스트
                if(isMainActivity){
                    EventBusHelper.sendEvent(EventBusData(Flag.RequestCode.HOME_MOVE, Info.MainHomeIndex.PLANNING_LIST))
                }else{
                    BaseApplication.getInstance().moveToMain = ActivityMoveToMain(ResultCode.GO_TO_MAIN.flag, Info.MainHomeIndex.PLANNING_LIST,true, isMainActivity)
                    act.setResult(Flag.ResultCode.GO_TO_MAIN_HOME)
                    act.onBackPressed()
                }
                if(isFinished)act.finish()
            }
            SchemeMoveType.PRODUCT.code->{ // 상품상세
                if(isMainActivity){
                    if(!TextUtils.isEmpty(param2)){
                        try {
                            CommonUtil.startProductActivity(act, param2.toLong())
                        }catch (e : Exception){
                            if(CustomLog.flag)CustomLog.E(e)
                        }
                    }
                }else{
                    BaseApplication.getInstance().moveToMain = ActivityMoveToMain(ResultCode.GO_TO_MAIN.flag,true, isMainActivity)
                    act.setResult(Flag.ResultCode.GO_TO_MAIN_HOME)
                    act.onBackPressed()
                }
                if(isFinished)act.finish()
            }
            SchemeMoveType.SEARCH.code->{ // 검색목록
                if(isMainActivity){
                    var deStr : String = if(param2.matches(Regex(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*"))) param2 else String(Base64.decode(param2, Base64.URL_SAFE))
                    if(CustomLog.flag)CustomLog.L("CommonUtilKotlin","moveEventPage SEARCH param2",param2,"deStr",deStr)
                    CommonUtil.startSearchListActivity(act,deStr,true)
                }else{
                    BaseApplication.getInstance().moveToMain = ActivityMoveToMain(ResultCode.GO_TO_MAIN.flag,true, isMainActivity)
                    act.setResult(Flag.ResultCode.GO_TO_MAIN_HOME)
                    act.onBackPressed()
                }
                if(isFinished)act.finish()
            }
        }
    }


    fun moveGuhadaTokenAddress(act : Activity, tokenName : String){
        val intent = Intent(act, GuhadaTokenAddressCreateDialog::class.java)
        intent.putExtra("tokenName",tokenName)
        act.startActivityForResult(intent,Flag.RequestCode.GUHADA_TOKEN_ADDRESS)
    }


    fun movePlanningDealDetail(act : Activity, id : Int, url : String){
        val intent = Intent(act, PlanningDealDetailActivity::class.java)
        intent.putExtra("planningDealDetailId",id)
        intent.putExtra("url",url)
        act.startActivityForResult(intent,Flag.RequestCode.PLANNING_DEAL_DETAIL)
    }


}