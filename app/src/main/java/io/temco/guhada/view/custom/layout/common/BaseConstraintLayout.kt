package io.temco.guhada.view.custom.layout.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.OnLifecycleEvent

abstract class BaseConstraintLayout<B : ViewDataBinding, T : BaseObservableViewModel> : ConstraintLayout, LifecycleOwner, LifecycleObserver {

    // -------- LOCAL VALUE --------
    protected lateinit var mBinding: B
    protected lateinit var mViewModel: T
    protected lateinit var view: View

    private val lifecycleRegistry = LifecycleRegistry(this)
    // -----------------------------

    constructor(context: Context?) : super(context) {
        initBinding()
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initBinding()
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initBinding()
        init()
    }

    ////////////////////////////////////////////////
    // ABSTRACT
    ////////////////////////////////////////////////

    protected abstract fun getBaseTag(): String

    protected abstract fun getLayoutId(): Int

    protected abstract fun init()

    protected fun initBinding() {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), getLayoutId(), this, false)
        view = mBinding.root
        if (this.childCount > 0) removeAllViews()
        addView(view)
    }

    ////////////////////////////////////////////////

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun onStateEvent(owner: LifecycleOwner, event: Lifecycle.Event) {
        lifecycleRegistry.handleLifecycleEvent(event)
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////

}
