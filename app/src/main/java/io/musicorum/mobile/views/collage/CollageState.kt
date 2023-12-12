package io.musicorum.mobile.views.collage

import io.musicorum.mobile.models.FetchPeriod
import io.musicorum.mobile.models.MusicorumTheme
import io.musicorum.mobile.models.ResourceEntity

data class CollageState(
    val ready: Boolean = false,
    val isGenerating: Boolean = false,
    val errorMessage: String? = "",
    val selectedTheme: MusicorumTheme = MusicorumTheme.GRID,
    val selectedPeriod: FetchPeriod = FetchPeriod.WEEK,
    val selectedEntity: ResourceEntity = ResourceEntity.Album,
    val hideUsername: Boolean = false,
    val storyMode: Boolean = true,
    val gridRowCount: Int = 6,
    val gridColCount: Int = 6,
    val duotonePalette: String = "Purplish",
    val imageUrl: String? = null
)