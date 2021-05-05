package com.easv.tkm.recipeapp.data

enum class IntentValues(val code: Int) {

    //Requestcodes
    REQUEST_DETAIL(1),
    REQUESTCODE_IMAGE_APP(2),
    REQUESTCODE_IMAGE_DIRECT(3),
    REQUESTCODE_IMAGE_PICK(4),
    REQUESTCODE_CRUD(5),


    //Responsedetails
    RESPONSE_DETAIL_CREATE(1),
    RESPONSE_DETAIL_UPDATE(2),
    RESPONSE_DETAIL_DELETE(3),
}