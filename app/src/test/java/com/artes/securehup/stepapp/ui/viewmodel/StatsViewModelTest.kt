package com.artes.securehup.stepapp.ui.viewmodel // StatsViewModel'in haftalık/aylık agregasyonlarını test eder

import com.artes.securehup.stepapp.domain.model.Gender // Profil için cinsiyet
import com.artes.securehup.stepapp.domain.model.HealthData // Günlük sağlık verisi
import com.artes.securehup.stepapp.domain.model.UserProfile // Kullanıcı profili
import com.artes.securehup.stepapp.domain.repository.HealthRepository // Haftalık/aylık veri sorguları için
import com.artes.securehup.stepapp.domain.usecase.GetTodayHealthDataUseCase // Bugün verisi use case'i
import com.artes.securehup.stepapp.domain.usecase.GetWeeklyStatsUseCase // (Kapsam dışı ama ctor bağımlılığı)
import com.artes.securehup.stepapp.domain.usecase.ManageUserProfileUseCase // Profil okuma use case'i
import com.artes.securehup.stepapp.testutil.MainDispatcherRule // Coroutine test kuralı
import com.google.common.truth.Truth.assertThat // Assertion
import io.mockk.coEvery // Suspend stub
import io.mockk.every // Normal stub
import io.mockk.mockk // Mock üretimi
import kotlinx.coroutines.ExperimentalCoroutinesApi // runTest opt-in
import kotlinx.coroutines.test.advanceUntilIdle // Scheduler'ı boşaltma
import kotlinx.coroutines.test.runTest // Coroutine test runner
import org.junit.Rule // JUnit kuralı
import org.junit.Test // Test anotasyonu
import java.util.Date // Tarih

/*
Bu sınıfta yaptığımız işlemler şunları içeriyor:
- loadStats: Haftalık/Aylık toplamlar doğru hesaplanmalı
*/

@OptIn(ExperimentalCoroutinesApi::class) // runTest kullanımı
class StatsViewModelTest { // Test sınıfı

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule() // Dispatchers.Main kontrolü

    private val getToday: GetTodayHealthDataUseCase = mockk() // Use case mock'u
    private val getWeeklyStats: GetWeeklyStatsUseCase = mockk(relaxed = true) // Bağımlılık (bu testte doğrudan kullanılmıyor)
    private val manageUser: ManageUserProfileUseCase = mockk() // Profil use case'i mock'u
    private val healthRepository: HealthRepository = mockk() // Repo mock'u

    @Test // Bu test kısmı şunu test ediyor: Haftalık/Aylık toplamlar doğru hesaplanmalı. Burada şunu test ediyoruz: Haftalık/Aylık toplamlar doğru hesaplanıyor mu?
    fun `loadStats aggregates weekly and monthly data`() = runTest { // Haftalık/Aylık toplamlar doğru hesaplanmalı
        // today
        val today = HealthData(date = Date(), steps = 500) // Bugünün verisi
        coEvery { getToday.invoke() } returns today // Stub

        // profile
        coEvery { manageUser.getUserProfile() } returns UserProfile( // Profil senaryosu
            name = "User", age = 25, height = 175.0, weight = 70.0, gender = Gender.MALE
        )

        // repository weekly/monthly windows ignored via any(), return controlled lists
        val weekList = listOf( // Bu hafta verileri
            HealthData(date = Date(), steps = 100, distance = 1.0, calories = 10, activeTime = 10),
            HealthData(date = Date(), steps = 200, distance = 2.0, calories = 20, activeTime = 20),
            HealthData(date = Date(), steps = 300, distance = 3.0, calories = 30, activeTime = 30)
        )
        val lastWeekList = listOf( // Geçen hafta verileri
            HealthData(date = Date(), steps = 50, distance = 0.5, calories = 5, activeTime = 5)
        )
        coEvery { healthRepository.getHealthDataBetweenDatesSync(any(), any()) } returnsMany listOf(
            weekList, // this week
            lastWeekList, // last week
            weekList, // this month
        )

        val vm = StatsViewModel(getToday, getWeeklyStats, manageUser, healthRepository) // SUT

        advanceUntilIdle() // Tüm coroutine işler bitsin

        val state = vm.uiState.value // Son durum
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


