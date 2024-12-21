package com.anuragxone.innertube

import com.anuragxone.innertube.core.Session
import com.anuragxone.innertube.types.InnerTubeConfig

class Innertube(private val session: Session) {

    companion object {
        suspend fun create(config: InnerTubeConfig = InnerTubeConfig()): Innertube {
            return Innertube(Session.create(config))
        }
    }
}

//    suspend fun getInfo(
//        target: Any, // Accepts String or NavigationEndpoint
//        client: InnerTubeClient? = null
//    ): VideoInfo {
//        throwIfMissing(mapOf("target" to target))
//
//        val nextPayload: INextRequest = when (target) {
//            is NavigationEndpoint -> {
//                NextEndpoint.build(
//                    videoId = target.payload?.videoId,
//                    playlistId = target.payload?.playlistId,
//                    params = target.payload?.params,
//                    playlistIndex = target.payload?.index
//                )
//            }
//
//            is String -> {
//                NextEndpoint.build(videoId = target)
//            }
//
//            else -> {
//                throw InnertubeError("Invalid target. Expected a video ID or NavigationEndpoint.", target)
//            }
//        }
//
//        if (nextPayload.videoId.isNullOrEmpty()) {
//            throw InnertubeError("Video ID cannot be empty", nextPayload)
//        }
//
//        val playerPayload = PlayerEndpoint.build(
//            videoId = nextPayload.videoId,
//            playlistId = nextPayload.playlistId,
//            client = client,
//            sts = session.player?.sts,
//            poToken = session.poToken
//        )
//
//        val playerResponseDeferred = async { actions.execute(PlayerEndpoint.PATH, playerPayload) }
//        val nextResponseDeferred = async { actions.execute(NextEndpoint.PATH, nextPayload) }
//
//        val responses = awaitAll(playerResponseDeferred, nextResponseDeferred)
//
//        val cpn = generateRandomString(16)
//
//        return VideoInfo(responses, actions, cpn)
//    }

//    suspend fun getBasicInfo(
//        videoId: String, client: InnerTubeClient? = null
//    ): VideoInfo {
//        throwIfMissing(mapOf("video_id" to videoId))
//
//        val response = actions.execute(
//            PlayerEndpoint.PATH, PlayerEndpoint.build(
//                videoId = videoId, client = client, sts = session.player?.sts, poToken = session.poToken
//            )
//        )
//
//        val cpn = generateRandomString(16)
//
//        return VideoInfo(listOf(response), actions, cpn)
//    }


//}
