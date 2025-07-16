package io.github.patrickvillarroel.wheel.vault.ui.screen.splash

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import io.github.patrickvillarroel.wheel.vault.R

@Composable
fun SplashScreen(onVideoFinish: () -> Unit, modifier: Modifier = Modifier) {
    val onVideoFinishLatest by rememberUpdatedState(onVideoFinish)
    val context = LocalContext.current

    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            val videoUri: Uri = Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .path(R.raw.splash_content.toString())
                .build()
            val mediaItem = MediaItem.fromUri(videoUri)
            setMediaItem(mediaItem)
            repeatMode = Player.REPEAT_MODE_OFF
            playWhenReady = true
            prepare()
        }
    }

    DisposableEffect(Unit) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    onVideoFinishLatest()
                }
            }
        }

        player.addListener(listener)

        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    Scaffold(
        modifier.fillMaxSize(),
        containerColor = Color.Black,
    ) { paddingValues ->
        PlayerSurface(
            player = player,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        )
    }
}
