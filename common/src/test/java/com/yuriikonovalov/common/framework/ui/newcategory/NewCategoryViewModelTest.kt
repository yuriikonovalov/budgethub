package com.yuriikonovalov.common.framework.ui.newcategory

import android.graphics.Color
import com.google.common.truth.Truth.*
import com.yuriikonovalov.common.application.entities.category.CategoryType
import com.yuriikonovalov.common.application.usecases.GetCategoryIcons
import com.yuriikonovalov.common.application.usecases.GetColors
import com.yuriikonovalov.common.application.usecases.SaveCategory
import com.yuriikonovalov.common.presentation.newcategory.NewCategoryEvent
import com.yuriikonovalov.common.presentation.newcategory.NewCategoryIntent
import com.yuriikonovalov.shared_test.MainCoroutineRule
import com.yuriikonovalov.shared_test.fakes.usecase.FakeGetCategoryIcons
import com.yuriikonovalov.shared_test.fakes.usecase.FakeGetColors
import com.yuriikonovalov.shared_test.fakes.usecase.FakeSaveCategory
import com.yuriikonovalov.shared_test.model.icons
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NewCategoryViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private fun initSUT(
        type: CategoryType = CategoryType.EXPENSE,
        saveCategory: SaveCategory = FakeSaveCategory(),
        getCategoryIcons: GetCategoryIcons = FakeGetCategoryIcons(),
        getColors: GetColors = FakeGetColors()
    ): NewCategoryViewModel {
        return NewCategoryViewModel(type, saveCategory, getCategoryIcons, getColors)
    }

    @Test
    fun `if create a view model - a category type correspond to an argument category type`() {
        // BEFORE
        val expected = CategoryType.INCOME
        val sut = initSUT(type = expected)

        // THEN
        val actual = sut.stateFlow.value.type
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `change a name intent - should update name with a given text`() {
        // BEFORE
        val sut = initSUT()
        val expected = "new name"

        // WHEN
        sut.handleIntent(NewCategoryIntent.ChangeName(expected))

        // THEN
        val actual = sut.stateFlow.value.name
        assertThat(actual).isEqualTo(expected)
    }


    @Test
    fun `if a name is blank or empty - a save button is not enabled`() = runTest {
        // BEFORE
        val icons = icons(5)
        val icon = icons.first()
        val sut = initSUT(getCategoryIcons = FakeGetCategoryIcons(icons))
        val name = " "
        // Load icons
        runCurrent()

        // WHEN
        sut.handleIntent(NewCategoryIntent.ChangeName(name))
        sut.handleIntent(NewCategoryIntent.ChangeIcon(icon))

        // THEN
        val actual = sut.stateFlow.value.saveButtonEnabled
        assertThat(actual).isFalse()
    }

    @Test
    fun `if an icon is null - a save button is not enabled`() {
        // BEFORE
        val sut = initSUT()
        val name = "name"

        // WHEN
        sut.handleIntent(NewCategoryIntent.ChangeName(name))

        // THEN
        val actual = sut.stateFlow.value.saveButtonEnabled
        assertThat(actual).isFalse()
    }

    @Test
    fun `if a name is not blank and an icon is not null - a save button is enabled`() = runTest {
        // BEFORE
        val icons = icons(5)
        val icon = icons.first()
        val sut = initSUT(getCategoryIcons = FakeGetCategoryIcons(icons))
        val name = "name"
        // Load icons
        runCurrent()

        // WHEN
        sut.handleIntent(NewCategoryIntent.ChangeName(name))
        sut.handleIntent(NewCategoryIntent.ChangeIcon(icon))

        // THEN
        val actual = sut.stateFlow.value.saveButtonEnabled
        assertThat(actual).isTrue()
    }

    @Test
    fun `change color intent - should change color value`() {
        // BEFORE
        val sut = initSUT()
        val expected = Color.YELLOW

        // WHEN
        sut.handleIntent(NewCategoryIntent.ChangeColor(expected))

        // THEN
        val actual = sut.stateFlow.value.color
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `if change a color - a selected color should be equal to a colors list item that checked as true`() {
        // BEFORE
        val sut = initSUT()
        val expected = Color.YELLOW

        // WHEN
        sut.handleIntent(NewCategoryIntent.ChangeColor(expected))

        // THEN
        val actual = sut.stateFlow.value.colors.first { it.checked }.color
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `if change a color - a colors list contains only one item checked as true`() {
        // BEFORE
        val sut = initSUT()
        val expected = Color.YELLOW

        // WHEN
        sut.handleIntent(NewCategoryIntent.ChangeColor(expected))

        // THEN
        val actual = sut.stateFlow.value.colors.filter { it.checked }
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `change icon intent - should change icon`() = runTest {
        // BEFORE
        val icons = icons(5)
        val sut = initSUT(getCategoryIcons = FakeGetCategoryIcons(icons))
        val expected = icons.first()
        // Load icons
        runCurrent()

        // WHEN
        sut.handleIntent(NewCategoryIntent.ChangeIcon(expected))

        // THEN
        val actual = sut.stateFlow.value.icon
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `save category - event value should be NavigateUp`() = runTest {
        // BEFORE
        val icons = icons(5)
        val icon = icons.first()
        val sut = initSUT(getCategoryIcons = FakeGetCategoryIcons(icons))
        val name = "name"
        // Load icons
        runCurrent()

        // WHEN
        sut.handleIntent(NewCategoryIntent.ChangeName(name))
        sut.handleIntent(NewCategoryIntent.ChangeIcon(icon))
        sut.handleIntent(NewCategoryIntent.ClickSaveButton)
        // Save category
        runCurrent()

        // THEN
        val actual = sut.eventFlow.value
        assertThat(actual).isInstanceOf(NewCategoryEvent.NavigateUp::class.java)
    }

    @Test
    fun `click a color button - event value should be ShowColorPicker`() {
        // BEFORE
        val sut = initSUT()

        // WHEN
        sut.handleIntent(NewCategoryIntent.ClickColorButton)

        // THEN
        val actual = sut.eventFlow.value
        assertThat(actual).isInstanceOf(NewCategoryEvent.ShowColorPicker::class.java)
    }

    @Test
    fun `select a color in the color picker - the color should be added to the colors list`() {
        // BEFORE
        val colors = listOf(Color.BLACK, Color.YELLOW)
        val expectedColor = Color.RED
        val expectedColors = colors + expectedColor
        val sut = initSUT(getColors = FakeGetColors(colors))

        // WHEN
        sut.handleIntent(NewCategoryIntent.AddColor(expectedColor))

        // THEN
        val actualColor = sut.stateFlow.value.color
        assertThat(actualColor).isEqualTo(expectedColor)

        val actualColors = sut.stateFlow.value.colors.map { it.color }
        assertThat(actualColors).containsExactlyElementsIn(expectedColors)
    }

    @Test
    fun `select a color in the color picker - the color should be added before the last item`() {
        // BEFORE
        val colors = listOf(Color.BLACK, Color.YELLOW, Color.MAGENTA, Color.GRAY)
        val color = Color.RED
        // The last index of the list after adding an item will be the index that is before the last index.
        val expected = colors.lastIndex
        val sut = initSUT(getColors = FakeGetColors(colors))

        // WHEN
        sut.handleIntent(NewCategoryIntent.AddColor(color))

        // THEN
        val actual = sut.stateFlow.value.colors.indexOfFirst { it.color == color }
        assertThat(actual).isEqualTo(expected)
    }
}