package com.anuragxone.innertube.core


enum class ClientType(val type: String) {
    WEB("WEB"),
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
            val graftUrl: String,
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
    /**
     * Language.
     */
    val lang: String? = null,

    /**
     * Geolocation.
     */
    val location: String? = null,

    /**
     * The account index to use. This is useful if you have multiple accounts logged in.
     *
     * NOTE: Only works if you are signed in with cookies.
     */
    val accountIndex: Int? = null,

    /**
     * Specify the Page ID of the YouTube profile/channel to use, if the logged-in account has multiple profiles.
     */
    val onBehalfOfUser: String? = null,

    /**
     * Specifies whether to retrieve the JS player. Disabling this will make session creation faster.
     *
     * NOTE: Deciphering formats is not possible without the JS player.
     */
    val retrievePlayer: Boolean? = null,

    /**
     * Specifies whether to enable safety mode. This will prevent the session from loading any potentially unsafe content.
     */
    val enableSafetyMode: Boolean? = null,

    /**
     * Specifies whether to generate the session data locally or retrieve it from YouTube.
     * This can be useful if you need more performance.
     *
     * NOTE: If you are using the cache option and a session has already been generated, this will be ignored.
     * If you want to force a new session to be generated, you must clear the cache or disable session caching.
     */
    val generateSessionLocally: Boolean? = null,

    /**
     * Specifies whether the session data should be cached.
     */
    val enableSessionCache: Boolean? = null,

    /**
     * Platform to use for the session.
     */
    val deviceCategory: DeviceCategory? = null,

    /**
     * InnerTube client type.
     */
    val clientType: ClientType? = null,

    /**
     * The time zone.
     */
    val timezone: String? = null,

    /**
     * Used to cache algorithms, session data, and OAuth2 tokens.
     */
    val cache: ICache? = null,

    /**
     * YouTube cookies.
     */
    val cookie: String? = null,

    /**
     * Setting this to a valid and persistent visitor data string will allow YouTube to give this session tailored content even when not logged in.
     * A good way to get a valid one is by either grabbing it from a browser or calling InnerTube's `/visitor_id` endpoint.
     */
    val visitorData: String? = null,

    /**
     * Fetch function to use.
     */
    val fetch: FetchFunction? = null,

    /**
     * Proof of Origin Token. This is an attestation token generated by BotGuard/DroidGuard. It is used to confirm that the request is coming from a genuine client.
     */
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
    val onBehalfOfUser: String? = null
)

const val TAG = "Session"

class Session : EventEmitter() {
    lateinit var context: Context
    var player: Player? = null
    lateinit var oauth: OAuth2
    lateinit var http: HTTPClient
    var loggedIn: Boolean = false
    lateinit var actions: Actions
    var cache: ICache? = null
    lateinit var key: String
    lateinit var apiVersion: String
    var accountIndex: Int = 0
    var poToken: String? = null

    constructor(
        context: Context,
        apiKey: String,
        apiVersion: String,
        accountIndex: Int,
        player: Player? = null,
        cookie: String? = null,
        fetch: FetchFunction? = null,
        cache: ICache? = null,
        poToken: String? = null
    ) : super() {
        this.http = HTTPClient(this, cookie, fetch)
        this.actions = Actions(this)
        this.oauth = OAuth2(this)
        this.loggedIn = cookie != null
        this.cache = cache
        this.accountIndex = accountIndex
        this.key = apiKey
        this.apiVersion = apiVersion
        this.context = context
        this.player = player
        this.poToken = poToken
    }

    fun on(type: String, listener: (vararg args: Any) -> Unit) {
        when (type) {
            "auth" -> super.on(type, listener as OAuth2AuthEventHandler)
            "auth-pending" -> super.on(type, listener as OAuth2AuthPendingEventHandler)
            "auth-error" -> super.on(type, listener as OAuth2AuthErrorEventHandler)
            "update-credentials" -> super.on(type, listener as OAuth2AuthEventHandler)
            else -> super.on(type, listener)
        }
    }

    fun once(type: String, listener: (vararg args: Any) -> Unit) {
        when (type) {
            "auth" -> super.once(type, listener as OAuth2AuthEventHandler)
            "auth-pending" -> super.once(type, listener as OAuth2AuthPendingEventHandler)
            "auth-error" -> super.once(type, listener as OAuth2AuthErrorEventHandler)
            else -> super.once(type, listener)
        }
    }


}


