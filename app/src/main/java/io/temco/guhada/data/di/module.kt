package io.temco.guhada.data.di

import io.temco.guhada.common.listener.OnProductDetailListener
import io.temco.guhada.data.viewmodel.ProductDetailViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { (listener: OnProductDetailListener) -> ProductDetailViewModel(listener) }
}