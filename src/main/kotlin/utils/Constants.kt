package com.anuragxone.innertube.utils


object Constants {

    object URLS {
        const val YT_BASE = "https://www.youtube.com"
        const val YT_MUSIC_BASE = "https://music.youtube.com"
        const val YT_SUGGESTIONS = "https://suggestqueries.google.com/complete/"
        const val YT_UPLOAD = "https://upload.youtube.com/"

        object API {
            const val BASE = "https://youtubei.googleapis.com"
            const val PRODUCTION_1 = "https://www.youtube.com/youtubei/"
            const val PRODUCTION_2 = "https://youtubei.googleapis.com/youtubei/"
            const val STAGING = "https://green-youtubei.sandbox.googleapis.com/youtubei/"
            const val RELEASE = "https://release-youtubei.sandbox.googleapis.com/youtubei/"
            const val TEST = "https://test-youtubei.sandbox.googleapis.com/youtubei/"
            const val CAMI = "http://cami-youtubei.sandbox.googleapis.com/youtubei/"
            const val UYTFE = "https://uytfe.sandbox.google.com/youtubei/"
        }

        const val GOOGLE_SEARCH_BASE = "https://www.google.com/"
    }

    object OAUTH {
        object REGEX {
            val TV_SCRIPT = Regex("""<script\s+id="base-js"\s+src="([^"]+)"[^>]*></script>""")
            val CLIENT_IDENTITY = Regex("""clientId:"(?<client_id>[^"]+)",[^"]*?:"(?<client_secret>[^"]+)"""")
        }
    }

    object CLIENTS {
        object IOS {
            const val NAME_ID = "5"
            const val NAME = "iOS"
            const val VERSION = "18.06.35"
            const val USER_AGENT = "com.google.ios.youtube/18.06.35 (iPhone; CPU iPhone OS 14_4 like Mac OS X; en_US)"
            const val DEVICE_MODEL = "iPhone10,6"
        }

        object WEB {
            const val NAME_ID = "1"
            const val NAME = "WEB"
            const val VERSION = "2.20240111.09.00"
            const val API_KEY = "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8"
            const val API_VERSION = "v1"
            const val STATIC_VISITOR_ID = "6zpwvWUNAco"
        }

        object WEB_KIDS {
            const val NAME_ID = "76"
            const val NAME = "WEB_KIDS"
            const val VERSION = "2.20230111.00.00"
        }

        object YTMUSIC {
            const val NAME_ID = "67"
            const val NAME = "WEB_REMIX"
            const val VERSION = "1.20211213.00.00"
        }

        object ANDROID {
            const val NAME_ID = "3"
            const val NAME = "ANDROID"
            const val VERSION = "19.35.36"
            const val SDK_VERSION = 33
            const val USER_AGENT = "com.google.android.youtube/19.35.36(Linux; U; Android 13; en_US; SM-S908E Build/TP1A.220624.014) gzip"
        }

        object YTSTUDIO_ANDROID {
            const val NAME_ID = "14"
            const val NAME = "ANDROID_CREATOR"
            const val VERSION = "22.43.101"
        }

        object YTMUSIC_ANDROID {
            const val NAME_ID = "21"
            const val NAME = "ANDROID_MUSIC"
            const val VERSION = "5.34.51"
        }

        object TV {
            const val NAME_ID = "7"
            const val NAME = "TVHTML5"
            const val VERSION = "7.20241016.15.00"
            const val USER_AGENT = "Mozilla/5.0 (ChromiumStylePlatform) Cobalt/Version"
        }

        object TV_EMBEDDED {
            const val NAME_ID = "85"
            const val NAME = "TVHTML5_SIMPLY_EMBEDDED_PLAYER"
            const val VERSION = "2.0"
        }

        object WEB_EMBEDDED {
            const val NAME_ID = "56"
            const val NAME = "WEB_EMBEDDED_PLAYER"
            const val VERSION = "2.20240111.09.00"
            const val API_KEY = "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8"
            const val API_VERSION = "v1"
            const val STATIC_VISITOR_ID = "6zpwvWUNAco"
        }

        object WEB_CREATOR {
            const val NAME_ID = "62"
            const val NAME = "WEB_CREATOR"
            const val VERSION = "1.20240918.03.00"
            const val API_KEY = "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8"
            const val API_VERSION = "v1"
            const val STATIC_VISITOR_ID = "6zpwvWUNAco"
        }
    }

    object STREAM_HEADERS {
        const val ACCEPT = "*/*"
        const val ORIGIN = "https://www.youtube.com"
        const val REFERER = "https://www.youtube.com"
        const val DNT = "?1"
    }

    object INNERTUBE_HEADERS_BASE {
        const val ACCEPT = "*/*"
        const val ACCEPT_ENCODING = "gzip, deflate"
        const val CONTENT_TYPE = "application/json"
    }

    val SUPPORTED_CLIENTS = listOf(
        "IOS", "WEB", "YTKIDS", "YTMUSIC", "ANDROID",
        "YTSTUDIO_ANDROID", "YTMUSIC_ANDROID", "TV",
        "TV_EMBEDDED", "WEB_EMBEDDED", "WEB_CREATOR"
    )
}
