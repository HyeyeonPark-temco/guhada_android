package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Flag
import io.temco.guhada.common.listener.OnShippingAddressListener
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.UserShipping
import io.temco.guhada.data.viewmodel.shippingaddress.AddShippingAddressViewModel
import io.temco.guhada.databinding.DialogAddshippingaddressBinding
import io.temco.guhada.view.fragment.shippingaddress.ShippingAddressFormFragment
import java.awt.font.TextAttribute
import java.text.AttributedString

/**
 * 신규 배송지 등록 액티비티
 * @author Hyeyeon Park
 */
class AddShippingAddressActivity : AppCompatActivity(), OnShippingAddressListener {
    lateinit var mBinding: DialogAddshippingaddressBinding
    var addButtonVisible = false
    private lateinit var mViewModel: AddShippingAddressViewModel
    private lateinit var mShippingAddressFormFragment: ShippingAddressFormFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.dialog_addshippingaddress)

        addButtonVisible = intent.getBooleanExtra("addButtonVisible", false)
        mViewModel = AddShippingAddressViewModel(this)
        initForm()
        setDescription()

        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    private fun setDescription(){
        val description1 = resources.getString(R.string.addshippingaddress_description1)
        val description2 = resources.getString(R.string.addshippingaddress_description2)
        val description3 = resources.getString(R.string.addshippingaddress_description3)
        val spannable = SpannableString("$description1 $description2 $description3")
        spannable.setSpan(ForegroundColorSpan(resources.getColor(R.color.common_blue_purple)), description1.length, description1.length + description2.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        mBinding.textviewAddshippingaddressDescription.setText(spannable, TextView.BufferType.SPANNABLE)
    }

    private fun initForm() {
        mShippingAddressFormFragment = ShippingAddressFormFragment().apply {
            this.mViewModel = this@AddShippingAddressActivity.mViewModel
            this.addButtonVisible = this@AddShippingAddressActivity.addButtonVisible
        }
        supportFragmentManager.beginTransaction().add(mBinding.framelayoutAddshippingaddressForm.id, mShippingAddressFormFragment).commitAllowingStateLoss()
    }

    override fun closeActivity(resultCode: Int, shippingAddress: UserShipping?) {
        if (shippingAddress != null)
            intent.putExtra("shippingAddress", shippingAddress)
        setResult(resultCode, intent)
        finish()
    }

    override fun redirectSearchZipActivity() {
        startActivityForResult(Intent(this, SearchZipWebViewActivity::class.java), Flag.RequestCode.SEARCH_ZIP)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Flag.RequestCode.SEARCH_ZIP) {
            if (resultCode == Activity.RESULT_OK) {
                val address = data?.getStringExtra("address")
                val zip = data?.getStringExtra("zip")

                if (address == null || address.isEmpty() || zip == null || zip.isEmpty()) {
                    ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.common_message_error))
                } else {
                    mViewModel.newItem.zip = zip
                    mViewModel.newItem.address = address
                    mViewModel.newItem.roadAddress = address
                    mShippingAddressFormFragment.updateSearchZipResult(zip, address)
                }
            }
        }
    }

    // NOT USED
    override fun notifyDeleteItem() {}

    override fun redirectEditShippingAddressActivity(shippingAddress: UserShipping) {}
    override fun redirectAddShippingAddressActivity() {}

}