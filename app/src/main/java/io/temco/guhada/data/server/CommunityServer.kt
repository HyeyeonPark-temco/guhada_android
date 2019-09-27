package io.temco.guhada.data.server

import com.google.gson.Gson
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.*
import io.temco.guhada.data.model.base.BaseErrorModel
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.base.Message
import io.temco.guhada.data.model.community.*
import io.temco.guhada.data.retrofit.manager.RetrofitManager
import io.temco.guhada.data.retrofit.service.CommunityService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CommunityServer {

    companion object {


        @JvmStatic
        fun <C , R>resultListener(listener: OnServerListener, call: Call<C>, response: Response<R>){
            if (response.code() in 200..400 && response.body() != null) {
                listener.onResult(true, response.body())
            } else {
                try {
                    var msg = Message()
                    var errorBody: String? = response.errorBody()?.string() ?: null
                    if (!errorBody.isNullOrEmpty()) {
                        var gson = Gson()
                        msg = gson.fromJson<Message>(errorBody, Message::class.java)
                    }
                    var error = BaseErrorModel(response.code(), response.raw().request().url().toString(), msg)
                    if (CustomLog.flag) CustomLog.L("saveReport", "onResponse body", error.toString())
                    listener.onResult(false, error)
                } catch (e: Exception) {
                    if (CustomLog.flag) CustomLog.E(e)
                    listener.onResult(false, null)
                }
            }
        }


        /**
         * 게시글 상세 정보 가져오기
         *
         * bbsId : 게시글 id
         * userIp : 유져 ip
         */
        @JvmStatic
        fun getCommunityAll(listener: OnServerListener) {
            RetrofitManager.createService(Type.Server.BBS, CommunityService::class.java, true)
                    .getCommunityAll().enqueue(object : Callback<BaseModel<CommunityCategory>> {
                        override fun onResponse(call: Call<BaseModel<CommunityCategory>>, response: Response<BaseModel<CommunityCategory>>) {
                            if(response.code() in 200..400 && response.body() != null){
                                listener.onResult(true, response.body())
                            }else{
                                try{
                                    var msg  = Message()
                                    var errorBody : String? = response.errorBody()?.string() ?: null
                                    if(!errorBody.isNullOrEmpty()){
                                        var gson = Gson()
                                        msg = gson.fromJson<Message>(errorBody, Message::class.java)
                                    }
                                    var error = BaseErrorModel(response.code(),response.raw().request().url().toString(),msg)
                                    listener.onResult(false, error)
                                }catch (e : Exception){
                                    if(CustomLog.flag) CustomLog.E(e)
                                    listener.onResult(false, null)
                                }
                            }
                        }
                        override fun onFailure(call: Call<BaseModel<CommunityCategory>>, t: Throwable) {
                            listener.onResult(false, t.message)
                        }
                    }
                    )
        }

        /**
         * 게시글 상세 정보 가져오기
         *
         * bbsId : 게시글 id
         * userIp : 유져 ip
         */
        @JvmStatic
        fun getCommunityCategoryAll(listener: OnServerListener, communityId: Int) {
            RetrofitManager.createService(Type.Server.BBS, CommunityService::class.java, true)
                    .getCommunityCategoryAll(communityId).enqueue(object : Callback<BaseModel<CommunityCategorySub>> {
                        override fun onResponse(call: Call<BaseModel<CommunityCategorySub>>, response: Response<BaseModel<CommunityCategorySub>>) {
                            if(response.code() in 200..400 && response.body() != null){
                                listener.onResult(true, response.body())
                            }else{
                                try{
                                    var msg  = Message()
                                    var errorBody : String? = response.errorBody()?.string() ?: null
                                    if(!errorBody.isNullOrEmpty()){
                                        var gson = Gson()
                                        msg = gson.fromJson<Message>(errorBody, Message::class.java)
                                    }
                                    var error = BaseErrorModel(response.code(),response.raw().request().url().toString(),msg)
                                    listener.onResult(false, error)
                                }catch (e : Exception){
                                    if(CustomLog.flag) CustomLog.E(e)
                                    listener.onResult(false, null)
                                }
                            }
                        }
                        override fun onFailure(call: Call<BaseModel<CommunityCategorySub>>, t: Throwable) {
                            listener.onResult(false, t.message)
                        }
                    }
                    )
        }

        /**
         * 게시글 상세 정보 가져오기
         *
         * bbsId : 게시글 id
         * userIp : 유져 ip
         */
        @JvmStatic
        fun getCategoryFilter(listener: OnServerListener, communityCategoryId: Int) {
            RetrofitManager.createService(Type.Server.BBS, CommunityService::class.java, true)
                    .getCategoryFilter(communityCategoryId).enqueue(object : Callback<BaseModel<CommunityCategoryfilter>> {
                        override fun onResponse(call: Call<BaseModel<CommunityCategoryfilter>>, response: Response<BaseModel<CommunityCategoryfilter>>) {
                            if(response.code() in 200..400 && response.body() != null){
                                listener.onResult(true, response.body())
                            }else{
                                try{
                                    var msg  = Message()
                                    var errorBody : String? = response.errorBody()?.string() ?: null
                                    if(!errorBody.isNullOrEmpty()){
                                        var gson = Gson()
                                        msg = gson.fromJson<Message>(errorBody, Message::class.java)
                                    }
                                    var error = BaseErrorModel(response.code(),response.raw().request().url().toString(),msg)
                                    listener.onResult(false, error)
                                }catch (e : Exception){
                                    if(CustomLog.flag) CustomLog.E(e)
                                    listener.onResult(false, null)
                                }
                            }
                        }
                        override fun onFailure(call: Call<BaseModel<CommunityCategoryfilter>>, t: Throwable) {
                            listener.onResult(false, t.message)
                        }
                    }
                    )
        }

        /**
         * 게시글 상세 정보 가져오기
         *
         * bbsId : 게시글 id
         * userIp : 유져 ip
         */
        @JvmStatic
        fun getBbsDetail(listener: OnServerListener, bbsId : Long, userIp: String) {
            RetrofitManager.createService(Type.Server.BBS, CommunityService::class.java, true)
                    .getBbsDetail(bbsId, userIp).enqueue(object : Callback<BaseModel<CommunityDetail>> {
                        override fun onResponse(call: Call<BaseModel<CommunityDetail>>, response: Response<BaseModel<CommunityDetail>>) {
                            if(response.code() in 200..400 && response.body() != null){
                                listener.onResult(true, response.body())
                            }else{
                                try{
                                    var msg  = Message()
                                    var errorBody : String? = response.errorBody()?.string() ?: null
                                    if(!errorBody.isNullOrEmpty()){
                                        var gson = Gson()
                                        msg = gson.fromJson<Message>(errorBody, Message::class.java)
                                    }
                                    var error = BaseErrorModel(response.code(),response.raw().request().url().toString(),msg)
                                    listener.onResult(false, error)
                                }catch (e : Exception){
                                    if(CustomLog.flag) CustomLog.E(e)
                                    listener.onResult(false, null)
                                }
                            }
                        }
                        override fun onFailure(call: Call<BaseModel<CommunityDetail>>, t: Throwable) {
                            listener.onResult(false, t.message)
                        }
                    }
            )
        }


        /**
         * 게시글 상세의 댓글 리스트
         *
         * bbsId : 게시글 id
         *
         */
        @JvmStatic
        fun getCommentList(listener: OnServerListener, bbsId : Long, page: Int, orderType : String, unitPerPage : Int) {
            RetrofitManager.createService(Type.Server.BBS, CommunityService::class.java, true)
                    .getCommentList(bbsId, page, orderType, unitPerPage).enqueue(object : Callback<BaseModel<CommentContent>> {
                        override fun onResponse(call: Call<BaseModel<CommentContent>>, response: Response<BaseModel<CommentContent>>) {
                            if(response.code() in 200..400 && response.body() != null){
                                listener.onResult(true, response.body())
                            }else{
                                try{
                                    var msg  = Message()
                                    var errorBody : String? = response.errorBody()?.string() ?: null
                                    if(!errorBody.isNullOrEmpty()){
                                        var gson = Gson()
                                        msg = gson.fromJson<Message>(errorBody, Message::class.java)
                                    }
                                    var error = BaseErrorModel(response.code(),response.raw().request().url().toString(),msg)
                                    listener.onResult(false, error)
                                }catch (e : Exception){
                                    if(CustomLog.flag) CustomLog.E(e)
                                    listener.onResult(false, null)
                                }
                            }
                        }
                        override fun onFailure(call: Call<BaseModel<CommentContent>>, t: Throwable) {
                            listener.onResult(false, t.message)
                        }
                    }
            )
        }

        /**
         * 댓글 가져오기
         *
         * id : 댓글 id
         */
        @JvmStatic
        fun getCommentIdData(listener: OnServerListener, id : Long) {
            RetrofitManager.createService(Type.Server.BBS, CommunityService::class.java, true)
                    .getCommentId(id).enqueue(object : Callback<BaseModel<Comments>> {
                        override fun onResponse(call: Call<BaseModel<Comments>>, response: Response<BaseModel<Comments>>) {
                            if(response.code() in 200..400 && response.body() != null){
                                listener.onResult(true, response.body())
                            }else{
                                try{
                                    var msg  = Message()
                                    var errorBody : String? = response.errorBody()?.string() ?: null
                                    if(!errorBody.isNullOrEmpty()){
                                        var gson = Gson()
                                        msg = gson.fromJson<Message>(errorBody, Message::class.java)
                                    }
                                    var error = BaseErrorModel(response.code(),response.raw().request().url().toString(),msg)
                                    listener.onResult(false, error)
                                }catch (e : Exception){
                                    if(CustomLog.flag) CustomLog.E(e)
                                    listener.onResult(false, null)
                                }
                            }
                        }
                        override fun onFailure(call: Call<BaseModel<Comments>>, t: Throwable) {
                            listener.onResult(false, t.message)
                        }
                    }
            )
        }


        /**
         * 댓글 삭제
         *
         * id : 댓글 id
         */
        @JvmStatic
        fun deleteComment(listener: OnServerListener, accessToken: String, id : Long) {
            RetrofitManager.createService(Type.Server.BBS, CommunityService::class.java, true)
                    .deleteCommentId(accessToken, id).enqueue(object : Callback<BaseModel<Any>> {
                        override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                            if(response.code() in 200..400 && response.body() != null){
                                listener.onResult(true, response.body())
                            }else{
                                try{
                                    var msg  = Message()
                                    var errorBody : String? = response.errorBody()?.string() ?: null
                                    if(!errorBody.isNullOrEmpty()){
                                        var gson = Gson()
                                        msg = gson.fromJson<Message>(errorBody, Message::class.java)
                                    }
                                    var error = BaseErrorModel(response.code(),response.raw().request().url().toString(),msg)
                                    listener.onResult(false, error)
                                }catch (e : Exception){
                                    if(CustomLog.flag) CustomLog.E(e)
                                    listener.onResult(false, null)
                                }
                            }
                        }
                        override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                            listener.onResult(false, t.message)
                        }
                    }
            )
        }


        /**
         * 게시글 댓글 수정
         *
         * bbsId : 게시글 id
         * userIp : 유져 ip
         */
        @JvmStatic
        fun modifyCommentData(listener: OnServerListener, accessToken: String, id : Long, body : CommentResponse) {
            RetrofitManager.createService(Type.Server.BBS, CommunityService::class.java, true)
                    .modifyCommentId(accessToken, id, body).enqueue(object : Callback<BaseModel<Any>> {
                        override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                            if(response.code() in 200..400 && response.body() != null){
                                listener.onResult(true, response.body())
                            }else{
                                try{
                                    var msg  = Message()
                                    var errorBody : String? = response.errorBody()?.string() ?: null
                                    if(!errorBody.isNullOrEmpty()){
                                        var gson = Gson()
                                        msg = gson.fromJson<Message>(errorBody, Message::class.java)
                                    }
                                    var error = BaseErrorModel(response.code(),response.raw().request().url().toString(),msg)
                                    listener.onResult(false, error)
                                }catch (e : Exception){
                                    if(CustomLog.flag) CustomLog.E(e)
                                    listener.onResult(false, null)
                                }
                            }
                        }
                        override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                            listener.onResult(false, t.message)
                        }
                    }
            )
        }




        /**
         * 게시글 댓글 등록
         *
         * bbsId : 게시글 id
         * userIp : 유져 ip
         */
        @JvmStatic
        fun postCommentData(listener: OnServerListener, accessToken: String, body : CommentResponse) {
            RetrofitManager.createService(Type.Server.BBS, CommunityService::class.java, true)
                    .postCommentData(accessToken, body).enqueue(object : Callback<BaseModel<Any>> {
                        override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                            if(response.code() in 200..400 && response.body() != null){
                                listener.onResult(true, response.body())
                            }else{
                                try{
                                    var msg  = Message()
                                    var errorBody : String? = response.errorBody()?.string() ?: null
                                    if(!errorBody.isNullOrEmpty()){
                                        var gson = Gson()
                                        msg = gson.fromJson<Message>(errorBody, Message::class.java)
                                    }
                                    var error = BaseErrorModel(response.code(),response.raw().request().url().toString(),msg)
                                    listener.onResult(false, error)
                                }catch (e : Exception){
                                    if(CustomLog.flag) CustomLog.E(e)
                                    listener.onResult(false, null)
                                }
                            }
                        }
                        override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                            listener.onResult(false, t.message)
                        }
                    }
                    )
        }





        /**
         * 게시글  등록
         *
         * bbsId : 게시글 id
         * userIp : 유져 ip
         */
        @JvmStatic
        fun postBbsData(listener: OnServerListener, accessToken: String, body : CreateBbsResponse) {
            RetrofitManager.createService(Type.Server.BBS, CommunityService::class.java, true)
                    .postBbsData(accessToken, body).enqueue(object : Callback<BaseModel<Any>> {
                        override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                            if(response.code() in 200..400 && response.body() != null){
                                listener.onResult(true, response.body())
                            }else{
                                try{
                                    var msg  = Message()
                                    var errorBody : String? = response.errorBody()?.string() ?: null
                                    if(!errorBody.isNullOrEmpty()){
                                        var gson = Gson()
                                        msg = gson.fromJson<Message>(errorBody, Message::class.java)
                                    }
                                    var error = BaseErrorModel(response.code(),response.raw().request().url().toString(),msg)
                                    listener.onResult(false, error)
                                }catch (e : Exception){
                                    if(CustomLog.flag) CustomLog.E(e)
                                    listener.onResult(false, null)
                                }
                            }
                        }
                        override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                            listener.onResult(false, t.message)
                        }
                    }
                    )
        }

        /**
         * 게시글 수정
         *
         * bbsId : 게시글 id
         * userIp : 유져 ip
         */
        @JvmStatic
        fun modifyBbsData(listener: OnServerListener, accessToken: String, id : Long, body : CreateBbsResponse) {
            RetrofitManager.createService(Type.Server.BBS, CommunityService::class.java, true)
                    .modifyBbsData(accessToken, id, body).enqueue(object : Callback<BaseModel<Any>> {
                        override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                            if(response.code() in 200..400 && response.body() != null){
                                listener.onResult(true, response.body())
                            }else{
                                try{
                                    var msg  = Message()
                                    var errorBody : String? = response.errorBody()?.string() ?: null
                                    if(!errorBody.isNullOrEmpty()){
                                        var gson = Gson()
                                        msg = gson.fromJson<Message>(errorBody, Message::class.java)
                                    }
                                    var error = BaseErrorModel(response.code(),response.raw().request().url().toString(),msg)
                                    listener.onResult(false, error)
                                }catch (e : Exception){
                                    if(CustomLog.flag) CustomLog.E(e)
                                    listener.onResult(false, null)
                                }
                            }
                        }
                        override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                            listener.onResult(false, t.message)
                        }
                    }
                    )
        }



        /**
         * 게시글 삭제
         *
         * id : 댓글 id
         */
        @JvmStatic
        fun deleteBbsData(listener: OnServerListener, accessToken: String, id : Long) {
            RetrofitManager.createService(Type.Server.BBS, CommunityService::class.java, true)
                    .deleteBbsData(accessToken, id).enqueue(object : Callback<BaseModel<Any>> {
                        override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                            if(response.code() in 200..400 && response.body() != null){
                                listener.onResult(true, response.body())
                            }else{
                                try{
                                    var msg  = Message()
                                    var errorBody : String? = response.errorBody()?.string() ?: null
                                    if(!errorBody.isNullOrEmpty()){
                                        var gson = Gson()
                                        msg = gson.fromJson<Message>(errorBody, Message::class.java)
                                    }
                                    var error = BaseErrorModel(response.code(),response.raw().request().url().toString(),msg)
                                    listener.onResult(false, error)
                                }catch (e : Exception){
                                    if(CustomLog.flag) CustomLog.E(e)
                                    listener.onResult(false, null)
                                }
                            }
                        }
                        override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                            listener.onResult(false, t.message)
                        }
                    }
            )
        }



        /**
         * 게시글 임시 등록
         *
         * bbsId : 게시글 id
         * userIp : 유져 ip
         */
        @JvmStatic
        fun postBbsTempData(listener: OnServerListener, accessToken: String, body : CreateBbsTempResponse) {
            RetrofitManager.createService(Type.Server.BBS, CommunityService::class.java, true)
                    .postBbsTempData(accessToken, body).enqueue(object : Callback<BaseModel<Any>> {
                        override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                            if(response.code() in 200..400 && response.body() != null){
                                listener.onResult(true, response.body())
                            }else{
                                try{
                                    var msg  = Message()
                                    var errorBody : String? = response.errorBody()?.string() ?: null
                                    if(!errorBody.isNullOrEmpty()){
                                        var gson = Gson()
                                        msg = gson.fromJson<Message>(errorBody, Message::class.java)
                                    }
                                    var error = BaseErrorModel(response.code(),response.raw().request().url().toString(),msg)
                                    listener.onResult(false, error)
                                }catch (e : Exception){
                                    if(CustomLog.flag) CustomLog.E(e)
                                    listener.onResult(false, null)
                                }
                            }
                        }
                        override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                            listener.onResult(false, t.message)
                        }
                    }
                    )
        }

        /**
         * 게시글 임시 수정
         *
         * bbsId : 게시글 id
         * userIp : 유져 ip
         */
        @JvmStatic
        fun modifyBbsTempData(listener: OnServerListener, accessToken: String, id : Long, body : CreateBbsTempResponse) {
            RetrofitManager.createService(Type.Server.BBS, CommunityService::class.java, true)
                    .modifyBbsTempData(accessToken, id, body).enqueue(object : Callback<BaseModel<Any>> {
                        override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                            if(response.code() in 200..400 && response.body() != null){
                                listener.onResult(true, response.body())
                            }else{
                                try{
                                    var msg  = Message()
                                    var errorBody : String? = response.errorBody()?.string() ?: null
                                    if(!errorBody.isNullOrEmpty()){
                                        var gson = Gson()
                                        msg = gson.fromJson<Message>(errorBody, Message::class.java)
                                    }
                                    var error = BaseErrorModel(response.code(),response.raw().request().url().toString(),msg)
                                    listener.onResult(false, error)
                                }catch (e : Exception){
                                    if(CustomLog.flag) CustomLog.E(e)
                                    listener.onResult(false, null)
                                }
                            }
                        }
                        override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                            listener.onResult(false, t.message)
                        }
                    }
            )
        }



        /**
         * 게시글 임시 삭제
         *
         * id : 댓글 id
         */
        @JvmStatic
        fun deleteBbsTempData(listener: OnServerListener, accessToken: String, id : Long) {
            RetrofitManager.createService(Type.Server.BBS, CommunityService::class.java, true)
                    .deleteBbsTempData(accessToken, id).enqueue(object : Callback<BaseModel<Any>> {
                        override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                            if(response.code() in 200..400 && response.body() != null){
                                listener.onResult(true, response.body())
                            }else{
                                try{
                                    var msg  = Message()
                                    var errorBody : String? = response.errorBody()?.string() ?: null
                                    if(!errorBody.isNullOrEmpty()){
                                        var gson = Gson()
                                        msg = gson.fromJson<Message>(errorBody, Message::class.java)
                                    }
                                    var error = BaseErrorModel(response.code(),response.raw().request().url().toString(),msg)
                                    listener.onResult(false, error)
                                }catch (e : Exception){
                                    if(CustomLog.flag) CustomLog.E(e)
                                    listener.onResult(false, null)
                                }
                            }
                        }
                        override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                            listener.onResult(false, t.message)
                        }
                    }
                    )
        }


        /**
         * 게시글 임시 삭제
         *
         * id : 댓글 id
         */
        @JvmStatic
        fun getBbsTempListData(listener: OnServerListener, accessToken: String) {
            RetrofitManager.createService(Type.Server.BBS, CommunityService::class.java, true)
                    .getBbsTempListData(accessToken).enqueue(object : Callback<BaseModel<CommunityTempInfo>> {
                        override fun onResponse(call: Call<BaseModel<CommunityTempInfo>>, response: Response<BaseModel<CommunityTempInfo>>) {
                            if(response.code() in 200..400 && response.body() != null){
                                listener.onResult(true, response.body())
                            }else{
                                try{
                                    var msg  = Message()
                                    var errorBody : String? = response.errorBody()?.string() ?: null
                                    if(!errorBody.isNullOrEmpty()){
                                        var gson = Gson()
                                        msg = gson.fromJson<Message>(errorBody, Message::class.java)
                                    }
                                    var error = BaseErrorModel(response.code(),response.raw().request().url().toString(),msg)
                                    listener.onResult(false, error)
                                }catch (e : Exception){
                                    if(CustomLog.flag) CustomLog.E(e)
                                    listener.onResult(false, null)
                                }
                            }
                        }
                        override fun onFailure(call: Call<BaseModel<CommunityTempInfo>>, t: Throwable) {
                            listener.onResult(false, t.message)
                        }
                    }
                    )
        }


        /**
         * 게시글 임시 삭제
         *
         * id : 댓글 id
         */
        @JvmStatic
        fun getBbsTempData(listener: OnServerListener, accessToken: String, id : Long) {
            RetrofitManager.createService(Type.Server.BBS, CommunityService::class.java, true)
                    .getBbsTempData(accessToken, id).enqueue(object : Callback<BaseModel<CommunityTempInfo>> {
                        override fun onResponse(call: Call<BaseModel<CommunityTempInfo>>, response: Response<BaseModel<CommunityTempInfo>>) {
                            if(response.code() in 200..400 && response.body() != null){
                                listener.onResult(true, response.body())
                            }else{
                                try{
                                    var msg  = Message()
                                    var errorBody : String? = response.errorBody()?.string() ?: null
                                    if(!errorBody.isNullOrEmpty()){
                                        var gson = Gson()
                                        msg = gson.fromJson<Message>(errorBody, Message::class.java)
                                    }
                                    var error = BaseErrorModel(response.code(),response.raw().request().url().toString(),msg)
                                    listener.onResult(false, error)
                                }catch (e : Exception){
                                    if(CustomLog.flag) CustomLog.E(e)
                                    listener.onResult(false, null)
                                }
                            }
                        }
                        override fun onFailure(call: Call<BaseModel<CommunityTempInfo>>, t: Throwable) {
                            listener.onResult(false, t.message)
                        }
                    }
                    )
        }


    }


}