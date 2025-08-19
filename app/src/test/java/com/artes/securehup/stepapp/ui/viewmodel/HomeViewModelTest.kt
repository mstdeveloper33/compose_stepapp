package com.artes.securehup.stepapp.ui.viewmodel

import app.cash.turbine.test
import com.artes.securehup.stepapp.domain.model.Gender
import com.artes.securehup.stepapp.domain.model.HealthData
import com.artes.securehup.stepapp.domain.model.UserProfile
import com.artes.securehup.stepapp.domain.usecase.GetTodayHealthDataUseCase
import com.artes.securehup.stepapp.domain.usecase.ManageUserProfileUseCase
import com.artes.securehup.stepapp.testutil.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getToday: GetTodayHealthDataUseCase = mockk()
    private val manageUser: ManageUserProfileUseCase = mockk()

    @Test
    fun `init loads today data and observes profile`() = runTest {
        val today = HealthData(date = Date(), steps = 10)
        coEvery { getToday.invoke() } returns today
        every { manageUser.getUserProfileFlow() } returns flowOf(
            UserProfile(name = "A", age = 20, height = 170.0, weight = 60.0, gender = Gender.FEMALE)
        )

        val vm = HomeViewModel(getToday, manageUser)

        vm.uiState.test {
            // İlk default state (isLoading=false)
            val initial = awaitItem()
            assertThat(initial.isLoading).isFalse()

            // Loading state'e geçiş
            val loading = awaitItem()
            assertThat(loading.isLoading).isTrue()

            // Yüklenmiş state (todayHealthData set edilmiş)
            val loaded = awaitItem()
            assertThat(loaded.isLoading).isFalse()
            assertThat(loaded.todayHealthData).isEqualTo(today)

            // Profil güncellemesi akıştan gelebilir
            val withProfile = awaitItem()
            assertThat(withProfile.userProfile?.name).isEqualTo("A")

            cancelAndIgnoreRemainingEvents()
        }
        advanceUntilIdle()
    }
}


