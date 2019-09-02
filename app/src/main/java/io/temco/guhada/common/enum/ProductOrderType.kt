package io.temco.guhada.common.enum

/**
 * 상품 정렬 enum class
 * DATE: 최신 순
 * PRICE_ASC: 낮은 가격 순
 * PRICE_DESC: 높은 가격 순
 * SCORE: 평점 순
 *
 * @author Hyeyeon Park
 * @since 2019.09.02
 */
enum class ProductOrderType(val type: String) {
    DATE("DATE"),
    PRICE_ASC("PRICE_ASC"),
    PRICE_DESC("PRICE_DESC"),
    SCORE("SCORE")
}