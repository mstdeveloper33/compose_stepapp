package com.artes.securehup.stepapp.domain.usecase

import com.artes.securehup.stepapp.domain.model.HealthData
import com.artes.securehup.stepapp.domain.repository.HealthRepository
import com.artes.securehup.stepapp.testutil.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class GetTodayHealthDataUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val healthRepository: HealthRepository = mockk()

    @Test
    fun `invoke returns today's data when repository has entry`() = runTest {
        val today = Date()
        val expected = HealthData(date = today, steps = 1234, distance = 1.2, calories = 80, activeTime = 10)
        coEvery { healthRepository.getHealthDataByDate(any()) } returns expected

        val useCase = GetTodayHealthDataUseCase(healthRepository)

        val result = useCase()

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `invoke returns null when repository has no entry for today`() = runTest {
        coEvery { healthRepository.getHealthDataByDate(any()) } returns null

        val useCase = GetTodayHealthDataUseCase(healthRepository)

        val result = useCase()

        assertThat(result).isNull()
    }
}


