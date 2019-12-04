package io.temco.guhada.common

import io.temco.guhada.common.util.CustomLog
import java.io.Serializable

/**
 * @author park jungho
 *
 * 하단 탭버튼에 따른 메인으로 이동
 */
class ActivityMoveToMain : Serializable{

    // 메인으로 이동하는 code 값
    var resultCode = -1
    var resultPageIndex = -1
        internal set
    // 메인으로 이동여부
    var isMoveToMain = false
    var isInitMain = false
        internal set

    var state : String = ""
    var arg1 : String = ""
    var arg2 : String = ""

    constructor(resultCode: Int, isMoveToMain: Boolean) {
        this.resultCode = resultCode
        this.isMoveToMain = isMoveToMain
    }

    constructor(resultCode: Int, isMoveToMain: Boolean, isInitMain: Boolean) {
        this.resultCode = resultCode
        this.isMoveToMain = isMoveToMain
        this.isInitMain = isInitMain
    }

    constructor(resultCode: Int, resultPageIndex: Int, isMoveToMain: Boolean) {
        this.resultCode = resultCode
        this.resultPageIndex = resultPageIndex
        this.isMoveToMain = isMoveToMain
    }

    constructor(resultCode: Int, resultPageIndex: Int, isMoveToMain: Boolean, isInitMain: Boolean) {
        this.resultCode = resultCode
        this.resultPageIndex = resultPageIndex
        this.isMoveToMain = isMoveToMain
        this.isInitMain = isInitMain
    }

    constructor(resultCode: Int, resultPageIndex: Int, isMoveToMain: Boolean, isInitMain: Boolean, state: String, arg1: String, arg2: String) {
        this.resultCode = resultCode
        this.resultPageIndex = resultPageIndex
        this.isMoveToMain = isMoveToMain
        this.isInitMain = isInitMain
        this.state = state
        this.arg1 = arg1
        this.arg2 = arg2
    }


    fun clear() {
        resultCode = -1
        resultPageIndex = -1
        isMoveToMain = false
        isInitMain = false
        state = ""
        arg1 = ""
        arg2 = ""
    }

    override fun toString(): String {
        if(CustomLog.flag)return "ActivityMoveToMain(resultCode=$resultCode, resultPageIndex=$resultPageIndex, isMoveToMain=$isMoveToMain, isInitMain=$isInitMain, state='$state', arg1='$arg1', arg2='$arg2')"
        else return ""
    }


}


enum class SchemeMoveType(val code : String, var label:String) {
    MAIN("main", "메인"),
    SEARCH("search", "검색"),
    EVENT("event", "이벤트"),
    PRODUCT("product", "상품"),
    JOIN("join", "가입이벤트"),
    TIMEDEAL("timedeal", "타임딜"),
    LUCKYDRAW("luckydraw", "럭키드로우"),
    SELLER("seller", "판매자"),
    PLANNING("planning", "기획전")
}
