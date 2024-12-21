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
    val apiVersion: String = apiVersion
    val accountIndex: Int = accountIndex

    init {
        this.player = player
    }



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
            visitorData: String = "",
            enableSafetyMode: Boolean = false,
            generateSessionLocally: Boolean = false,
            deviceCategory: DeviceCategory = DeviceCategory.DESKTOP,
            clientName: ClientType = ClientType.WEB,
            timezone: String = java.util.TimeZone.getDefault().id,
            fetch: FetchFunction = Platform.shim.fetch,
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

                if (!generateSessionLocally) {
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

