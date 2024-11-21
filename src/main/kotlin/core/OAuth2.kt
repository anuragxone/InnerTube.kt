package com.anuragxone.core


//import core.Session
//import core.utils.*
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.net.URL
import java.util.Date

class OAuth2(private val session: Session, private val client: HttpClient) {
    companion object {
        private const val TAG = "OAuth2"
    }

    var ytTvUrl: URL = URL(Constants.URLS.YT_BASE + "/tv")
    var authServerCodeUrl: URL = URL(Constants.URLS.YT_BASE + "/o/oauth2/device/code")
    var authServerTokenUrl: URL = URL(Constants.URLS.YT_BASE + "/o/oauth2/token")
    var authServerRevokeTokenUrl: URL = URL(Constants.URLS.YT_BASE + "/o/oauth2/revoke")

    var clientId: OAuth2ClientID? = null
    var oauth2Tokens: OAuth2Tokens? = null

    suspend fun init(tokens: OAuth2Tokens? = null) {
        if (tokens != null) {
            setTokens(tokens)
            if (shouldRefreshToken()) {
                refreshAccessToken()
            }
            session.emit("auth", mapOf("credentials" to oauth2Tokens))
            return
        }

        if (loadFromCache()) {
            Log.info(TAG, "Loaded OAuth2 tokens from cache.", oauth2Tokens)
            return
        }

        clientId = clientId ?: getClientID()
        val deviceAndUserCode = getDeviceAndUserCode()
        session.emit("auth-pending", deviceAndUserCode)
        pollForAccessToken(deviceAndUserCode)
    }

    fun setTokens(tokens: OAuth2Tokens) {
        val expiryDate = tokens.expiresIn?.let {
            Date(System.currentTimeMillis() + it * 1000).toString()
        }
        val modifiedTokens = tokens.copy(expiryDate = expiryDate)

        if (!validateTokens(modifiedTokens)) {
            throw OAuth2Error("Invalid tokens provided.")
        }
        oauth2Tokens = modifiedTokens

        tokens.client?.let {
            Log.info(TAG, "Using provided client ID and secret.")
            clientId = it
        }
    }

    private suspend fun loadFromCache(): Boolean {
        val data = session.cache?.get("youtubei_oauth_credentials") ?: return false
        val credentials = Json.decodeFromString<OAuth2Tokens>(data.decodeToString())
        setTokens(credentials)
        session.emit("auth", mapOf("credentials" to credentials))
        return true
    }

    private suspend fun getDeviceAndUserCode(): DeviceAndUserCode {
        val clientId = clientId?.clientId ?: throw OAuth2Error("Client ID is missing.")
        val payload = mapOf(
            "client_id" to clientId,
            "scope" to "http://gdata.youtube.com https://www.googleapis.com/auth/youtube-paid-content",
            "device_id" to Platform.uuidv4(),
            "device_model" to "ytlr::"
        )

        val response: HttpResponse = client.post(authServerCodeUrl.toString()) {
            contentType(io.ktor.http.ContentType.Application.Json)
            setBody(Json.encodeToString(payload))
        }

        if (!response.status.isSuccess()) {
            throw OAuth2Error("Failed to get device/user code: ${response.status.value}")
        }

        return Json.decodeFromString(response.bodyAsText())
    }

    suspend fun pollForAccessToken(deviceAndUserCode: DeviceAndUserCode) {
        val interval = deviceAndUserCode.interval * 1000L
        while (true) {
            val payload = mapOf(
                "client_id" to clientId?.clientId,
                "client_secret" to clientId?.clientSecret,
                "code" to deviceAndUserCode.deviceCode,
                "grant_type" to "http://oauth.net/grant_type/device/1.0"
            )

            val response: HttpResponse = client.post(authServerTokenUrl.toString()) {
                contentType(io.ktor.http.ContentType.Application.Json)
                setBody(Json.encodeToString(payload))
            }

            val responseData = Json.decodeFromString<OAuth2Tokens>(response.bodyAsText())

            if (responseData.error != null) {
                when (responseData.error) {
                    "authorization_pending", "slow_down" -> Log.info(TAG, "Polling for access token...")
                    else -> {
                        session.emit("auth-error", OAuth2Error(responseData.error!!, responseData))
                        break
                    }
                }
            } else {
                setTokens(responseData)
                session.emit("auth", mapOf("credentials" to oauth2Tokens))
                break
            }
            delay(interval)
        }
    }

    private fun shouldRefreshToken(): Boolean {
        val expiryDate = oauth2Tokens?.expiryDate ?: return false
        return Date().time > Date(expiryDate).time
    }

    private fun validateTokens(tokens: OAuth2Tokens): Boolean {
        return !tokens.accessToken.isNullOrBlank() &&
                !tokens.expiryDate.isNullOrBlank() &&
                !tokens.refreshToken.isNullOrBlank()
    }
}

@Serializable
data class OAuth2ClientID(
    val clientId: String,
    val clientSecret: String
)

@Serializable
data class OAuth2Tokens(
    val accessToken: String,
    val expiryDate: String? = null,
    val expiresIn: Int? = null,
    val refreshToken: String? = null,
    val scope: String? = null,
    val tokenType: String? = null,
    val client: OAuth2ClientID? = null
)

@Serializable
data class DeviceAndUserCode(
    val deviceCode: String,
    val expiresIn: Int,
    val interval: Int,
    val userCode: String,
    val verificationUrl: String,
    val error: String? = null
)
