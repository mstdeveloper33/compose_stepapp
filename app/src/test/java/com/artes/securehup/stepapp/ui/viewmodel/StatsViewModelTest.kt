package com.artes.securehup.stepapp.ui.viewmodel

import com.artes.securehup.stepapp.domain.model.Gender
import com.artes.securehup.stepapp.domain.model.HealthData
import com.artes.securehup.stepapp.domain.model.UserProfile
import com.artes.securehup.stepapp.domain.repository.HealthRepository
import com.artes.securehup.stepapp.domain.usecase.GetTodayHealthDataUseCase
import com.artes.securehup.stepapp.domain.usecase.GetWeeklyStatsUseCase
import com.artes.securehup.stepapp.domain.usecase.ManageUserProfileUseCase
import com.artes.securehup.stepapp.testutil.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class StatsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getToday: GetTodayHealthDataUseCase = mockk()
    private val getWeeklyStats: GetWeeklyStatsUseCase = mockk(relaxed = true)
    private val manageUser: ManageUserProfileUseCase = mockk()
    private val healthRepository: HealthRepository = mockk()

    @Test
    fun `loadStats aggregates weekly and monthly data`() = runTest {
        // today
        val today = HealthData(date = Date(), steps = 500)
        coEvery { getToday.invoke() } returns today

        // profile
        coEvery { manageUser.getUserProfile() } returns UserProfile(
            name = "User", age = 25, height = 175.0, weight = 70.0, gender = Gender.MALE
        )

        // repository weekly/monthly windows ignored via any(), return controlled lists
        val weekList = listOf(
            HealthData(date = Date(), steps = 100, distance = 1.0, calories = 10, activeTime = 10),
            HealthData(date = Date(), steps = 200, distance = 2.0, calories = 20, activeTime = 20),
            HealthData(date = Date(), steps = 300, distance = 3.0, calories = 30, activeTime = 30)
        )
        val lastWeekList = listOf(
            HealthData(date = Date(), steps = 50, distance = 0.5, calories = 5, activeTime = 5)
        )
        coEvery { healthRepository.getHealthDataBetweenDatesSync(any(), any()) } returnsMany listOf(
            weekList, // this week
            lastWeekList, // last week
            weekList, // this month
        )

        val vm = StatsViewModel(getToday, getWeeklyStats, manageUser, healthRepository)

        advanceUntilIdle()

        val state = vm.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.todayHealthData).isEqualTo(today)
        assertThat(state.weeklyStats).isNotNull()
        assertThat(state.weeklyStats!!.totalSteps).isEqualTo(600)
        assertThat(state.weeklyStats!!.totalDistance).isEqualTo(6.0)
        assertThat(state.weeklyStats!!.totalCalories).isEqualTo(60)
        assertThat(state.weeklyStats!!.totalActiveTime).isEqualTo(60)

        assertThat(state.monthlyStats).isNotNull()
        assertThat(state.monthlyStats!!.totalSteps).isEqualTo(600)
        assertThat(state.userProfile?.name).isEqualTo("User")
    }
}


