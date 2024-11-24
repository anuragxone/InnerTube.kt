package com.anuragxone.innertube.types

import core.SessionOptions

typealias InnerTubeConfig = SessionOptions

enum class InnerTubeClient {
    IOS, WEB, ANDROID, YTMUSIC, YTMUSIC_ANDROID, YTSTUDIO_ANDROID, TV, TV_EMBEDDED,
    YTKIDS, WEB_EMBEDDED, WEB_CREATOR
}

enum class UploadDate {
    ALL, HOUR, TODAY, WEEK, MONTH, YEAR
}

enum class SearchType {
    ALL, VIDEO, CHANNEL, PLAYLIST, MOVIE
}

enum class Duration {
    ALL, SHORT, MEDIUM, LONG
}

enum class SortBy {
    RELEVANCE, RATING, UPLOAD_DATE, VIEW_COUNT
}

enum class Feature {
    HD, SUBTITLES, CREATIVE_COMMONS, THREE_D, LIVE, PURCHASED, FOUR_K, THREE_SIXTY,
    LOCATION, HDR, VR180
}

data class SearchFilters(
    val uploadDate: UploadDate? = null,
    val type: SearchType? = null,
    val duration: Duration? = null,
    val sortBy: SortBy? = null,
    val features: List<Feature>? = null
)

data class UpdateVideoMetadataOptions(
    val title: String? = null,
    val description: String? = null,
    val tags: List<String>? = null,
    val category: Int? = null,
    val license: String? = null,
    val ageRestricted: Boolean? = null,
    val madeForKids: Boolean? = null,
    val thumbnail: ByteArray? = null,
    val privacy: Privacy? = null
)

data class UploadedVideoMetadataOptions(
    val title: String? = null,
    val description: String? = null,
    val privacy: Privacy? = null,
    val isDraft: Boolean? = null
)

enum class Privacy {
    PUBLIC, PRIVATE, UNLISTED
}

enum class MusicSearchType {
    ALL, SONG, VIDEO, ALBUM, PLAYLIST, ARTIST
}

data class MusicSearchFilters(
    val type: MusicSearchType? = null
)
