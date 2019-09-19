package io.temco.guhada.common;

/**
 * @author park jungho
 *
 * 하단 탭버튼에 따른 메인으로 이동
 */
public class ActivityMoveToMain {

    // 메인으로 이동하는 code 값
    int resultCode = -1;
    int resultPageIndex = -1;
    // 메인으로 이동여부
    boolean isMoveToMain = false;
    boolean isInitMain = false;

    public ActivityMoveToMain(int resultCode, boolean isMoveToMain) {
        this.resultCode = resultCode;
        this.isMoveToMain = isMoveToMain;
    }
    public ActivityMoveToMain(int resultCode, boolean isMoveToMain, boolean isInitMain) {
        this.resultCode = resultCode;
        this.isMoveToMain = isMoveToMain;
        this.isInitMain = isInitMain;
    }

    public ActivityMoveToMain(int resultCode, int resultPageIndex, boolean isMoveToMain) {
        this.resultCode = resultCode;
        this.resultPageIndex = resultPageIndex;
        this.isMoveToMain = isMoveToMain;
    }

    public ActivityMoveToMain(int resultCode, int resultPageIndex, boolean isMoveToMain, boolean isInitMain) {
        this.resultCode = resultCode;
        this.resultPageIndex = resultPageIndex;
        this.isMoveToMain = isMoveToMain;
        this.isInitMain = isInitMain;
    }

    public void clear(){
        resultCode = -1;
        resultPageIndex = -1;
        isMoveToMain = false;
        isInitMain = false;
    }

    public int getResultCode() {
        return resultCode;
    }

    public int getResultPageIndex() {
        return resultPageIndex;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public boolean isMoveToMain() {
        return isMoveToMain;
    }

    public boolean isInitMain() {
        return isInitMain;
    }

    public void setMoveToMain(boolean moveToMain) {
        isMoveToMain = moveToMain;
    }
}
