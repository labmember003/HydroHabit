package com.falcon.hydrohabit.settings

import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import com.falcon.hydrohabit.R
import com.falcon.hydrohabit.ui.theme.fontFamily
import com.falcon.hydrohabit.ui.theme.fontFamilyBold
import com.falcon.hydrohabit.ui.theme.fontFamilyLight
import com.falcon.hydrohabit.ui.theme.primaryBlack
import com.falcon.hydrohabit.ui.theme.waterColor

private fun getSoundResId(index: Int): Int? {
    return when (index) {
        0 -> R.raw.water_drop_1
        1 -> R.raw.water_drop_2
        2 -> R.raw.water_drop_3
        3 -> R.raw.water_drop_4
        4 -> R.raw.water_drop_5
        else -> null
    }
}

@Composable
actual fun rememberCustomSoundPicker(
    existingUri: String?,
    onSoundPicked: (String) -> Unit
): CustomSoundPickerState {
    val ringtoneLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        @Suppress("DEPRECATION")
        val pickedUri = result.data?.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
        if (pickedUri != null) {
            onSoundPicked(pickedUri.toString())
        }
    }

    return CustomSoundPickerState(
        launch = {
            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
                putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Notification Sound")
                putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
                existingUri?.let { uri ->
                    putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri.toUri())
                }
            }
            ringtoneLauncher.launch(intent)
        }
    )
}

@Composable
actual fun SoundPickerDialog(
    currentIndex: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var previewIndex by remember { mutableIntStateOf(currentIndex) }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    fun previewSound(index: Int) {
        mediaPlayer?.release()
        mediaPlayer = null
        val resId = getSoundResId(index)
        if (resId != null) {
            try {
                mediaPlayer = MediaPlayer.create(context, resId)
                mediaPlayer?.setOnCompletionListener { mp ->
                    mp.release()
                    if (mediaPlayer == mp) mediaPlayer = null
                }
                mediaPlayer?.start()
            } catch (_: Exception) { }
        } else {
            try {
                val defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val ringtone = RingtoneManager.getRingtone(context, defaultUri)
                ringtone?.play()
            } catch (_: Exception) { }
        }
    }

    Dialog(onDismissRequest = {
        mediaPlayer?.release()
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

            soundOptions.forEachIndexed { index, label ->
                val isSelected = index == previewIndex
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (index == 6) {
                                mediaPlayer?.release()
                                onSelect(6)
                            } else {
                                previewIndex = index
                                previewSound(index)
                            }
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
                            text = if (index < 5) "🔔 " else if (index == 5) "📱 " else "🎵 ",
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
                        mediaPlayer?.release()
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
                        mediaPlayer?.release()
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
    if (soundIndex == 6 && customSoundUri != null) {
        val context = LocalContext.current
        return try {
            val ringtone = RingtoneManager.getRingtone(context, customSoundUri.toUri())
            ringtone?.getTitle(context) ?: "Custom"
        } catch (_: Exception) {
            "Custom"
        }
    }
    return if (soundIndex in soundOptions.indices) soundOptions[soundIndex] else "Droplet"
}
