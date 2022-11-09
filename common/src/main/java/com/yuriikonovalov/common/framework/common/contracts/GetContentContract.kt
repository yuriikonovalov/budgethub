package com.yuriikonovalov.common.framework.common.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.yuriikonovalov.common.framework.common.extentions.toTempFileFromContentUri
import java.io.File

/**
 * A custom ActivityResultContract for getting content and wrapping saving it in a cache file.
 *
 * @return a cache [File] with the content or null.
 */
class GetContentContract : ActivityResultContract<String, File?>() {
    private var context: Context? = null

    override fun createIntent(context: Context, input: String): Intent {
        this.context = context
        return Intent(Intent.ACTION_GET_CONTENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .setType(input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): File? {
        return if (resultCode == Activity.RESULT_OK) {
            intent?.data?.toTempFileFromContentUri(context!!)
        } else {
            null
        }
    }


}