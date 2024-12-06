package com.anuragxone.innertube

// Core imports
import core.Session
import core.clients.Kids
import core.clients.Music
import core.clients.Studio
import core.managers.AccountManager
import core.managers.InteractionManager
import core.managers.PlaylistManager
import core.mixins.Feed
import core.mixins.TabbedFeed

// Core endpoints
import core.endpoints.BrowseEndpoint
import core.endpoints.GetNotificationMenuEndpoint
import core.endpoints.GuideEndpoint
import core.endpoints.NextEndpoint
import core.endpoints.PlayerEndpoint
import core.endpoints.ResolveURLEndpoint
import core.endpoints.SearchEndpoint
import core.endpoints.Reel
import core.endpoints.Notification

// YouTube parser
import parser.youtube.Channel
import parser.youtube.Comments
import parser.youtube.Guide
import parser.youtube.HashtagFeed
import parser.youtube.History
import parser.youtube.HomeFeed
import parser.youtube.Library
import parser.youtube.NotificationsMenu
import parser.youtube.Playlist
import parser.youtube.Search
import parser.youtube.VideoInfo

// Shorts parser
import parser.ytshorts.ShortFormVideoInfo

// Classes
import parser.classes.NavigationEndpoint

// Utilities
import utils.Constants
import utils.Utils.generateRandomString
import utils.Utils.throwIfMissing
import utils.Utils.u8ToBase64
import utils.Utils.InnertubeError

// Core types
import core.Actions.ApiResponse
import types.InnerTubeConfig
import types.InnerTubeClient
import types.SearchFilters
import types.INextRequest

// Parser types
import parser.types.IBrowseResponse
import parser.types.IParsedResponse

// Format utilities
import types.FormatUtils.DownloadOptions
import types.FormatUtils.FormatOptions
import parser.classes.misc.Format

// Protobuf-generated types
import protos.generated.misc.params.SearchFilter_SortBy
import protos.generated.misc.params.SearchFilter_Filters_UploadDate
import protos.generated.misc.params.SearchFilter_Filters_SearchType
import protos.generated.misc.params.SearchFilter_Filters_Duration
import protos.generated.misc.params.Hashtag
import protos.generated.misc.params.SearchFilter
import protos.generated.misc.params.ReelSequence
import protos.generated.misc.params.GetCommentsSectionParams


import kotlinx.coroutines.runBlocking

class Innertube private constructor(private val session: Session) {

    companion object {
        /**
         * Creates an instance of Innertube with the specified configuration.
         *
         * @param config The InnerTube configuration (default is an empty configuration).
         * @return An instance of [Innertube].
         */
        fun create(config: InnerTubeConfig = InnerTubeConfig()): Innertube {
            val session = runBlocking { Session.create(config) }
            return Innertube(session)
        }
    }

    suspend fun getInfo(
        target: Any, // Accepts String or NavigationEndpoint
        client: InnerTubeClient? = null
    ): VideoInfo {
        throwIfMissing(mapOf("target" to target))

        val nextPayload: INextRequest = when (target) {
            is NavigationEndpoint -> {
                NextEndpoint.build(
                    videoId = target.payload?.videoId,
                    playlistId = target.payload?.playlistId,
                    params = target.payload?.params,
                    playlistIndex = target.payload?.index
                )
            }
            is String -> {
                NextEndpoint.build(videoId = target)
            }
            else -> {
                throw InnertubeError("Invalid target. Expected a video ID or NavigationEndpoint.", target)
            }
        }

        if (nextPayload.videoId.isNullOrEmpty()) {
            throw InnertubeError("Video ID cannot be empty", nextPayload)
        }

        val playerPayload = PlayerEndpoint.build(
            videoId = nextPayload.videoId,
            playlistId = nextPayload.playlistId,
            client = client,
            sts = session.player?.sts,
            poToken = session.poToken
        )

        val playerResponseDeferred = async { actions.execute(PlayerEndpoint.PATH, playerPayload) }
        val nextResponseDeferred = async { actions.execute(NextEndpoint.PATH, nextPayload) }

        val responses = awaitAll(playerResponseDeferred, nextResponseDeferred)

        val cpn = generateRandomString(16)

        return VideoInfo(responses, actions, cpn)
    }

    suspend fun getBasicInfo(
        videoId: String,
        client: InnerTubeClient? = null
    ): VideoInfo {
        throwIfMissing(mapOf("video_id" to videoId))

        val response = actions.execute(
            PlayerEndpoint.PATH,
            PlayerEndpoint.build(
                videoId = videoId,
                client = client,
                sts = session.player?.sts,
                poToken = session.poToken
            )
        )

        val cpn = generateRandomString(16)

        return VideoInfo(listOf(response), actions, cpn)
    }


}
