package com.dsv.basemvvm.domain.models

import com.google.common.math.IntMath

data class ReplayComment (
    val timeInSeconds: Int,
    val comment: String,
    val fromCoach: Boolean
)