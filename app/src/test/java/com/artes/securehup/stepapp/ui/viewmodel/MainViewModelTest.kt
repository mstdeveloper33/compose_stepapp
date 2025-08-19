package com.artes.securehup.stepapp.ui.viewmodel

import android.util.Log
import com.artes.securehup.stepapp.domain.usecase.ManageUserProfileUseCase
import com.artes.securehup.stepapp.testutil.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `init checks profile and sets initialized`() = runTest {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        val useCase: ManageUserProfileUseCase = mockk()
        coEvery { useCase.isProfileCompleted() } returns true

        val vm = MainViewModel(useCase)
        advanceUntilIdle()

        val state = vm.uiState.value
        assertThat(state.isInitialized).isTrue()
        assertThat(state.isProfileCompleted).isTrue()
        assertThat(state.error).isNull()
    }

    @Test
    fun `onOnboardingCompleted toggles isProfileCompleted`() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        val useCase: ManageUserProfileUseCase = mockk(relaxed = true)
        val vm = MainViewModel(useCase)
        vm.onOnboardingCompleted()
        val state = vm.uiState.value
        assertThat(state.isProfileCompleted).isTrue()
    }
}


