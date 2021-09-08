package br.com.videoStream.video

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/video")
class VideoController {

    @GetMapping("/stream")
    fun streamVideo(
        @RequestHeader(name = "Range", defaultValue = "") aHttpRangeList: String
    ): Mono<ResponseEntity<ByteArray>> {
        val lRangeList = aHttpRangeList.ifEmpty { null }
        val lVideoStreamService = VideoStreamService()
        return Mono.just(
            lVideoStreamService.prepareContent(
                "your\\path\\toystory.mp4", //Resource folder,
                lRangeList
            )
        )
    }

}