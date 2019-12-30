#include <jni.h>

JNIEXPORT jstring JNICALL

Java_io_temco_guhada_data_retrofit_manager_RetrofitManager_getAuthKey(JNIEnv *env, jobject instance) {
    return (*env)->  NewStringUTF(env, "WjNWb1lXUmhPbWQxYUdGa1lXTnNhV1Z1ZEhObFkzSmxkQT09");
}
