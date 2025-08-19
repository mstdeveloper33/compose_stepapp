package com.artes.securehup.stepapp.ui.viewmodel // HomeViewModel'in başlangıç yükleme ve profil gözlemini test eder

import app.cash.turbine.test // Flow test aracı; awaitItem ile sıradaki state'i bekler
import com.artes.securehup.stepapp.domain.model.Gender // Profil için cinsiyet
import com.artes.securehup.stepapp.domain.model.HealthData // Bugünün verisi
import com.artes.securehup.stepapp.domain.model.UserProfile // Kullanıcı profili
import com.artes.securehup.stepapp.domain.usecase.GetTodayHealthDataUseCase // Bağımlılık
import com.artes.securehup.stepapp.domain.usecase.ManageUserProfileUseCase // Bağımlılık
import com.artes.securehup.stepapp.testutil.MainDispatcherRule // Main dispatcher kontrolü
import com.google.common.truth.Truth.assertThat // Assertion
import io.mockk.coEvery // Suspend stub
import io.mockk.every // Normal stub
import io.mockk.mockk // Mock üretimi
import kotlinx.coroutines.ExperimentalCoroutinesApi // runTest opt-in
import kotlinx.coroutines.flow.MutableStateFlow // (kullanılmıyor: çıkarılabilir)
import kotlinx.coroutines.flow.flowOf // Sabit flow üretimi
import kotlinx.coroutines.test.advanceUntilIdle // Test scheduler'ı boşalana dek ilerletme
import kotlinx.coroutines.test.runTest // Coroutine test runner
import org.junit.Rule // JUnit kuralı
import org.junit.Test // Test anotasyonu
import java.util.Date // Tarih

/*
Bu sınıfta yaptığımız işlemler şunları içeriyor:
- init: Başlangıçta bugünkü veri ve profil kontrolü yapılır
*/


@OptIn(ExperimentalCoroutinesApi::class) // runTest kullanımı için
class HomeViewModelTest { // Test sınıfı

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule() // Dispatchers.Main yönlendirmesi

    private val getToday: GetTodayHealthDataUseCase = mockk() // Use case mock'u
    private val manageUser: ManageUserProfileUseCase = mockk() // Use case mock'u

    @Test
    fun `init loads today data and observes profile`() = runTest { // Başlatma akışını ve profil akışını test eder
        val today = HealthData(date = Date(), steps = 10) // Beklenen bugünkü veri
        coEvery { getToday.invoke() } returns today // Use case stub'u
        every { manageUser.getUserProfileFlow() } returns flowOf( // Profil akışı tek değer döndürsün. Tek değer döndürme sebebi: Profil akışının tek değer döndürmesi.
            UserProfile(name = "A", age = 20, height = 170.0, weight = 60.0, gender = Gender.FEMALE)
        )

        val vm = HomeViewModel(getToday, manageUser) // SUT 

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

            cancelAndIgnoreRemainingEvents() // Testi sonlandır
        }
        advanceUntilIdle() // Asenkron işler tamam
    }
}


