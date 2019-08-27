package io.temco.guhada.data.model

class ReportResponse {

    var content = ""
    var imageUrls : ArrayList<String> = arrayListOf()
    var reportTarget = ""
    //var reportType = ReportTypeData()
    var reportType = ""
    var reporter = 0L
    var targetId = 0L
    var title = ""

    override fun toString(): String {
        return "ReportResponse(content='$content', imageUrls=$imageUrls, reportTarget='$reportTarget', reportType=$reportType, reporter=$reporter, targetId=$targetId, title='$title')"
    }

}