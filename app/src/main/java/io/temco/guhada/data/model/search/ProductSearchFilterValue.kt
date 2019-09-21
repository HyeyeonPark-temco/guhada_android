package io.temco.guhada.data.model.search

import io.temco.guhada.data.model.body.FilterBodyAttribute

/**
 * @author park jungho
 *
 * /search/filter   searchByFilter
 * 상품 딜 목록 API
 */
class ProductSearchFilterValue {

    var brandIds : ArrayList<Int> = arrayListOf()
    var categoryIds : ArrayList<Int> = arrayListOf()
    var filters : ArrayList<FilterBodyAttribute> = arrayListOf()
    var searchQueries : ArrayList<String> = arrayListOf()
    var searchResultOrder = ""
}