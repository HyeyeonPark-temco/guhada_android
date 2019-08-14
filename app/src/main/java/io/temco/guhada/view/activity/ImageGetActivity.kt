package io.temco.guhada.view.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.databinding.ActivityImagegetBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.custom.dialog.CustomMessageDialog
import java.io.File


/**
 * @author park jungho
 * 19.08.05
 * Side Menu Activity
 *
 */
class ImageGetActivity : BindActivity<ActivityImagegetBinding>() {

    val PERMISSIONS_REQUEST_CODE = 100
    val PICK_FROM_ALBUM = 130

    val REQUIRED_PERMISSIONS = arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun getBaseTag(): String = ImageGetActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_imageget
    override fun getViewType(): Type.View = Type.View.IMAGE_GET

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun init() {
        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            galleryAddPic()
        }else{
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun galleryAddPic() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_FROM_ALBUM)
    }



    ////////////////////////////////////////////////////////////////////////////////
    // override activity

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(CustomLog.flag)CustomLog.L("ImageGetActivity","onActivityResult",resultCode,requestCode)
        if (requestCode == PICK_FROM_ALBUM) {
            var photoUri  = data!!.getData()
            var cursor : Cursor?  = null
            try {
                var proj = arrayOf(MediaStore.Images.Media.DATA)
                cursor = getContentResolver().query(photoUri, proj, null, null, null)
                var column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                var tempFile = File(cursor.getString(column_index))
                if(CustomLog.flag)CustomLog.L("ImageGetActivity","onActivityResult tempFile",tempFile.absolutePath)
                data?.apply {
                    var intent = Intent()
                    intent.putExtra("file_name",tempFile.absolutePath)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            } finally {
                if (cursor != null) {
                    cursor.close()
                }
            }
        }
    }

    private fun checkPermission(permission : String) : Boolean{
        if (Build.VERSION.SDK_INT >= 23) {
            when (ContextCompat.checkSelfPermission(this@ImageGetActivity, permission)) {
                PackageManager.PERMISSION_GRANTED -> return true
                else -> return false
            }
        }
        return false
    }

    private fun requestPermission(permission: String){
       /* if (ActivityCompat.shouldShowRequestPermissionRationale(this@ImageGetActivity, permission)){
            ToastUtil.showMessage("사진을 추가하기 위해서는 권한이 필요합니다.")
        }*/
        ActivityCompat.requestPermissions(this@ImageGetActivity, arrayOf(permission),PERMISSIONS_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        try {
            if(requestCode == PERMISSIONS_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                galleryAddPic()
            }else if(requestCode == PERMISSIONS_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_DENIED){
                CustomMessageDialog(message = "파일 읽기 권한이 거부되었습니다.\n설정(앱정보) 에서 권한을 허용해주세요.",
                        cancelButtonVisible = false,
                        confirmTask = {
                            if(CustomLog.flag)CustomLog.L("ImageGetActivity","onRequestPermissionsResult confirmTask")
                            this@ImageGetActivity.finish()
                        }).show(manager = this.supportFragmentManager, tag = "ImageGetActivity")
            }
        }catch (e:Exception){
            if(CustomLog.flag)CustomLog.E(e)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
}