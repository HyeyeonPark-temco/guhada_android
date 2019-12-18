package io.temco.guhada.common.util


import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.text.InputFilter
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.temco.guhada.common.listener.OnBaseDialogListener
import io.temco.guhada.view.custom.dialog.CustomMessageDialog
import androidx.core.content.ContextCompat.getSystemService


object CommonViewUtil {

    fun dimenToValue(ctx: Context, minusDimen: Int): Int {
        val matrix = DisplayMetrics()
        (ctx as Activity).windowManager.defaultDisplay.getMetrics(matrix)
        var px = convertDpToPixel(minusDimen, ctx)
        return (matrix.widthPixels - px)
    }

    @JvmStatic
    fun dipToPixel(context: Context, dip: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip.toFloat(), context.resources.displayMetrics).toInt()
    }

    fun convertDpToPixel(dp: Int, context: Context): Int {
        return (dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    }

    fun convertSpToPixel(sp: Float, context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources.displayMetrics).toInt()
    }

    fun convertPixelToDp(context: Context, px: Int): Int {
        val density = context.resources.displayMetrics.densityDpi.toFloat()
        val dp = (px / (density / DisplayMetrics.DENSITY_DEFAULT))
        return Math.ceil(dp.toDouble()).toInt()
    }

    fun pixelTodip(context: Context, px: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px.toFloat(), context.resources.displayMetrics)
                .toInt() /*(px / context.getResources().getDisplayMetrics().density);*/
    }

    fun setEditTextMaxLength(editText: EditText, length: Int) {
        val FilterArray = arrayOfNulls<InputFilter>(1)
        FilterArray[0] = InputFilter.LengthFilter(length)
        editText.filters = FilterArray
    }


    fun showKeyborad(v: View, context: Context) {
        if (v is EditText) {
            (v as EditText).isFocusable = true
            (v as EditText).requestFocus()
        }
        v.postDelayed(Runnable {
            val imgr = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            //imgr.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
            imgr.showSoftInput(v, 0)
        }, 300)
    }


    fun hideKeyborad(v: View, context: Context) {
        v.postDelayed(Runnable {
            /*val imgr = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imgr.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY)*/
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
        }, 300)
    }

    fun textViewAddBold(txt: TextView): TextView {
        txt.typeface = Typeface.DEFAULT_BOLD
        return txt
    }

    fun textViewRemoveBold(txt: TextView): TextView {
        txt.typeface = Typeface.DEFAULT
        return txt
    }

    fun getFindView(context: Context, view: View, name: String): View {
        return view.findViewById(context!!.resources.getIdentifier(name, "id", context.packageName))
    }


    /**
     * 텍스트가 여러줄일때 마지막 줄에 이미지를 붙이는 경우 사용
     */
    fun setTextViewImageTextEnd(context: Context, res: Int, text: String, textview: TextView) {
        val image = context.resources.getDrawable(res, null)
        image.setBounds(0, 0, image.intrinsicWidth, image.intrinsicHeight)
        var span = SpannableStringBuilder(text + "n")
        span.setSpan(ImageSpan(image), text.length, text.length + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textview.text = span
    }


    /***
     *
     * @param activity current Activity notNull
     * @param mas String type notNull
     * @param isCancelBtn Boolean data notNull
     * @param listener OnBaseDialogListener nullable
     */
    fun showDialog(activity: AppCompatActivity, mas: String, isCancelBtn: Boolean, listener: OnBaseDialogListener?) {
        CustomMessageDialog(message = mas, cancelButtonVisible = isCancelBtn,
                confirmTask = {
                    listener?.onClickOk()
                }
        ).show(manager = activity.supportFragmentManager, tag = activity::class.java.simpleName)
    }

    /***
     *
     * @param activity current Activity notNull
     * @param mas String type notNull
     * @param isCancelBtn Boolean data notNull
     * @param listener OnBaseDialogListener nullable
     */
    fun showDialog(activity: AppCompatActivity, mas: String, cancelListener: OnBaseDialogListener?, okListener: OnBaseDialogListener?) {
        CustomMessageDialog(message = mas,
                cancelTask = {
                    cancelListener?.onClickOk()
                },
                confirmTask = {
                    okListener?.onClickOk()
                }
        ).show(manager = activity.supportFragmentManager, tag = activity::class.java.simpleName)
    }

    /***
     *
     * @param activity current Activity notNull
     * @param mas String type notNull
     * @param isCancelBtn Boolean data notNull
     * @param listener OnBaseDialogListener nullable
     */
    fun showDialog(activity: AppCompatActivity, mas: String, cancelListener: OnBaseDialogListener?, okListener: OnBaseDialogListener?, confirmBtnName: String) {
        CustomMessageDialog(message = mas,
                cancelTask = {
                    cancelListener?.onClickOk()
                },
                confirmTask = {
                    okListener?.onClickOk()
                },
                confirmBtnName = confirmBtnName
        ).show(manager = activity.supportFragmentManager, tag = activity::class.java.simpleName)
    }


    /***
     *
     * @param activity current Activity notNull
     * @param mas String type notNull
     * @param isCancelBtn Boolean data notNull
     * @param isFinish OnBaseDialogListener nullable
     */
    fun showDialog(activity: AppCompatActivity, mas: String, isCancelBtn: Boolean, isFinish: Boolean) {
        CustomMessageDialog(message = mas, cancelButtonVisible = isCancelBtn,
                confirmTask = {
                    if (isFinish) activity.finish()
                }
        ).show(manager = activity.supportFragmentManager, tag = activity::class.java.simpleName)
    }

}