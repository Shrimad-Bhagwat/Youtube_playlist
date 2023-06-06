package com.shrimadbhagwat.youtube
import android.app.PendingIntent.getService
import  android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Debug
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.fasterxml.jackson.core.JsonFactory
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.shrimadbhagwat.youtube.ui.theme.YoutubeTheme
import com.google.api.services.youtube.YouTube
import androidx.lifecycle.lifecycleScope
import com.google.api.services.youtube.model.SearchListResponse
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import java.util.logging.ConsoleHandler
import com.google.api.client.http.json.JsonHttpContent
import com.google.api.client.json.gson.GsonFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            YoutubeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    YoutubeScreen()
//                    ExoPlayerScreen();
                }
            }
        }
    }
}

@Composable
fun YoutubeScreen( modifier: Modifier = Modifier) {
    var apiKey = "AIzaSyBwSTRtfQlsi-rvkhUvDv3cDk_fm7R39Is"

//    val videoList = getVideoIdsFromYouTubeAPI(apiKey = apiKey, query = "spiderman")
    var videoList by remember {
        mutableStateOf(emptyList<String>())
    }
    var myList = listOf("ZRhJT2nmvA4", "shW9i6k8cB0", "TyskcLbCkqE", "hwNWx1GTSKo")
    fetchYoutubeVideos(apiKey, "spiderman"){ videos ->
        myList = videos
    }


    LazyColumn {
        items(myList) { item ->
            Row {
                AndroidView(factory = {
                    var view  = YouTubePlayerView(it)
                    val fragment = view.addYouTubePlayerListener(
                        object : AbstractYouTubePlayerListener(){
                            override fun onReady(youTubePlayer: YouTubePlayer) {
                                super.onReady(youTubePlayer)
                                youTubePlayer.loadVideo(videoId = item, 0f)
                            }
                        }
                    )
                    view
                })
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

private fun fetchYoutubeVideos(apiKey: String,query: String, onVideosReceived: (List<String>) -> Unit) {
    val httpTransport: HttpTransport = GoogleNetHttpTransport.newTrustedTransport()
    val jsonFactory: GsonFactory? = GsonFactory.getDefaultInstance()
    val youtubeService = YouTube.Builder(httpTransport, jsonFactory, null)
        .setApplicationName("Youtube")
        .build()
    var channelId = "UCvVbWn1GxnOefPEKUiUSuYA"
    try {
        val request = youtubeService.search().list(channelId)
        request.key = apiKey
        request.q = query // Replace with your search query
        request.type = "video"
        request.maxResults = 10 // Set the maximum number of results you want to retrieve

        val response = request.execute() as SearchListResponse
        val videoIds = response.items?.map { it.id.videoId }


        if (videoIds != null) {
            onVideosReceived(videoIds)
        }
    } catch (e: Exception) {
        // Handle any errors or exceptions
        Log.e("Error", e.message.toString())
    }

}

//@Composable
//fun ExoPlayerScreen(modifier: Modifier = Modifier) {
//    val context = LocalContext.current
//    val url = "https://youtu.be/ZRhJT2nmvA4"
//    val exoPlayer = ExoPlayer.Builder(context).build()
//    val mediaItem = MediaItem.fromUri(Uri.parse(url))
//    exoPlayer.setMediaItem(mediaItem)
//
//
//    val playerView = StyledPlayerView(context)
//    playerView.player = exoPlayer
//
//    DisposableEffect(AndroidView(factory = {playerView})){
//
//        exoPlayer.prepare()
//        exoPlayer.playWhenReady= true
//
//        onDispose {
//            exoPlayer.release()
//        }
//    }
//}


































//fun getVideoIdsFromYouTubeAPI(apiKey: String, query: String): List<String> {
//    val packageName = "com.shrimadbhagwat.youtube"

// Replace with your implementation of getService()
//    var youtubeService: YouTube? by mutableStateOf(null)
//    val transport = GoogleNetHttpTransport.newTrustedTransport()
//    val jsonFactory: JacksonFactory? = JacksonFactory.getDefaultInstance()
//    val credential = GoogleCredential.getApplicationDefault()
//        .createScoped(listOf("https://www.googleapis.com/auth/youtube"))
//
//    youtubeService = YouTube.Builder(transport, jsonFactory,credential)
//        .setApplicationName("Youtube")
//        .build()
//    // Perform the API request and retrieve the response
//    val request = youtubeService?.search()?.list("snippet")
//    val response = request
//        ?.setKey(apiKey) // Replace with your actual developer key
//        ?.setQ("tech burner")
//        ?.execute() as SearchListResponse
//    val videoIds = response.items.map { it.id.videoId }
//    return videoIds

//    -----------------


//    val transport = GoogleNetHttpTransport.newTrustedTransport()
//    val jsonFactory: JacksonFactory? = JacksonFactory.getDefaultInstance()
//
//    val youtube = YouTube.Builder(transport, jsonFactory) { request ->
//        request.headers.set("Youtube", packageName)
//    }
//        .setApplicationName("Youtube")
//        .build()
//
//    val request = youtube.search().list("snippet")
//    request.key = apiKey
//    request.q = query // Enter your search query here
//    request.type = "video"
//    request.maxResults = 10 // Adjust the number of results as needed
//
////    val response = request.execute() as SearchListResponse
////    val videoIds = response.items.map { it.id.videoId }
//    return try {
//        val response = request.execute() as SearchListResponse
//        response.items.map { it.id.videoId }
//    } catch (e: Exception) {
//        Log.e("YouTubeAPI", "Error retrieving video IDs from YouTube API: ${e.message}")
//        emptyList()
//    }
////    return videoIds
//}
