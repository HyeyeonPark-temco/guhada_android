package io.temco.guhada.data.model.option

/**
 * 상품 옵션 정보 클래스
 * 앱에서 사용되는 형태로 생성됨
 * @see OptionInfo 웹에서 사용되는 형태의 클래스
 * @author Hyeyeon Park
 */
class Option {
    var type: String = ""
    var label: String = ""
    var attributes: List<String> = ArrayList()
    var rgb: List<String> = ArrayList()
}