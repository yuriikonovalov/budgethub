package com.yuriikonovalov.settings.presentation.createpassword

data class CreatePasswordState(
    val stage: Stage = Stage.INPUT,
    val password: String = "",
    val repeatedPassword: String = ""
) {
    enum class Message {
        PASSWORD_MIN_LENGTH, HAVING_FINISHED_PRESS_CONTINUE, REPEAT_PASSWORD
    }

    enum class Stage {
        INPUT, REPEAT
    }

    val errorPasswordSymbol: Boolean get() = updateErrorPasswordSymbol()
    val message: Message get() = updateMessage()
    val continueButtonEnabled: Boolean get() = isPasswordCorrect(password)
    val okButtonEnabled: Boolean get() = updateOkButtonEnabled()
    val continueButtonVisible: Boolean get() = stage == Stage.INPUT
    val okButtonVisible: Boolean get() = stage == Stage.REPEAT
    val passwordsMatch: Boolean get() = password == repeatedPassword

    private fun updateMessage(): Message {
        return when {
            stage == Stage.INPUT && password.length < PASSWORD_MIN_LENGTH -> {
                Message.PASSWORD_MIN_LENGTH
            }
            stage == Stage.INPUT && password.length >= PASSWORD_MIN_LENGTH -> {
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

    private fun updateOkButtonEnabled(): Boolean {
        return repeatedPassword.isNotBlank() && repeatedPassword.length >= PASSWORD_MIN_LENGTH
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

    fun updatePassword(password: String): CreatePasswordState {
        return when (stage) {
            Stage.INPUT -> copy(password = password)
            Stage.REPEAT -> copy(repeatedPassword = password)
        }
    }

    fun updateStage(stage: Stage): CreatePasswordState {
        return copy(stage = stage)
    }

    companion object {
        private const val PASSWORD_MIN_LENGTH = 6
    }
}
