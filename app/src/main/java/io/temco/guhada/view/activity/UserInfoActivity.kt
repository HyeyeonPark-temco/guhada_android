package io.temco.guhada.view.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.text.Editable
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.DatePicker
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.Observer
import io.reactivex.Observable
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.common.enum.RequestCode
import io.temco.guhada.common.listener.OnBaseDialogListener
import io.temco.guhada.common.listener.OnBorderEditTextFocusListener
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CommonViewUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.model.user.UserUpdateInfo
import io.temco.guhada.data.viewmodel.mypage.UserInfoViewModel
import io.temco.guhada.databinding.ActivityUserinfoBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.CommonSpinnerAdapter
import io.temco.guhada.view.custom.BorderEditTextView
import java.util.*

/**
 * @author park jungho
 * @author Hyeyeon Park
 *
 * 회원 정보 등록
 *
 */
class UserInfoActivity : BindActivity<ActivityUserinfoBinding>() {

    private lateinit var mViewModel: UserInfoViewModel
    private lateinit var mLoadingIndicatorUtil: LoadingIndicatorUtil
    private lateinit var datePicker : DatePickerDialog
    private var isPassFocus = false
    private var loginType = -1



    override fun getBaseTag() = this::class.simpleName.toString()
    override fun getLayoutId() = R.layout.activity_userinfo
    override fun getViewType(): Type.View = Type.View.USER_INFO


    override fun init() {
        // 0 : email, 1 : naver, 2 : kakao, 3 : facebook, 4 : google
        loginType = intent?.extras?.getInt("loginType") ?: 0
        mBinding.loginType = loginType

        mLoadingIndicatorUtil = LoadingIndicatorUtil(this)
        mViewModel = UserInfoViewModel(this@UserInfoActivity)
        mBinding.viewModel = mViewModel

        mBinding.setOnClickCloseButton { finish() }
        mBinding.setOnClickOkButton {
            if (CustomLog.flag) CustomLog.L("UserInfoActivity", "mViewModel.isNickNameFocus", mViewModel.isNickNameFocus)
            if (CustomLog.flag) CustomLog.L("UserInfoActivity", "mViewModel.mIsNicknameValid", mViewModel.mIsNicknameValid.get())
            if (mViewModel.isNickNameFocus && mViewModel.mIsNicknameValid.get()) {
                mViewModel.getUserByNickName(mBinding.edittextMypageuserinfoNickname.text.toString(), object : OnCallBackListener {
                    override fun callBackListener(resultFlag: Boolean, value: Any) {
                        sendData()
                    }
                })
            } else sendData()
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkUserLogin()) setInitView()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RequestCode.VERIFY_USERINFO.flag -> { // 이메일, 휴대폰번호 인증
                if (resultCode == Activity.RESULT_OK) {
                    val email = data?.getStringExtra("email")
                    val mobile = data?.getStringExtra("mobile")
                    val name = data?.getStringExtra("name")

                    if (email != null) {
                        mBinding.edittextMypageuserinfoEmail.text = email
                        mViewModel.mUser.value!!.email = email
                    }

                    if (mobile != null) {
                        mBinding.textviewMypageuserinfoMobile.text = mobile
                        mViewModel.mUser.value!!.mobile = mobile
                        mViewModel.mUser.value!!.phoneNumber = mobile
                    }

                    if (!name.isNullOrEmpty()) {
                        mViewModel.mUser.value!!.name = name
                        mBinding.textviewMypageuserinfoName.text = name
                    }

                    mBinding.executePendingBindings()
                }
            }
            Flag.RequestCode.USER_SIZE -> if (resultCode == Activity.RESULT_OK) getUserSize()
        }
    }

    private fun sendData(){
        if(CustomLog.flag)CustomLog.L("UserInfoActivity","init",mViewModel.mUser.value!!)
        if(CustomLog.flag)CustomLog.L("UserInfoActivity","init edittextJoinPassword",mBinding.edittextJoinPassword.text.toString())
        if(CustomLog.flag)CustomLog.L("UserInfoActivity","init edittextJoinConfirmpassword",mBinding.edittextJoinConfirmpassword.text.toString())
        var userUpInfo : UserUpdateInfo = UserUpdateInfo().apply {
            if(!TextUtils.isEmpty(mBinding.edittextJoinPassword.text.toString()) || !TextUtils.isEmpty(mBinding.edittextJoinConfirmpassword.text.toString())){
                if(!TextUtils.isEmpty(mBinding.edittextJoinPassword.text.toString()) && !TextUtils.isEmpty(mBinding.edittextJoinConfirmpassword.text.toString()) &&
                        CommonUtil.validatePassword(mBinding.edittextJoinPassword.text.toString()) && mBinding.edittextJoinPassword.text.toString() == mBinding.edittextJoinConfirmpassword.text.toString()) {
                    setData(mViewModel.mUser.value!!, mBinding.edittextJoinPassword.text.toString(), null, false, false)
                }else if(mBinding.edittextJoinPassword.text.toString() != mBinding.edittextJoinConfirmpassword.text.toString()){
                    CommonViewUtil.showDialog(this@UserInfoActivity, resources.getString(R.string.findpwd_message_notequalpwd), false,  false)
                    return
                }else if(!CommonUtil.validatePassword(mBinding.edittextJoinPassword.text.toString()) && mBinding.edittextJoinPassword.text.toString() == mBinding.edittextJoinConfirmpassword.text.toString()){
                    CommonViewUtil.showDialog(this@UserInfoActivity, resources.getString(R.string.findpwd_message_invalidformat), false,  false)
                    return
                }else{
                    CommonViewUtil.showDialog(this@UserInfoActivity, "입력하신 비밀번호를 확인해 주세요.", false,  false)
                    return
                }
            }else{
                setData(mViewModel.mUser.value!!,null, null,false,false)
            }
        }
        if(CustomLog.flag)CustomLog.L("UserInfoActivity","init userUpInfo",userUpInfo)

        mViewModel.repository.updateUserInfo(userUpInfo, object : OnCallBackListener{
            override fun callBackListener(resultFlag: Boolean, value: Any) {
                if(CustomLog.flag)CustomLog.L("updateUserInfo callBackListener","value",value)
                if(resultFlag) {
                    CommonViewUtil.showDialog(this@UserInfoActivity, "회원정보를 수정하였습니다.", false, true)
                }else{
                    CommonViewUtil.showDialog(this@UserInfoActivity, "회원정보 수정중 오류가 발생되었습니다.\n[$value.toString()]", false,  false)
                }
            }
        })
    }

    private fun checkUserLogin(): Boolean {
        if (!CommonUtil.checkToken()) {
            CommonViewUtil.showDialog(this@UserInfoActivity, "로그인이 필요한 화면입니다.", false, object : OnBaseDialogListener {
                override fun onClickOk() {
                    finish()
                }
            })
            return false
        } else return true
    }


    private fun setInitView() {
        mViewModel.userId = CommonUtil.checkUserId()
        setUserData()
    }

    private fun setUserData() {
        if (CustomLog.flag) CustomLog.L("MyPageUserInfoLayout", "setUserData ", "--/banks---", mViewModel.userId)
        // 유저 정보 가져오기
        mViewModel.userCheck(object : OnCallBackListener {
            override fun callBackListener(resultFlag: Boolean, value: Any) {
                mBinding.user = mViewModel.mUser.value!!
                mBinding.includeMypageuserinfoBank.user = mViewModel.mUser.value!!
                mBinding.edittextMypageuserinfoNickname.text = Editable.Factory.getInstance().newEditable(mViewModel.mUser.value!!.nickname ?:"")

                // 생년월일
                setBirth()

                // 성별
                mViewModel.checkGenderValue.set(mViewModel.mUser.value!!.userGender)

                mBinding.executePendingBindings()
                if (CustomLog.flag) CustomLog.L("MyPageUserInfoLayout callBackListener", "resultFlag", resultFlag, "mViewModel.user", mViewModel.mUser.value!!)
                if (CustomLog.flag) CustomLog.L("MyPageUserInfoLayout callBackListener", "userEmail -----", mViewModel.userEmail)
            }
        })

        // 닉네임 변경
        setNickNameListener()

        // 환불 계좌정보
        initRefundAccountView()

        // 이메일 인증
        mBinding.edittextMypageuserinfoEmail.setOnClickListener { redirectUserInfoVerifyActivity(true) }

        // 휴대폰 번호 인증
        mBinding.textviewMypageuserinfoMobile.setOnClickListener { redirectUserInfoVerifyActivity(false) }

        // 유저 사이즈
        getUserSize()

        mBinding.buttonMypageuserinfoSizeinsert.setOnClickListener {
            var intent = Intent(this@UserInfoActivity, UserSizeUpdateActivity::class.java)
            if (mViewModel.mUserSize != null) intent.putExtra("userSize", mViewModel.mUserSize)
            (this@UserInfoActivity).startActivityForResult(intent, Flag.RequestCode.USER_SIZE)
        }

        mBinding.buttonMypageuserinfoSizemodify.setOnClickListener {
            var intent = Intent(this@UserInfoActivity, UserSizeUpdateActivity::class.java)
            if (mViewModel.mUserSize != null) intent.putExtra("userSize", mViewModel.mUserSize)
            (this@UserInfoActivity).startActivityForResult(intent, Flag.RequestCode.USER_SIZE)
        }

        mBinding.setOnClickEmailButton {
            mViewModel.mUser.value!!.agreeEmailReception = !mViewModel.mUser.value!!.agreeEmailReception
            mBinding.checkboxMypageuserinfoEmail.setImageResource(if(mViewModel.mUser.value!!.agreeEmailReception) R.drawable.checkbox_selected else R.drawable.checkbox_select)
        }
        mBinding.setOnClickSmsButton {
            mViewModel.mUser.value!!.agreeSmsReception = !mViewModel.mUser.value!!.agreeSmsReception
            mBinding.checkboxMypageuserinfoSms.setImageResource(if(mViewModel.mUser.value!!.agreeSmsReception) R.drawable.checkbox_selected else R.drawable.checkbox_select)
        }
    }

    private fun setBirth(){
        var cal = Calendar.getInstance()
        if(CustomLog.flag)CustomLog.L("DatePickerDialog onDateSet","birth",mViewModel.mUser.value?.birth ?: "")
        if(!TextUtils.isEmpty(mViewModel.mUser.value?.birth)){
            var birth = mViewModel.mUser.value?.birth!!.split("-")
            if(CustomLog.flag)CustomLog.L("DatePickerDialog onDateSet","birth",birth)
            mBinding.textviewMypageuserinfoBirthY.text = birth[0]+" 년"
            mBinding.textviewMypageuserinfoBirthM.text = birth[1]+" 월"
            mBinding.textviewMypageuserinfoBirthD.text = birth[2]+" 일"
            cal.apply {
                set(Calendar.YEAR, birth[0].toInt())
                set(Calendar.MONTH, (birth[1].toInt()-1))
                set(Calendar.DAY_OF_MONTH, birth[2].toInt())
            }
        }
        if(CustomLog.flag)CustomLog.L("DatePickerDialog onDateSet format",String.format("%04d-%02d-%02d",cal.get(Calendar.YEAR),(cal.get(Calendar.MONTH)+1),cal.get(Calendar.DAY_OF_MONTH)))
        mBinding.setOnClickDateButton {
            datePicker = DatePickerDialog(this@UserInfoActivity, object :DatePickerDialog.OnDateSetListener{
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                    if(CustomLog.flag)CustomLog.L("DatePickerDialog onDateSet OnDateSetListener",String.format("%04d-%02d-%02d",year,(month+1),dayOfMonth))
                    mViewModel.mUser.value!!.birth = String.format("%04d-%02d-%02d",year,(month+1),dayOfMonth)
                    setBirth()
                }
            },cal.get(Calendar.YEAR),(cal.get(Calendar.MONTH)),cal.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }
        //mBinding.executePendingBindings()
    }

    private fun redirectUserInfoVerifyActivity(isEmail: Boolean) {
        val intent = Intent(mBinding.root.context, VerifyUserInfoActivity::class.java)
        intent.putExtra("isEmail", isEmail)
        intent.putExtra("name", mViewModel.mUser?.value?.name ?: "")
        intent.putExtra("email", mViewModel.mUser?.value?.email ?: "")
        (mBinding.root.context as Activity).startActivityForResult(intent, RequestCode.VERIFY_USERINFO.flag)
    }

    private fun getUserSize() {
        mViewModel.repository.getUserSize(object : OnCallBackListener {
            override fun callBackListener(resultFlag: Boolean, value: Any) {
                if (resultFlag) {
                    setUserSize()
                } else {
                    mBinding.linearlayoutMypageuserinfoSizeinfo.visibility = View.GONE
                    mBinding.buttonMypageuserinfoSizeinsert.visibility = View.VISIBLE
                    mBinding.buttonMypageuserinfoSizemodify.visibility = View.GONE
                }
            }
        })
    }


    private fun setUserSize() {
        if (mViewModel.mUserSize.weight > 0 || mViewModel.mUserSize.shoe > 0 || mViewModel.mUserSize.height > 0 || mViewModel.mUserSize.bottom > 0 || !TextUtils.isEmpty(mViewModel.mUserSize.top)) {
            mBinding.linearlayoutMypageuserinfoSizeinfo.visibility = View.VISIBLE
            mBinding.buttonMypageuserinfoSizeinsert.visibility = View.GONE
            mBinding.buttonMypageuserinfoSizemodify.visibility = View.VISIBLE

            mBinding.textviewMypageuserinfoBottom.text = mViewModel.mUserSize.bottom.toString()
            mBinding.textviewMypageuserinfoHeight.text = mViewModel.mUserSize.height.toString()
            mBinding.textviewMypageuserinfoShoe.text = mViewModel.mUserSize.shoe.toString()
            mBinding.textviewMypageuserinfoTop.text = mViewModel.mUserSize.top
            mBinding.textviewMypageuserinfoWeight.text = mViewModel.mUserSize.weight.toString()
        } else {
            mBinding.linearlayoutMypageuserinfoSizeinfo.visibility = View.GONE
            mBinding.buttonMypageuserinfoSizeinsert.visibility = View.VISIBLE
            mBinding.buttonMypageuserinfoSizemodify.visibility = View.GONE
        }
        setEditTextFocusListener()
        mBinding.executePendingBindings()
    }


    private fun setEditTextFocusListener() {
        if (CustomLog.flag) CustomLog.L("JoinActivity", "setEditTextFocusListener")
        mBinding.edittextJoinPassword.setOnBorderEditTextFocusListener(object : OnBorderEditTextFocusListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if (!isPassFocus) isPassFocus = hasFocus
                if (isPassFocus && !hasFocus) {
                    isPassFocus = false
                    if (CustomLog.flag) CustomLog.L("JoinActivity", "edittextJoinPassword validationCheck", mBinding.edittextJoinPassword.text)
                    if (!TextUtils.isEmpty(mBinding.edittextJoinPassword.text) && !CommonUtil.validatePassword(mBinding.edittextJoinPassword.text)) {
                        mBinding.textviewJoinPasswordfocus.visibility = View.VISIBLE
                    } else {
                        mBinding.textviewJoinPasswordfocus.visibility = View.GONE
                    }
                } else
                    mBinding.textviewJoinPasswordfocus.visibility = View.GONE
            }
        })
        BorderEditTextView.setInverseBindingListener(mBinding.edittextJoinPassword) {
            val pas1 = mBinding.edittextJoinPassword.text
            val pas2 = mBinding.edittextJoinConfirmpassword.text
            if (!TextUtils.isEmpty(pas1) && !TextUtils.isEmpty(pas2) && pas1 != pas2) {
                mBinding.textviewJoinConfirmpasswordfocus.setText(R.string.findpwd_message_notequalpwd)
                mBinding.textviewJoinConfirmpasswordfocus.visibility = View.VISIBLE
            } else {
                if (!CommonUtil.validatePassword(mBinding.edittextJoinPassword.text)) {
                    mBinding.textviewJoinPasswordfocus.visibility = View.VISIBLE
                } else {
                    mBinding.textviewJoinPasswordfocus.visibility = View.GONE
                    mBinding.textviewJoinConfirmpasswordfocus.visibility = View.GONE
                }
            }
        }
        BorderEditTextView.setInverseBindingListener(mBinding.edittextJoinConfirmpassword) {
            val pas1 = mBinding.edittextJoinPassword.text
            val pas2 = mBinding.edittextJoinConfirmpassword.text
            if (!TextUtils.isEmpty(pas1) && !TextUtils.isEmpty(pas2) && pas1 != pas2) {
                mBinding.textviewJoinConfirmpasswordfocus.setText(R.string.findpwd_message_notequalpwd)
                mBinding.textviewJoinConfirmpasswordfocus.visibility = View.VISIBLE
            } else
                mBinding.textviewJoinConfirmpasswordfocus.visibility = View.GONE
        }
    }


    // 0 : email, 1 : naver, 2 : kakao, 3 : facebook, 4 : google
    private fun showLoginTypeUser() {
        var message = ""
        when (mViewModel.mypageUserInfoLoginCheckType.get()) {
            0 -> message = "이메일로 "
            1 -> message = "네이버로 "
            2 -> message = "카카오로 "
            3 -> message = "페이스북으로 "
            4 -> message = "구글로 "
        }
        message += "가입한 사용자 입니다."
        CommonViewUtil.showDialog(baseContext as MainActivity, message, false, false)
    }

    private fun setNickNameListener() {
        mBinding.edittextMypageuserinfoNickname.setOnFocusChangeListener { v, hasFocus ->
            if (!mViewModel.isNickNameFocus) mViewModel.isNickNameFocus = hasFocus
            if (mViewModel.isNickNameFocus && !hasFocus) {
                mViewModel.getUserByNickName(mBinding.edittextMypageuserinfoNickname.text.toString(), null)
            }
        }
    }

    private fun initRefundAccountView() {
        mViewModel.getRefundBanks()
        mBinding.includeMypageuserinfoBank.viewModel = mViewModel
        mViewModel.mBankNumInputAvailableTask = {
            if (mViewModel.mIsCheckAccountAvailable.get())
                mBinding.includeMypageuserinfoBank.buttonRequestrefundCheckaccount.text = resources.getString(R.string.requestorderstatus_refund_checkbank)
            else
                mBinding.includeMypageuserinfoBank.buttonRequestrefundCheckaccount.text = resources.getString(R.string.requestorderstatus_refund_checkbank_success)

            mBinding.executePendingBindings()
        }
        mViewModel.mRefundBanks.observe(this, Observer { banks ->
            val bankNameList = mutableListOf<String>()
            Observable.fromIterable(banks)
                    .map {
                        it.bankName
                    }.subscribe {
                        bankNameList.add(it)
                    }.dispose()

            bankNameList.add(mBinding.root.context.getString(R.string.requestorderstatus_refund_bankhint1))
            mBinding.includeMypageuserinfoBank.spinnerRequestorderstatusBank.adapter = CommonSpinnerAdapter(context = mBinding.root.context, layoutRes = R.layout.item_common_spinner, list = bankNameList).apply {
                this.mItemCount = bankNameList.size - 1
            }
            mBinding.includeMypageuserinfoBank.spinnerRequestorderstatusBank.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (position < banks.size) {
                        val selectedBank: PurchaseOrder.Bank? = banks[position]
                        if (selectedBank != null) {
                            mBinding.includeMypageuserinfoBank.textviewRequestrefundBankname.text = selectedBank.bankName
                            mViewModel.mRefundRequest.refundBankCode = selectedBank.bankCode

                            mViewModel.mIsCheckAccountAvailable = ObservableBoolean(true)
                            mViewModel.notifyPropertyChanged(BR.mIsCheckAccountAvailable)
                        }
                    }
                }
            }

            mViewModel.mBankAccount.observe(this, Observer {
                // 환불 계좌 정보
                val accountOwner = mViewModel.mRefundRequest.refundBankAccountOwner
                val accountNumber = mViewModel.mRefundRequest.refundBankAccountNumber
                val accountBankCode = mViewModel.mRefundRequest.refundBankCode

                mBinding.includeMypageuserinfoBank.edittextRequestrefundBankowner.setText(it.name)
            })

            mBinding.includeMypageuserinfoBank.spinnerRequestorderstatusBank.setSelection(bankNameList.size - 1)
        })
    }


}