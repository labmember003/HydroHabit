package com.falcon.hydrohabit.composeapp.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.cinterop.ExperimentalForeignApi
import com.falcon.hydrohabit.composeapp.ui.theme.fontFamily
import com.falcon.hydrohabit.composeapp.ui.theme.fontFamilyBold
import com.falcon.hydrohabit.composeapp.ui.theme.fontFamilyLight
import com.falcon.hydrohabit.composeapp.ui.theme.primaryBlack
import com.falcon.hydrohabit.composeapp.ui.theme.waterColor
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.AudioToolbox.AudioServicesPlaySystemSound
import platform.Foundation.NSBundle
import platform.Foundation.NSURL

private fun soundFileName(index: Int): String? = when (index) {
    0 -> "water_drop_1"
    1 -> "water_drop_2"
    2 -> "water_drop_3"
    3 -> "water_drop_4"
    4 -> "water_drop_5"
    else -> null
}

@Composable
actual fun rememberCustomSoundPicker(
    existingUri: String?,
    onSoundPicked: (String) -> Unit
): CustomSoundPickerState {
    return CustomSoundPickerState(launch = { })
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun SoundPickerDialog(
    currentIndex: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val iosOptions = soundOptions.dropLast(1)
    var player by remember { mutableStateOf<AVAudioPlayer?>(null) }
    var previewIndex by remember { mutableIntStateOf(currentIndex) }

    DisposableEffect(Unit) {
        val session = AVAudioSession.sharedInstance()
        session.setCategory(AVAudioSessionCategoryPlayback, error = null)
        session.setActive(true, error = null)
        onDispose {
            player?.stop()
            player = null
        }
    }

    fun previewSound(index: Int) {
        player?.stop()
        player = null

        val fileName = soundFileName(index)
        if (fileName == null) {
            // System Default (index 5) — no bundled asset, play a system notification tone.
            AudioServicesPlaySystemSound(1007u)
            return
        }

        val path = NSBundle.mainBundle.pathForResource(fileName, ofType = "wav") ?: return
        val url = NSURL.fileURLWithPath(path)
        val newPlayer = AVAudioPlayer(contentsOfURL = url, error = null)
        newPlayer.prepareToPlay()
        newPlayer.play()
        player = newPlayer
    }

    Dialog(onDismissRequest = {
        player?.stop()
        onDismiss()
    }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Text(
                text = "Notification Sound",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = fontFamilyBold,
                    fontWeight = FontWeight(600),
                    color = primaryBlack,
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tap to preview sound",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF8E8E93),
                )
            )
            Spacer(modifier = Modifier.height(20.dp))

            iosOptions.forEachIndexed { index, label ->
                val isSelected = index == previewIndex
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            previewIndex = index
                            previewSound(index)
                        }
                        .background(
                            color = if (isSelected) waterColor.copy(alpha = 0.1f) else Color.Transparent,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (index < 5) "🔔 " else "📱 ",
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = label,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = fontFamilyLight,
                                fontWeight = if (isSelected) FontWeight(500) else FontWeight(400),
                                color = if (isSelected) waterColor else primaryBlack,
                            )
                        )
                    }
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Selected",
                            tint = waterColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        player?.stop()
                        onDismiss()
                    }
                ) {
                    Text(
                        text = "Cancel",
                        color = Color(0xFF8E8E93),
                        fontFamily = fontFamily,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = {
                        player?.stop()
                        onSelect(previewIndex)
                    }
                ) {
                    Text(
                        text = "Done",
                        color = waterColor,
                        fontFamily = fontFamilyBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
actual fun resolveSoundDisplayName(soundIndex: Int, customSoundUri: String?): String {
    return if (soundIndex in soundOptions.indices) soundOptions[soundIndex] else "Droplet"
}
