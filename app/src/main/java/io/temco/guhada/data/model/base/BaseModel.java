package io.temco.guhada.data.model.base;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.temco.guhada.common.util.CustomLog;

/**
 * @author park jungho
 * 19.07.18
 * Json을 파싱하는 기본 모델 새로 작성
 *
 */
public class BaseModel<T> {

    @SerializedName("resultCode")
    public int resultCode;

    @SerializedName("message")
    public String message;

    // list 로 내려오는 데이터
    @SerializedName("list")
    public List<T> list;

    // JsonObject로 내려오는 데이터
    @SerializedName("data")
    public T data;

    // resultCode 가 200..400 범위 이외의 값을 json의 toString 의 형태로 넣음
    @SerializedName("error")
    public String error;

    @SerializedName("result")
    public String result;

    public BaseErrorModel errorModel = null;

}