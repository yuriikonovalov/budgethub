package com.yuriikonovalov.onboarding.presentation.security.password

data class PasswordState(
    val stage: Stage = Stage.INPUT,
    val password: String = "",
    val repeatedPassword: String = ""
) {
    enum class Message {
        PASSWORD_MIN_LENGTH, HAVING_FINISHED_PRESS_CONTINUE, REPEAT_PASSWORD
    }

    enum class PositiveButtonText {
        CONTINUE, OK
    }

    enum class Stage {
        INPUT, REPEAT
    }

    val message: Message get() = updateMessage()
    val positiveButtonEnabled: Boolean get() = updatePositiveButtonEnabled()
    val positiveButtonText: PositiveButtonText get() = updatePositiveButtonText()
    val passwordsMatch: Boolean get() = updatePasswordsMatch()
    val errorPasswordSymbol: Boolean get() = updateErrorPasswordSymbol()


    private fun updateMessage(): Message {
        return when {
            stage == Stage.INPUT && password.length < PASSWORD_MIN_LENGTH -> {
                Message.PASSWORD_MIN_LENGTH
            }
            stage == Stage.INPUT -> {
                Message.HAVING_FINISHED_PRESS_CONTINUE
            }
            stage == Stage.REPEAT -> {
                Message.REPEAT_PASSWORD
            }
            else -> {
                throw IllegalArgumentException("Unexpected case.")
            }
        }
    }

    private fun updatePositiveButtonEnabled(): Boolean {
        return when (stage) {
            Stage.INPUT -> {
                isPasswordCorrect(password)
            }
            Stage.REPEAT -> {
                repeatedPassword.isNotBlank() && repeatedPassword.length >= PASSWORD_MIN_LENGTH
            }
        }
    }

    private fun updatePositiveButtonText(): PositiveButtonText {
        return when (stage) {
            Stage.INPUT -> PositiveButtonText.CONTINUE
            Stage.REPEAT -> PositiveButtonText.OK
        }
    }

    private fun updatePasswordsMatch(): Boolean {
        return password == repeatedPassword
    }

    private fun updateErrorPasswordSymbol(): Boolean {
        return when (stage) {
            Stage.INPUT -> password.containsIncorrectSymbol()
            Stage.REPEAT -> false
        }
    }

    private fun String.containsIncorrectSymbol(): Boolean {
        // contains non-word character [a-zA-Z_0-9] - true
        return this.contains(Regex("\\W"))
    }

    private fun isPasswordCorrect(password: String): Boolean {
        // contains at least 6 word character [a-zA-Z_0-9] - true
        return password.contains(Regex("^\\w{6,}$"))
    }

    fun updatePassword(password: String): PasswordState {
        return when (stage) {
            Stage.INPUT -> copy(password = password)
            Stage.REPEAT -> copy(repeatedPassword = password)
        }
    }

    fun updateStage(stage: Stage): PasswordState {
        return copy(stage = stage)
    }

    companion object {
        private const val PASSWORD_MIN_LENGTH = 6
    }
}

