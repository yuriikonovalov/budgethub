package com.yuriikonovalov.common.framework.ui.tag

import com.google.common.truth.Truth.*
import com.yuriikonovalov.common.application.entities.Tag
import com.yuriikonovalov.common.application.usecases.*
import com.yuriikonovalov.common.framework.ui.addedittransaction.tag.TagViewModel
import com.yuriikonovalov.common.presentation.addedittransaction.tag.TagIntent
import com.yuriikonovalov.shared_test.MainCoroutineRule
import com.yuriikonovalov.shared_test.fakes.usecase.FakeDeleteTag
import com.yuriikonovalov.shared_test.fakes.usecase.FakeGetTags
import com.yuriikonovalov.shared_test.fakes.usecase.FakeSaveTag
import com.yuriikonovalov.shared_test.model.tags
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TagViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private fun initSUT(
        selection: List<Tag> = emptyList(),
        getTags: GetTags = FakeGetTags(),
        saveTag: SaveTag = FakeSaveTag(),
        deleteTag: DeleteTag = FakeDeleteTag()
    ): TagViewModel {
        return TagViewModel(selection, getTags, saveTag, deleteTag)
    }

    @Test
    fun `if there's no tags - placeholder is visible`() = runTest {
        // BEFORE
        val sut = initSUT(getTags = FakeGetTags(isSuccess = false))

        // WHEN
        runCurrent()

        // THEN
        val actual = sut.stateFlow.value.emptyPlaceholderVisible
        assertThat(actual).isTrue()
    }

    @Test
    fun `if there's tags - placeholder is not visible`() = runTest {
        // BEFORE
        val sut = initSUT(getTags = FakeGetTags(isSuccess = true))

        // WHEN
        runCurrent()

        // THEN
        val actual = sut.stateFlow.value.emptyPlaceholderVisible
        assertThat(actual).isFalse()
    }

    @Test
    fun `if name field is not empty - add button is enabled`() {
        // BEFORE
        val sut = initSUT()

        // WHEN
        sut.handleIntent(TagIntent.ChangeName("new tag"))

        // THEN
        val actual = sut.stateFlow.value.addButtonEnabled
        assertThat(actual).isTrue()
    }

    @Test
    fun `if name field is empty - add button is not enabled`() {
        // BEFORE
        val sut = initSUT()

        // THEN
        val actual = sut.stateFlow.value.addButtonEnabled
        assertThat(actual).isFalse()
    }

    @Test
    fun `if delete a tag - the tag list and selected tag list don't contain the tag`() {
        // BEFORE
        val tags = tags(3)
        val tag = tags.first()
        val sut = initSUT(getTags = FakeGetTags(tags = tags))

        // WHEN
        sut.handleIntent(TagIntent.DeleteTag(tag))
        val actualTags = sut.stateFlow.value.tagItems.map { it.tag }
        val actualSelectedTags = sut.stateFlow.value.selectedTags

        // THEN
        assertThat(actualTags).doesNotContain(tag)
        assertThat(actualSelectedTags).doesNotContain(tag)
    }

    @Test
    fun `if add a tag - name should be cleared`() = runTest {
        // BEFORE
        val sut = initSUT()

        // WHEN
        sut.handleIntent(TagIntent.ChangeName("new tag"))
        sut.handleIntent(TagIntent.ClickAddButton)
        runCurrent()

        // THEN
        val actual = sut.stateFlow.value.name
        assertThat(actual).isEmpty()
    }

    @Test
    fun `checked items of the tagItems should be equal to selectedTags items`() = runTest {
        // BEFORE
        val tags = tags(5)
        val selection = tags.take(2)
        val sut = initSUT(selection = selection, getTags = FakeGetTags(tags = tags))

        // WHEN
        runCurrent()

        // THEN
        val actual = sut.stateFlow.value.tagItems.filter { it.checked }.map { it.tag }
        assertThat(actual).containsExactlyElementsIn(selection)
    }
}