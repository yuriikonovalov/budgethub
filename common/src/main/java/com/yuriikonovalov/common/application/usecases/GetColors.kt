package com.yuriikonovalov.common.application.usecases

interface GetColors {
    operator fun invoke(): List<Int>
}