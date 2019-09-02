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
enum class ProductOrderType(val position: Int, val type: String, val label: String) {
    DATE(0, "DATE", "신상품순"),
    SCORE(1, "SCORE", "평점순"),
    PRICE_ASC(2, "PRICE_ASC", "낮은가격순"),
    PRICE_DESC(3, "PRICE_DESC", "높은가격순")
}