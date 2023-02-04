package io.musicorum.mobile.utils

import android.content.Context
import com.crowdin.platform.Crowdin
import com.crowdin.platform.CrowdinConfig

object CrowdinUtils {
    fun initCrowdin(ctx: Context) {
        Crowdin.init(
            ctx,
            CrowdinConfig.Builder()
                .withDistributionHash("e-e9053c0cd23c44eea6b23b2q9b") // required
                .withUpdateInterval(5)
                .build()
        )
    }
}