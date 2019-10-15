package io.temco.guhada.data.viewmodel

import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class PhotoPagerViewModel : BaseObservableViewModel() {
    var mUrlList = mutableListOf<String>()
    var mCurrentPos = 1
}