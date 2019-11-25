package io.temco.guhada.view.custom.dialog

import androidx.fragment.app.FragmentManager
import io.temco.guhada.R
import io.temco.guhada.databinding.DialogMessageBinding
import io.temco.guhada.view.custom.dialog.base.BaseDialog

/**
 * Dialog (text + ok btn + cancel btn)
 * @author Hyeyeon Park
 */
class CustomMessageDialog(msg: String) : BaseDialog<DialogMessageBinding>() {
    private var message: String = msg
    private var cancelButtonVisible: Boolean = false
    private lateinit var confirmTask: () -> Unit
    private lateinit var cancelTask: () -> Unit
    private var confirmBtnName : String? = null
    override fun getLayoutId(): Int = R.layout.dialog_message

    constructor(message: String, cancelButtonVisible: Boolean) : this(message) {
        this.message = message
        this.cancelButtonVisible = cancelButtonVisible
        this.isCancelable = false
    }

    constructor(message: String, cancelButtonVisible: Boolean, confirmTask: () -> Unit) : this(message) {
        this.message = message
        this.cancelButtonVisible = cancelButtonVisible
        this.confirmTask = confirmTask
        this.isCancelable = false
    }


    constructor(message: String, cancelTask: () -> Unit, confirmTask: () -> Unit) : this(message) {
        this.message = message
        this.cancelButtonVisible = true
        this.cancelTask = cancelTask
        this.confirmTask = confirmTask
        this.isCancelable = false
    }


    constructor(message: String, cancelTask: () -> Unit, confirmTask: () -> Unit, confirmBtnName:String) : this(message) {
        this.message = message
        this.cancelButtonVisible = true
        this.cancelTask = cancelTask
        this.confirmTask = confirmTask
        this.confirmBtnName = confirmBtnName
        this.isCancelable = false
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
            if(::cancelTask.isInitialized) cancelTask()
            dismissAllowingStateLoss()
        }
        mBinding.cancelButtonVisible = this.cancelButtonVisible
        if(confirmBtnName != null) mBinding.buttonConfirm.text = confirmBtnName
        mBinding.executePendingBindings()
    }
}