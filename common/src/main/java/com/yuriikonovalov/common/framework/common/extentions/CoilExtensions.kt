package com.yuriikonovalov.common.framework.common.extentions

import android.widget.ImageView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.CachePolicy
import coil.request.ImageRequest


fun ImageView.loadSvg(data: Any?) {
    data?.let {
        val imageLoader = ImageLoader.Builder(this.context)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()

        val request = ImageRequest.Builder(this.context)
            .data(it)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .target(this)
            .build()


        imageLoader.enqueue(request)
    }
}
