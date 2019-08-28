package io.temco.guhada.data.server

import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.retrofit.manager.RetrofitManager
import io.temco.guhada.data.retrofit.service.BenefitService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class BenefitServer {
    companion object {
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

    }
}