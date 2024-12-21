package com.anuragxone.innertube.core

import com.anuragxone.innertube.utils.Constants
import com.anuragxone.innertube.utils.DeviceCategory

enum class ClientType(val value: String) {
    WEB("WEB"),
    MWEB("MWEB"),
    KIDS("WEB_KIDS"),
    MUSIC("WEB_REMIX"),
    IOS("iOS"),
    ANDROID("ANDROID"),
    ANDROID_MUSIC("ANDROID_MUSIC"),
    ANDROID_CREATOR("ANDROID_CREATOR"),
    TV("TVHTML5"),
    TV_EMBEDDED("TVHTML5_SIMPLY_EMBEDDED_PLAYER"),
    WEB_EMBEDDED("WEB_EMBEDDED_PLAYER"),
    WEB_CREATOR("WEB_CREATOR")

//    companion object {
//        fun fromString(value: String): ClientType? {
//            return values().find { it.value == value }
//        }
//    }
}

data class Context(
    val client: Client,
    val user: User,
    val thirdParty: ThirdParty? = null,
    val request: Request? = null
) {
    data class Client(
        val hl: String,
        val gl: String,
        val remoteHost: String? = null,
        val screenDensityFloat: Float? = null,
        val screenHeightPoints: Int? = null,
        val screenPixelDensity: Float? = null,
        val screenWidthPoints: Int? = null,
        val visitorData: String? = null,
        val clientName: String,
        val clientVersion: String,
        val clientScreen: String? = null,
        val androidSdkVersion: Int? = null,
        val osName: String,
        val osVersion: String,
        val platform: String,
        val clientFormFactor: String,
        val userInterfaceTheme: String? = null,
        val timeZone: String,
        val userAgent: String? = null,
        val browserName: String? = null,
        val browserVersion: String? = null,
        val originalUrl: String? = null,
        val deviceMake: String,
        val deviceModel: String,
        val utcOffsetMinutes: Int,
        val mainAppWebInfo: MainAppWebInfo? = null,
        val memoryTotalKbytes: String? = null,
        val configInfo: ConfigInfo? = null,
        val kidsAppInfo: KidsAppInfo? = null
    ) {
        data class MainAppWebInfo(
            val graftUrl: String = "one",
            val pwaInstallabilityStatus: String,
            val webDisplayMode: String,
            val isWebNativeShareAvailable: Boolean
        )

        data class ConfigInfo(
            val appInstallData: String
        )

        data class KidsAppInfo(
            val categorySettings: CategorySettings,
            val contentSettings: ContentSettings
        ) {
            data class CategorySettings(
                val enabledCategories: List<String>
            )

            data class ContentSettings(
                val corpusPreference: String,
                val kidsNoSearchMode: String
            )
        }
    }

    data class User(
        val enableSafetyMode: Boolean,
        val lockedSafetyMode: Boolean,
        val onBehalfOfUser: String? = null
    )

    data class ThirdParty(
        val embedUrl: String
    )

    data class Request(
        val useSsl: Boolean,
        val internalExperimentFlags: List<Any>
    )
}

data class ContextData(
    val hl: String,
    val gl: String,
    val remoteHost: String? = null,
    val visitorData: String,
    val clientName: String,
    val clientVersion: String,
    val osName: String,
    val osVersion: String,
    val deviceCategory: String,
    val timeZone: String,
    val enableSafetyMode: Boolean,
    val browserName: String? = null,
    val browserVersion: String? = null,
    val appInstallData: String? = null,
    val deviceMake: String,
    val deviceModel: String,
    val onBehalfOfUser: String? = null
)


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

data class SessionData(
    val context: Context,
    val apiKey: String,
    val apiVersion: String
)

data class SWSessionData(
    val contextData: ContextData,
    val apiKey: String,
    val apiVersion: String
)

data class SessionArgs(
    val lang: String,
    val location: String,
    val timeZone: String,
    val deviceCategory: DeviceCategory,
    val clientName: ClientType,
    val enableSafetyMode: Boolean,
    val visitorData: String,
    val onBehalfOfUser: String?
)

const val TAG = "Session"



class Session(
    val context: Context,
    val apiKey: String,
    val apiVersion: String,
    val accountIndex: Int,
    var player: Player? = null,
    cookie: String? = null,
    fetch: FetchFunction? = null,
    val cache: ICache? = null,
    val poToken: String? = null
) {

    val http: HTTPClient = HTTPClient(this, cookie, fetch)
    val actions: Actions = Actions(this)
    val oauth: OAuth2 = OAuth2(this)
    val loggedIn: Boolean = cookie != null
    val key: String = apiKey

    companion object {

        suspend fun create(options: SessionOptions = SessionOptions()): Session {
            val sessionData = getSessionData(
//                lang = options.lang,
//                location = options.location,
//                accountIndex = options.accountIndex,
                visitorData = options.visitorData,
//                enableSafetyMode = options.enableSafetyMode,
                generateSessionLocally = options.generateSessionLocally,
//                deviceCategory = options.deviceCategory,
//                clientType = options.clientType,
//                timezone = options.timezone,
                fetch = options.fetch,
//                onBehalfOfUser = options.onBehalfOfUser,
                cache = options.cache,
//                enableSessionCache = options.enableSessionCache,
                poToken = options.poToken
            )

            val (context, apiKey, apiVersion, accountIndex) = sessionData

            val player = if (options.retrievePlayer == false) {
                null
            } else {
                Player.create(options.cache, options.fetch, options.poToken)
            }

            return Session(
                context = context,
                apiKey = apiKey,
                apiVersion = apiVersion,
                accountIndex = accountIndex,
                player = player,
                cookie = options.cookie,
                fetch = options.fetch,
                cache = options.cache,
                poToken = options.poToken
            )
        }

        private suspend fun getSessionData(
            lang: String = "",
            location: String = "",
            accountIndex: Int = 0,
            visitorData: String? = "",
            enableSafetyMode: Boolean = false,
            generateSessionLocally: Boolean? = false,
            deviceCategory: DeviceCategory = DeviceCategory.DESKTOP,
            clientName: ClientType = ClientType.WEB,
            timezone: String = java.util.TimeZone.getDefault().id,
//            fetch: FetchFunction = Platform.shim.fetch,
            onBehalfOfUser: String? = null,
            cache: ICache? = null,
            enableSessionCache: Boolean = true,
            poToken: String? = null
        ): SessionDataWithAccountIndex {
            val sessionArgs = mapOf(
                "lang" to lang,
                "location" to location,
                "time_zone" to timezone,
                "device_category" to deviceCategory,
                "client_name" to clientName,
                "enable_safety_mode" to enableSafetyMode,
                "visitor_data" to visitorData,
                "on_behalf_of_user" to onBehalfOfUser,
                "po_token" to poToken
            )

            var sessionData: SessionData? = null

            if (cache != null && enableSessionCache) {
                val cachedSessionData = fromCache(cache, sessionArgs)
                if (cachedSessionData != null) {
                    Log.info(TAG, "Found session data in cache.")
                    sessionData = cachedSessionData
                }
            }

            if (sessionData == null) {
                Log.info(TAG, "Generating session data.")

                var apiKey = Constants.CLIENTS.WEB.API_KEY
                var apiVersion = Constants.CLIENTS.WEB.API_VERSION

                var contextData = ContextData(
                    hl = lang.ifEmpty { "en" },
                    gl = location.ifEmpty { "US" },
                    remoteHost = "",
                    visitorData = visitorData.ifEmpty {
                        ProtoUtils.encodeVisitorData(
                            generateRandomString(11),
                            System.currentTimeMillis() / 1000
                        )
                    },
                    clientName = clientName,
                    clientVersion = Constants.CLIENTS.WEB.VERSION,
                    deviceCategory = deviceCategory.name.uppercase(),
                    osName = "Windows",
                    osVersion = "10.0",
                    timeZone = timezone,
                    browserName = "Chrome",
                    browserVersion = "125.0.0.0",
                    deviceMake = "",
                    deviceModel = "",
                    enableSafetyMode = enableSafetyMode
                )

                if (!generateSessionLocally!!) {
                    try {
                        val swSessionData = getSessionDataFromServer(sessionArgs, fetch)
                        apiKey = swSessionData.apiKey
                        apiVersion = swSessionData.apiVersion
                        contextData = swSessionData.contextData
                    } catch (e: Exception) {
                        Log.error(
                            TAG,
                            "Failed to retrieve session data from server. Session data generated locally will be used instead.",
                            e
                        )
                    }
                }

                onBehalfOfUser?.let {
                    contextData = contextData.copy(onBehalfOfUser = it)
                }

                sessionData = SessionData(
                    apiKey = apiKey,
                    apiVersion = apiVersion,
                    context = buildContext(contextData)
                )

                if (enableSessionCache) storeSession(sessionData, cache)
            }

            Log.debug(TAG, "Session data: $sessionData")

            return SessionDataWithAccountIndex(sessionData, accountIndex)
        }

    }



}

