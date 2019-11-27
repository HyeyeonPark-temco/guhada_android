package io.temco.guhada.data.server

import com.google.gson.Gson
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.base.BaseErrorModel
import io.temco.guhada.data.model.base.Message
import io.temco.guhada.data.model.coupon.CouponConsumption
import io.temco.guhada.data.model.coupon.CouponSaveProcess
import io.temco.guhada.data.model.order.OrderItemResponse
import io.temco.guhada.data.model.point.PointProcessParam
import io.temco.guhada.data.model.point.PointRequest
import io.temco.guhada.data.retrofit.manager.RetrofitManager
import io.temco.guhada.data.retrofit.service.BenefitService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import retrofit2.Call
import retrofit2.Response

class BenefitServer {
    companion object {

        @JvmStatic
        fun <C, R> resultListener(listener: OnServerListener, call: Call<C>, response: Response<R>) {
            if (response.code() in 200..400 && response.body() != null) {
                listener.onResult(true, response.body())
            } else {
                try {
                    var msg = Message()
                    var errorBody: String? = response.errorBody()?.string() ?: null
                    if (!errorBody.isNullOrEmpty()) {
                        var gson = Gson()
                        msg = gson.fromJson<Message>(errorBody, Message::class.java)
                    }
                    var error = BaseErrorModel(response.code(), response.raw().request().url().toString(), msg)
                    if (CustomLog.flag) CustomLog.L("saveReport", "onResponse body", error.toString())
                    listener.onResult(false, error)
                } catch (e: Exception) {
                    if (CustomLog.flag) CustomLog.E(e)
                    listener.onResult(false, null)
                }
            }
        }

        /**
         * 포인트 Summary 조회
         * @param expireDays 포인트 만료 예정 조회 일수
         */
        fun getPointSummary(listener: OnServerListener, accessToken: String, expireDays: Int) =
                RetrofitManager.createService(Type.Server.BENEFIT, BenefitService::class.java, true).getPointSummary(accessToken, expireDays).enqueue(
                        ServerCallbackUtil.ServerResponseCallback(successTask = { response -> listener.onResult(true, response.body()) })
                )


        /**
         * 포인트 리스트 조회
         * @param orderType 정렬 순서 (default: DESC; available: DESC, ASC)
         * @param sortType 정렬 조건 (default: CREATED, available: CREATED)
         * @param page 페이지 번호 (default 1)
         * @param unitPerPage 페이지당 표시 목록 수
         */
        fun getPoints(listener: OnServerListener, accessToken: String, orderType: String, sortType: String, page: Int, unitPerPage: Int) =
                RetrofitManager.createService(Type.Server.BENEFIT, BenefitService::class.java, true).getPoints(accessToken, orderType, sortType, page, unitPerPage).enqueue(
                        ServerCallbackUtil.ServerResponseCallback(successTask = { response -> listener.onResult(true, response.body()) })
                )

        /**
         * 포인트 리스트 기간별 조회
         * @param fromAt 조회 시작 기간 (required)
         * @param toAt 조회 종료 가간 (required)
         * @param charge 충전 내역 조회 (default: false)
         * @param historyStatus 상태 (available: DUE_SAVE, SAVED, DUE_CONSUMPTION, CONSUMPTION, RESTORE, EXPIRED, DUE_SAVE_CANCEL, SAVED_CANCEL, DUE_CONSUMPTION_CANCEL, CONSUMPTION_CANCEL, RESTORE_CANCEL, EXPIRED_CANCEL)
         * @param orderType 정렬 순서 (default: DESC; available: DESC, ASC)
         * @param sortType 정렬 대상 (default: CREATED, available: CREATED)
         * @param unitPerPage 페이지별 목록 수 (default: 10)
         * @param page 페이지 번호 (default: 1)
         * @param userId 사용자 아이디
         */
        fun getPointHistories(listener: OnServerListener, accessToken: String, fromAt: String, toAt: String, charge: Boolean, historyStatus: String, orderType: String, sortType: String, unitPerPage: Int, page: Int, userId: Int) =
                RetrofitManager.createService(Type.Server.BENEFIT, BenefitService::class.java, true).getPointHistories(accessToken, charge, historyStatus, orderType, sortType, fromAt, toAt, page, unitPerPage, userId).enqueue(
                        ServerCallbackUtil.ServerResponseCallback(successTask = { response -> listener.onResult(true, response.body()) }))

        /**
         * 쿠폰 리스트 조회
         */
        fun getCoupons(listener: OnServerListener, accessToken: String, isAvailable: Boolean, page: Int, unitPerPage: Int) =
                RetrofitManager.createService(Type.Server.BENEFIT, BenefitService::class.java, true).getCoupons(accessToken, isAvailable, page, unitPerPage).enqueue(
                        ServerCallbackUtil.ServerResponseCallback(successTask = { response -> listener.onResult(true, response.body()) }))

        /**
         * 쿠폰 삭제
         * @author Hyeyeon Park
         * @since 2019.08.28
         */
        fun deleteCoupon(listener: OnServerListener, accessToken: String, couponNumber: String) =
                RetrofitManager.createService(Type.Server.BENEFIT, BenefitService::class.java, true).deleteCoupon(accessToken, couponNumber).enqueue(
                        ServerCallbackUtil.ServerResponseCallback(successTask = { response -> listener.onResult(true, response.body()) }))

        /**
         * 쿠폰 삭제 (비동기)
         * @author Hyeyeon Park
         * @since 2019.08.28
         */
        fun deleteCouponAsync(accessToken: String, couponNumber: String) = GlobalScope.async {
            RetrofitManager.createService(Type.Server.BENEFIT, BenefitService::class.java, true).deleteCouponAsync(accessToken, couponNumber)
        }

        /**
         * 포인트 기록 삭제
         * @author Hyeyeon Park
         * @since 2019.08.28
         */
        fun deletePoint(listener: OnServerListener, accessToken: String, pointId: Long) =
                RetrofitManager.createService(Type.Server.BENEFIT, BenefitService::class.java, true).deletePoint(accessToken, pointId).enqueue(
                        ServerCallbackUtil.ServerResponseCallback(successTask = { response -> listener.onResult(true, response.body()) }))

        /**
         * 포인트 기록 삭제 (비동기)
         * @author Hyeyeon Park
         * @since 2019.08.28
         */
        fun deletePointAsync(accessToken: String, pointId: Long) = GlobalScope.async {
            RetrofitManager.createService(Type.Server.BENEFIT, BenefitService::class.java, true).deletePointAsync(accessToken, pointId)
        }

        /**
         * 적립 예정 포인트 조회
         * @author Hyeyeon Park
         * @since 2019.08.28
         */
        fun getExpectedPoint(listener: OnServerListener, accessToken: String, pointRequest: PointRequest) =
                RetrofitManager.createService(Type.Server.BENEFIT, BenefitService::class.java, true).getExpectedPoint(accessToken = accessToken, pointRequest = pointRequest).enqueue(
                        ServerCallbackUtil.ServerResponseCallback(successTask = { response -> listener.onResult(true, response.body()) }))

        /**
         * 발급 가능한 쿠폰 조회
         * saveActionType BUY == BUY + FOLLOW
         * @author Hyeyeon Park
         * @since 2019.08.29
         */
        fun getExpectedCoupon(listener: OnServerListener, accessToken: String, item: OrderItemResponse, saveActionType: String, serviceType: String) {
            val paymentPrice = if (item.sellPrice > 0) item.sellPrice else item.discountPrice
            RetrofitManager.createService(Type.Server.BENEFIT, BenefitService::class.java, true, false).getExpectedCoupon(
                    accessToken = accessToken,
                    DCategoryId = item.dCategoryId.toLong(),
                    LCategoryId = item.lCategoryId.toLong(),
                    MCategoryId = item.mCategoryId.toLong(),
                    SCategoryId = item.sCategoryId.toLong(),
                    dealId = item.dealId,
                    sellerId = item.sellerId.toLong(),
                    paymentPrice = paymentPrice,
                    saveActionType = saveActionType,
                    serviceType = serviceType).enqueue(
                    ServerCallbackUtil.ServerResponseCallback(successTask = { response -> listener.onResult(true, response.body()) }))
        }

        /**
         * 쿠폰 사용
         * @author Hyeyeon Park
         * @since 2019.09.10
         */
        fun useCoupon(listener: OnServerListener, accessToken: String, couponConsumption: CouponConsumption) =
                RetrofitManager.createService(Type.Server.BENEFIT, BenefitService::class.java, true).useCoupon(accessToken = accessToken, couponConsumption = couponConsumption).enqueue(ServerCallbackUtil.ServerResponseCallback(successTask = { response -> listener.onResult(true, response.body()) }))

        /**
         * 쿠폰 발급
         * @author Hyeyeon Park
         * @since 2019.09.10
         */
        fun saveCoupon(listener: OnServerListener, accessToken: String, couponSaveProcess: CouponSaveProcess) =
                RetrofitManager.createService(Type.Server.BENEFIT, BenefitService::class.java, true).saveCoupon(accessToken = accessToken, couponSaveProcess = couponSaveProcess).enqueue(ServerCallbackUtil.ServerResponseCallback(successTask = { response -> listener.onResult(true, response.body()) }))

        /**
         * 사용 가능 포인트, 쿠폰 갯수 조회
         * @author Hyeyeon Park
         * @since 2019.09.17
         */
        fun getAvailableBenefitCount(listener: OnServerListener, accessToken: String) =
                RetrofitManager.createService(Type.Server.BENEFIT, BenefitService::class.java, true).getAvailableBenefitCount(accessToken = accessToken).enqueue(ServerCallbackUtil.ServerResponseCallback(successTask = { response -> listener.onResult(true, response.body()) }))

        /**
         * 적립 예정 포인트 조회 API
         * @author Hyeyeon Park
         * @since 2019.09.18
         */
        fun getDueSavePoint(listener: OnServerListener, accessToken: String, pointProcessParam: PointProcessParam) =
                RetrofitManager.createService(Type.Server.BENEFIT, BenefitService::class.java, true).getBenefitDueSavePoint(accessToken = accessToken, pointProcessParam = pointProcessParam).enqueue(ServerCallbackUtil.ServerResponseCallback(successTask = { response -> listener.onResult(true, response.body()) }))

        /**
         * 적립 예정 포인트 조회 API
         * @author Hyeyeon Park
         * @since 2019.09.18
         */
        fun getDueSavePoint(listener: OnServerListener,pointProcessParam: PointProcessParam) =
                RetrofitManager.createService(Type.Server.BENEFIT, BenefitService::class.java, true).getBenefitDueSavePoint(pointProcessParam = pointProcessParam).enqueue(ServerCallbackUtil.ServerResponseCallback(successTask = { response -> listener.onResult(true, response.body()) }))


        /**
         * 구매확정 적립 예정 포인트
         * @author Hyeyeon Park
         * @since 2019.10.29
         */
        fun getConfirmProductDueSavePoint(listener: OnServerListener, accessToken: String, orderProdGroupId: Long) =
                RetrofitManager.createService(Type.Server.BENEFIT, BenefitService::class.java, true).getConfirmProductDueSavePoint(accessToken = accessToken, orderProdGroupId = orderProdGroupId).enqueue(ServerCallbackUtil.ServerResponseCallback(successTask = { response -> listener.onResult(true, response.body()) }))

    }
}