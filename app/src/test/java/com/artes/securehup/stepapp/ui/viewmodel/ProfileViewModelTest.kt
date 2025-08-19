package com.artes.securehup.stepapp.ui.viewmodel

import app.cash.turbine.test
import com.artes.securehup.stepapp.domain.model.Gender
import com.artes.securehup.stepapp.domain.model.UserProfile
import com.artes.securehup.stepapp.domain.usecase.ManageUserProfileUseCase
import com.artes.securehup.stepapp.domain.util.LanguageManager
import com.artes.securehup.stepapp.testutil.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val useCase: ManageUserProfileUseCase = mockk()
    private val languageManager: LanguageManager = mockk()

    @Test
    fun `observes profile and updates successMessage on update`() = runTest {
        val flow = MutableSharedFlow<UserProfile?>(replay = 1)
        val profile = UserProfile(name = "X", age = 25, height = 170.0, weight = 60.0, gender = Gender.FEMALE)
        every { useCase.getUserProfileFlow() } returns flow
        coEvery { useCase.updateUserProfile(any()) } returns Result.success(Unit)

        val vm = ProfileViewModel(useCase, languageManager)

        // Emit initial profile
        flow.emit(profile)

        vm.uiState.test {
            // initial default state
            val initial = awaitItem()
            assertThat(initial.isLoading).isFalse()

            // collecting started -> loading
            val loading = awaitItem()
            assertThat(loading.isLoading).isTrue()

            // loaded with profile
            val loaded = awaitItem()
            assertThat(loaded.isLoading).isFalse()
            assertThat(loaded.userProfile?.name).isEqualTo("X")
            cancelAndIgnoreRemainingEvents()
        }

        vm.updateProfile(profile)
        advanceUntilIdle()
        val state = vm.uiState.value
        assertThat(state.successMessage).isNotEmpty()
        assertThat(state.error).isNull()
    }

    @Test
    fun `changeLanguage updates state with selected language`() = runTest {
        every { languageManager.setLanguage(any()) } returns Unit
        every { languageManager.getAvailableLanguages() } returns emptyList()
        val useCase = mockk<ManageUserProfileUseCase>()
        every { useCase.getUserProfileFlow() } returns flowOf(null)

        val vm = ProfileViewModel(useCase, languageManager)
        vm.changeLanguage(LanguageManager.LANGUAGE_ENGLISH)
        advanceUntilIdle()

        val state = vm.uiState.value
        assertThat(state.languageChanged).isTrue()
        assertThat(state.selectedLanguage).isEqualTo(LanguageManager.LANGUAGE_ENGLISH)
    }
}


