package com.artes.securehup.stepapp.domain.util

import com.artes.securehup.stepapp.domain.model.Gender
import com.artes.securehup.stepapp.domain.model.UserProfile
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CalorieCalculatorTest {

    private val calculator = CalorieCalculator()
    private val user = UserProfile(name = "U", age = 28, height = 175.0, weight = 72.0, gender = Gender.MALE)

    @Test
    fun `calculateCaloriesFromActivity returns zero for non-positive duration`() {
        val result = calculator.calculateCaloriesFromActivity(0.0, user, ActivityType.WALKING_NORMAL)
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun `calculateCaloriesFromActivity computes proportional to MET and weight`() {
        val walk = calculator.calculateCaloriesFromActivity(60.0, user, ActivityType.WALKING_NORMAL)
        val run = calculator.calculateCaloriesFromActivity(60.0, user, ActivityType.RUNNING_NORMAL)
        assertThat(run).isGreaterThan(walk)
    }

    @Test
    fun `calculateDistanceFromSteps increases with steps and activity type`() {
        val distWalk = calculator.calculateDistanceFromSteps(1000, user, ActivityType.WALKING_NORMAL)
        val distRun = calculator.calculateDistanceFromSteps(1000, user, ActivityType.RUNNING_NORMAL)
        assertThat(distWalk).isGreaterThan(0.0)
        assertThat(distRun).isGreaterThan(distWalk)
    }

    @Test
    fun `estimateActivityType increases with cadence`() {
        val slow = calculator.estimateActivityType(80, 2.0)
        val fast = calculator.estimateActivityType(400, 2.0)
        assertThat(slow.metValue).isLessThan(fast.metValue)
    }
}


