package com.logoped_plus.domain.usecase

import com.logoped_plus.domain.model.DeviceVideo
import com.logoped_plus.domain.repository.VideoLibrary
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class FindLessonVideosUseCaseTest {

    private class FakeVideoLibrary(private val videos: List<DeviceVideo>) : VideoLibrary {
        var lastFrom: Instant? = null
        var lastTo: Instant? = null

        override suspend fun findVideos(from: Instant, to: Instant): List<DeviceVideo> {
            lastFrom = from
            lastTo = to
            return videos.filter { it.createdMillis >= from.toEpochMilli() && it.createdMillis < to.toEpochMilli() }
        }
    }

    private val zone = ZoneId.systemDefault()

    private fun videoAt(start: LocalDateTime, offsetSeconds: Long, uri: String) =
        DeviceVideo(uri, "video.mp4", start.atZone(zone).plusSeconds(offsetSeconds).toInstant().toEpochMilli())

    @Test
    fun intervalIsStartInclusiveEndExclusive() = runTest {
        val start = LocalDateTime.of(2026, 9, 30, 9, 0)
        val videos = listOf(
            videoAt(start, 0, "content://videos/start"),
            videoAt(start, 10 * 60, "content://videos/middle"),
            videoAt(start, 40 * 60, "content://videos/end")
        )
        val library = FakeVideoLibrary(videos)

        val found = FindLessonVideosUseCase(library)(start, 40, emptySet())

        assertEquals(listOf("content://videos/start", "content://videos/middle"), found.map { it.uri })
        assertEquals(start.atZone(zone).toInstant(), library.lastFrom)
        assertEquals(start.plusMinutes(40).atZone(zone).toInstant(), library.lastTo)
    }

    @Test
    fun urisAlreadyInDraftAreNotReturned() = runTest {
        val start = LocalDateTime.of(2026, 9, 30, 9, 0)
        val videos = listOf(
            videoAt(start, 5 * 60, "content://videos/one"),
            videoAt(start, 10 * 60, "content://videos/two")
        )

        val found = FindLessonVideosUseCase(FakeVideoLibrary(videos))(start, 40, setOf("content://videos/one"))

        assertEquals(listOf("content://videos/two"), found.map { it.uri })
    }

    @Test
    fun returnsEmptyListWhenNoVideoInRange() = runTest {
        val start = LocalDateTime.of(2026, 9, 30, 9, 0)
        val videos = listOf(
            videoAt(start, -60, "content://videos/before"),
            videoAt(start, 45 * 60, "content://videos/after")
        )

        val found = FindLessonVideosUseCase(FakeVideoLibrary(videos))(start, 40, emptySet())

        assertEquals(emptyList<DeviceVideo>(), found)
    }

    @Test
    fun lessonCrossingMidnightIsSingleEpochInterval() = runTest {
        val start = LocalDateTime.of(2026, 9, 30, 23, 30)
        val videos = listOf(
            videoAt(start, 20 * 60, "content://videos/2350"),
            videoAt(start, 40 * 60, "content://videos/0010"),
            videoAt(start, 60 * 60, "content://videos/0030")
        )
        val library = FakeVideoLibrary(videos)

        val found = FindLessonVideosUseCase(library)(start, 60, emptySet())

        assertEquals(listOf("content://videos/2350", "content://videos/0010"), found.map { it.uri })
        assertEquals(start.atZone(zone).toInstant(), library.lastFrom)
        assertEquals(start.plusMinutes(60).atZone(zone).toInstant(), library.lastTo)
    }
}
