package io.temco.guhada.view.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.InverseBindingListener;

import org.jetbrains.annotations.NotNull;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnBorderEditTextFocusListener;
import io.temco.guhada.common.listener.OnJoinListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.common.util.CommonUtilKotlin;
import io.temco.guhada.common.util.CustomLog;
import io.temco.guhada.data.model.claim.Claim;
import io.temco.guhada.data.viewmodel.account.JoinViewModel;
import io.temco.guhada.databinding.ActivityJoinBinding;
import io.temco.guhada.view.activity.base.BindActivity;
import io.temco.guhada.view.custom.BorderEditTextView;

public class JoinActivity extends BindActivity<ActivityJoinBinding> {
    private JoinViewModel mViewModel;
    private boolean isEmailFocus = false;
    private boolean isPassFocus = false;

    @Override
    protected String getBaseTag() {
        return JoinActivity.class.getSimpleName();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_join;
    }

    @Override
    protected Type.View getViewType() {
        return Type.View.JOIN;
    }

    @Override
    protected void init() {
        mViewModel = new JoinViewModel(new OnJoinListener() {
            @Override
            public void showMessage(String message) {
                Toast.makeText(JoinActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void closeActivity(int resultCode) {
                finish();
            }

            @Override
            public void showSnackBar(String message) {
                CommonUtil.showSnackBar(mBinding.linearlayoutJoinForm, message);
            }
        });
        mViewModel.setToolBarTitle(getResources().getString(R.string.join_title));
        mBinding.includeJoinHeader.setViewModel(mViewModel);
        mBinding.setViewModel(mViewModel);

        mBinding.setPersonalClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtilKotlin.INSTANCE.startTermsPersonal(JoinActivity.this);
            }
        });

        mBinding.setPurchaseClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtilKotlin.INSTANCE.startTermsPurchase(JoinActivity.this);
            }
        });

        mBinding.setSaleClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtilKotlin.INSTANCE.startTermsSale(JoinActivity.this);
            }
        });

        setEditTextFocusListener();
        mBinding.executePendingBindings();

    }


    private void setEditTextFocusListener(){
        if(CustomLog.INSTANCE.getFlag())CustomLog.INSTANCE.L("JoinActivity","setEditTextFocusListener");
        mBinding.edittextJoinEmail.setOnBorderEditTextFocusListener(new OnBorderEditTextFocusListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!isEmailFocus) isEmailFocus = hasFocus;
                if(isEmailFocus && !hasFocus){
                    isEmailFocus = false;
                    if(CustomLog.INSTANCE.getFlag())CustomLog.INSTANCE.L("JoinActivity","edittextJoinEmail validationCheck",mBinding.edittextJoinEmail.getText());
                    if(TextUtils.isEmpty(mBinding.edittextJoinEmail.getText())){
                        mBinding.textviewJoinEmailfocus.setText(R.string.findpwd_message_invalidemailformat_none);
                        mBinding.textviewJoinEmailfocus.setVisibility(View.VISIBLE);
                    }else{
                        if(!TextUtils.isEmpty(mBinding.edittextJoinEmail.getText()) && !CommonUtil.validateEmail(mBinding.edittextJoinEmail.getText())) {
                            mBinding.textviewJoinEmailfocus.setText(R.string.findpwd_message_invalidemailformat);
                            mBinding.textviewJoinEmailfocus.setVisibility(View.VISIBLE);
                        }else mBinding.textviewJoinEmailfocus.setVisibility(View.GONE);
                    }
                }else mBinding.textviewJoinEmailfocus.setVisibility(View.GONE);
            }
        });
        mBinding.edittextJoinPassword.setOnBorderEditTextFocusListener(new OnBorderEditTextFocusListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!isPassFocus) isPassFocus = hasFocus;
                if(isPassFocus && !hasFocus){
                    isPassFocus = false;
                    if(CustomLog.INSTANCE.getFlag())CustomLog.INSTANCE.L("JoinActivity","edittextJoinPassword validationCheck",mBinding.edittextJoinPassword.getText());
                    if(!TextUtils.isEmpty(mBinding.edittextJoinPassword.getText()) && !CommonUtil.validatePassword(mBinding.edittextJoinPassword.getText())) mBinding.textviewJoinPasswordfocus.setVisibility(View.VISIBLE);
                    else mBinding.textviewJoinPasswordfocus.setVisibility(View.GONE);
                }else mBinding.textviewJoinPasswordfocus.setVisibility(View.GONE);
            }
        });
        BorderEditTextView.setInverseBindingListener(mBinding.edittextJoinPassword, new InverseBindingListener() {
            @Override
            public void onChange() {
                String pas1 = mBinding.edittextJoinPassword.getText();
                String pas2 = mBinding.edittextJoinConfirmpassword.getText();
                if(!TextUtils.isEmpty(pas1) && !TextUtils.isEmpty(pas2) && !(pas1.equals(pas2))){
                    mBinding.textviewJoinConfirmpasswordfocus.setText(R.string.findpwd_message_notequalpwd);
                    mBinding.textviewJoinConfirmpasswordfocus.setVisibility(View.VISIBLE);
                }else mBinding.textviewJoinConfirmpasswordfocus.setVisibility(View.GONE);
            }
        });
        BorderEditTextView.setInverseBindingListener(mBinding.edittextJoinConfirmpassword, new InverseBindingListener() {
            @Override
            public void onChange() {
                String pas1 = mBinding.edittextJoinPassword.getText();
                String pas2 = mBinding.edittextJoinConfirmpassword.getText();
                if(!TextUtils.isEmpty(pas1) && !TextUtils.isEmpty(pas2) && !(pas1.equals(pas2))){
                    mBinding.textviewJoinConfirmpasswordfocus.setText(R.string.findpwd_message_notequalpwd);
                    mBinding.textviewJoinConfirmpasswordfocus.setVisibility(View.VISIBLE);
                }else mBinding.textviewJoinConfirmpasswordfocus.setVisibility(View.GONE);
            }
        });
    }

}
