package io.temco.guhada.view.custom.dialog

import androidx.fragment.app.FragmentManager
import io.temco.guhada.R
import io.temco.guhada.databinding.DialogMessageBinding
import io.temco.guhada.view.custom.dialog.base.BaseDialog

class CustomMessageDialog(msg: String) : BaseDialog<DialogMessageBinding>() {
    private var message: String = msg
    private var cancelButtonVisible: Boolean = false
    private lateinit var confirmTask: () -> Unit
    override fun getLayoutId(): Int = R.layout.dialog_message

    constructor(message: String, cancelButtonVisible: Boolean) : this(message) {
        this.message = message
        this.cancelButtonVisible = cancelButtonVisible
    }

    constructor(message: String, cancelButtonVisible: Boolean, confirmTask: () -> Unit) : this(message) {
        this.message = message
        this.cancelButtonVisible = cancelButtonVisible
        this.confirmTask = confirmTask
    }

    override fun show(manager: FragmentManager, tag: String?) {
        super.show(manager, tag)
    }

    override fun init() {
        mBinding.textMessage.text = message
        mBinding.setClickListener {
            confirmTask()
            dismissAllowingStateLoss()
        }
        mBinding.setCancelClickListener {
            dismissAllowingStateLoss()
        }
        mBinding.cancelButtonVisible = this.cancelButtonVisible
        mBinding.executePendingBindings()
    }
}