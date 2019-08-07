package io.temco.guhada.common;

/**
 * @author park jungho
 *
 * 하단 탭버튼에 따른 메인으로 이동
 */
public class ActivityMoveToMain {

    // 메인으로 이동하는 code 값
    int resultCode = 0;
    // 메인으로 이동여부
    boolean isMoveToMain = false;

    public ActivityMoveToMain(int resultCode, boolean isMoveToMain) {
        this.resultCode = resultCode;
        this.isMoveToMain = isMoveToMain;
    }

    public void clear(){
        resultCode = 0;
        isMoveToMain = false;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public boolean isMoveToMain() {
        return isMoveToMain;
    }

    public void setMoveToMain(boolean moveToMain) {
        isMoveToMain = moveToMain;
    }
}
