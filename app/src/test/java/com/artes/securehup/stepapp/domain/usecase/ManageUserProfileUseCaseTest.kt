package com.artes.securehup.stepapp.domain.usecase

import com.artes.securehup.stepapp.domain.model.Gender
import com.artes.securehup.stepapp.domain.model.UserProfile
import com.artes.securehup.stepapp.domain.repository.UserRepository
import com.artes.securehup.stepapp.testutil.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class ManageUserProfileUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userRepository: UserRepository = mockk(relaxed = true)

    @Test
    fun `getUserProfileFlow returns data`() = runTest {
        val profile = UserProfile(name = "A", age = 22, height = 170.0, weight = 60.0, gender = Gender.FEMALE)
        every { userRepository.getUserProfile() } returns flowOf(profile)

        val useCase = ManageUserProfileUseCase(userRepository)
        val result = useCase.getUserProfileFlow()
        val first = result.first()
        assertThat(first).isEqualTo(profile)
    }

    @Test
    fun `updateUserProfile returns success and calls repo`() = runTest {
        val profile = UserProfile(name = "B", age = 30, height = 180.0, weight = 80.0, gender = Gender.MALE)
        coEvery { userRepository.updateUserProfile(profile) } returns Unit

        val useCase = ManageUserProfileUseCase(userRepository)
        val result = useCase.updateUserProfile(profile)

        assertThat(result.isSuccess).isTrue()
        coVerify { userRepository.updateUserProfile(profile) }
    }

    @Test
    fun `updateGoals delegates to repo for non-null values`() = runTest {
        val useCase = ManageUserProfileUseCase(userRepository)
        coEvery { userRepository.updateStepGoal(any()) } returns Unit
        coEvery { userRepository.updateDistanceGoal(any()) } returns Unit
        coEvery { userRepository.updateCalorieGoal(any()) } returns Unit
        coEvery { userRepository.updateActiveTimeGoal(any()) } returns Unit

        val result = useCase.updateGoals(1000, 2.5, 300, 45)

        assertThat(result.isSuccess).isTrue()
        coVerify { userRepository.updateStepGoal(1000) }
        coVerify { userRepository.updateDistanceGoal(2.5) }
        coVerify { userRepository.updateCalorieGoal(300) }
        coVerify { userRepository.updateActiveTimeGoal(45) }
    }
}


