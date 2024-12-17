package com.anuragxone.innertube.core

data class SessionOptions(

//    val lang: String? = null,
//    val location: String? = null,
//    val accountIndex: Int? = null,
//    val onBehalfOfUser: String? = null,
//    val retrievePlayer: Boolean? = null,
//    val enableSafetyMode: Boolean? = null,
    val generateSessionLocally: Boolean? = null,
//    val enableSessionCache: Boolean? = null,
//    val deviceCategory: DeviceCategory? = null,
//    val clientType: ClientType? = null,
//    val timezone: String? = null,
    val cache: ICache? = null,
//    val cookie: String? = null,
    val visitorData: String? = null,
    val fetch: FetchFunction? = null,
    val poToken: String? = null

)
