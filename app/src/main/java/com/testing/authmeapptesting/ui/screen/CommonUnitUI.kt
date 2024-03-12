package com.testing.authmeapptesting.ui.screen

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget

@Composable
fun CircleCropImage(url: String, contentDescription: String, modifier: Modifier = Modifier) {

    var bitmapState: Bitmap? by remember {
        mutableStateOf(null)
    }

    val requestOptions = RequestOptions().transform(CircleCrop())

    Glide.with(LocalContext.current)
        .asBitmap()
        .load(url)
        .apply(requestOptions)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(
                resource: Bitmap,
                transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
            ) {

                bitmapState = resource
            }

            override fun onLoadCleared(placeholder: Drawable?) {}
        })


    bitmapState?.let { bitmap ->
        val painter = BitmapPainter(bitmap.asImageBitmap())
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier
        )
    }

}