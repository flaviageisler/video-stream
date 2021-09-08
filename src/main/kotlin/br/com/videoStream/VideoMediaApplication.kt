package br.com.videoStream

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class VideoStreamApplication

fun main(args: Array<String>) {
    runApplication<VideoStreamApplication>(*args)
}

@RestController
class MessageResource {
    @GetMapping
    fun index(): String = "I'm UP!"

}