package com.yuriikonovalov.budgethub.framework.ui

sealed interface MainActivityDirection {
    object Login : MainActivityDirection
    object Onboarding : MainActivityDirection
    object Home : MainActivityDirection
}
