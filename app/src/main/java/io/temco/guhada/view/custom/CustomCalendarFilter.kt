package io.temco.guhada.view.custom

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import io.temco.guhada.R
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.databinding.LayoutAllCalendarBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * 날짜 필터 View
 * @param mListener CustomCalendarListener 구현 필수
 * @author Hyeyeon Park
 */
class CustomCalendarFilter : LinearLayout, View.OnClickListener {
    private lateinit var mBinding: LayoutAllCalendarBinding
    lateinit var mListener: CustomCalendarListener
    var startDate = ""
        set(value) {
            field = value
            mBinding.textDateFrom.text = field
            mBinding.executePendingBindings()
        }
    var endDate = ""
        set(value) {
            field = value
            mBinding.textDateTo.text = field
            mBinding.executePendingBindings()
        }

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_all_calendar, this, true)
        mBinding.clickListener = this
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.text_week -> {
                setPeriod(0)
                if (::mListener.isInitialized) mListener.onClickWeek()
            }
            R.id.text_month -> {
                setPeriod(1)
                if (::mListener.isInitialized) mListener.onClickMonth()
            }
            R.id.text_month_three -> {
                setPeriod(2)
                if (::mListener.isInitialized) mListener.onClickThreeMonth()
            }
            R.id.text_year -> {
                setPeriod(3)
                if (::mListener.isInitialized) mListener.onClickYear()
            }
            R.id.layout_date_from -> {
                showDatePicker(mBinding.textDateFrom)
                startDate = mBinding.textDateFrom.text.toString()
            }
            R.id.layout_date_to -> {
                showDatePicker(mBinding.textDateTo)
                endDate = mBinding.textDateTo.text.toString()
            }
            R.id.text_check -> {
                if (::mListener.isInitialized) checkDate { mListener.onClickCheck() }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun checkDate(task: () -> Unit) {
        if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
            val format = SimpleDateFormat("yyyy.MM.dd")
            val start = format.parse(startDate)
            val end = format.parse(endDate)
            if (start <= end) task()
            else ToastUtil.showMessage("종료 날짜는 시작날짜보다 늦어야 합니다.")
        } else {
            ToastUtil.showMessage("날짜를 선택해주세요.")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePicker(textView: TextView) {
        val today = Calendar.getInstance()
        val dataPicker = DatePickerDialog(context, R.style.CalendarDialogTheme, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            val monthStr: String = if (month < 9) "0${month + 1}" else "${month + 1}"
            val dayStr: String = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
            val dateStr = "$year.$monthStr.$dayStr"

            textView.text = dateStr
            if (textView.id == mBinding.textDateFrom.id) startDate = dateStr
            else endDate = dateStr

            // reset filters
            mBinding.textWeek.isSelected = false
            mBinding.textMonth.isSelected = false
            mBinding.textMonthThree.isSelected = false
            mBinding.textYear.isSelected = false

            mBinding.executePendingBindings()
        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH))
        dataPicker.show()
    }

    fun setPeriod(position: Int) {
        when (position) {
            0 -> {
                mBinding.textWeek.isSelected = true //
                mBinding.textMonth.isSelected = false
                mBinding.textMonthThree.isSelected = false
                mBinding.textYear.isSelected = false
            }
            1 -> {
                mBinding.textWeek.isSelected = false
                mBinding.textMonth.isSelected = true //
                mBinding.textMonthThree.isSelected = false
                mBinding.textYear.isSelected = false
            }
            2 -> {
                mBinding.textWeek.isSelected = false
                mBinding.textMonth.isSelected = false
                mBinding.textMonthThree.isSelected = true //
                mBinding.textYear.isSelected = false
            }
            3 -> {
                mBinding.textWeek.isSelected = false
                mBinding.textMonth.isSelected = false
                mBinding.textMonthThree.isSelected = false
                mBinding.textYear.isSelected = true //
            }
        }
    }

    interface CustomCalendarListener {
        fun onClickWeek()
        fun onClickMonth()
        fun onClickThreeMonth()
        fun onClickYear()
        fun onClickCheck()
    }
}