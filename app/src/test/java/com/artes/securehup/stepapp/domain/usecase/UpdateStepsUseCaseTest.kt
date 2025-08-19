package com.artes.securehup.stepapp.domain.usecase

import com.artes.securehup.stepapp.domain.model.Gender
import com.artes.securehup.stepapp.domain.model.UserProfile
import com.artes.securehup.stepapp.domain.repository.HealthRepository
import com.artes.securehup.stepapp.domain.repository.UserRepository
import com.artes.securehup.stepapp.domain.util.ActivityType
import com.artes.securehup.stepapp.domain.util.CalorieCalculator
import com.artes.securehup.stepapp.testutil.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class UpdateStepsUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val healthRepository: HealthRepository = mockk(relaxed = true)
    private val userRepository: UserRepository = mockk(relaxed = true)
    private val calorieCalculator = CalorieCalculator()

    private fun user(): UserProfile = UserProfile(
        name = "John",
        age = 30,
        height = 180.0,
        weight = 80.0,
        gender = Gender.MALE
    )

    @Test
    fun `updates totals and derived metrics when moving`() = runTest {
        val today = Date()
        coEvery { userRepository.getUserProfileSync() } returns user()
        coEvery { healthRepository.getHealthDataByDate(any()) } returns null
        coEvery { healthRepository.updateStepsForToday(any()) } returns Result.success(Unit)
        coEvery { healthRepository.updateCaloriesForToday(any()) } returns Result.success(Unit)
        coEvery { healthRepository.updateDistanceForToday(any()) } returns Result.success(Unit)
        coEvery { healthRepository.updateActiveTimeForToday(any()) } returns Result.success(Unit)

        val useCase = UpdateStepsUseCase(healthRepository, userRepository, calorieCalculator)

        val result = useCase(totalSteps = 2000, deltaSteps = 120, deltaMillis = 60_000)

        assertThat(result.isSuccess).isTrue()
        coVerify { healthRepository.updateStepsForToday(2000) }
        coVerify { healthRepository.updateCaloriesForToday(any()) }
        coVerify { healthRepository.updateDistanceForToday(any()) }
        coVerify { healthRepository.updateActiveTimeForToday(any()) }
    }

    @Test
    fun `does not update derived metrics when not moving`() = runTest {
        coEvery { userRepository.getUserProfileSync() } returns user()
        coEvery { healthRepository.getHealthDataByDate(any()) } returns null
        coEvery { healthRepository.updateStepsForToday(any()) } returns Result.success(Unit)

        val useCase = UpdateStepsUseCase(healthRepository, userRepository, calorieCalculator)

        val result = useCase(totalSteps = 1000, deltaSteps = 0, deltaMillis = 0)

        assertThat(result.isSuccess).isTrue()
        coVerify { healthRepository.updateStepsForToday(1000) }
        coVerify(exactly = 0) { healthRepository.updateCaloriesForToday(any()) }
        coVerify(exactly = 0) { healthRepository.updateDistanceForToday(any()) }
        coVerify(exactly = 0) { healthRepository.updateActiveTimeForToday(any()) }
    }
}


