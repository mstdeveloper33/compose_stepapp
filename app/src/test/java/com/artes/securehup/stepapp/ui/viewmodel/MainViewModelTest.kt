package com.artes.securehup.stepapp.ui.viewmodel // MainViewModel'in profil kontrol ve onboarding flag davranışlarını test eder

import android.util.Log // JVM'de mock'lanması gereken Android Log API
import com.artes.securehup.stepapp.domain.usecase.ManageUserProfileUseCase // Use case bağımlılığı
import com.artes.securehup.stepapp.testutil.MainDispatcherRule // Main dispatcher kuralı
import com.google.common.truth.Truth.assertThat // Assertions
import io.mockk.coEvery // Suspend stub
import io.mockk.every // Normal stub
import io.mockk.mockk // Mock üretimi
import io.mockk.mockkStatic // Statik metodları mock'lamak için
import kotlinx.coroutines.ExperimentalCoroutinesApi // runTest opt-in
import kotlinx.coroutines.test.advanceUntilIdle // Scheduler'ı boşalt
import kotlinx.coroutines.test.runTest // Coroutine test runner
import org.junit.Rule // JUnit kuralı
import org.junit.Test // Test anotasyonu

/*
Bu sınıfta yaptığımız işlemler şunları içeriyor:
- init: Başlangıçta profil kontrolü yapılır
- onOnboardingCompleted: Onboarding tamamlanınca flag true olur
*/

@OptIn(ExperimentalCoroutinesApi::class) // runTest kullanımı
class MainViewModelTest { // Test sınıfı

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule() // Dispatchers.Main kontrolü

    @Test // Bu test kısmı şunu test ediyor: Başlangıçta profil kontrolü yapılır
    fun `init checks profile and sets initialized`() = runTest { // Başlangıçta profil kontrolü yapılır
        mockkStatic(Log::class) // Android Log mock'u
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        val useCase: ManageUserProfileUseCase = mockk()
        coEvery { useCase.isProfileCompleted() } returns true // Profil tamamlandı

        val vm = MainViewModel(useCase)
        advanceUntilIdle() // Asenkron işler bitsin

        val state = vm.uiState.value
        assertThat(state.isInitialized).isTrue()
        assertThat(state.isProfileCompleted).isTrue()
        assertThat(state.error).isNull()
    }

    @Test
    fun `onOnboardingCompleted toggles isProfileCompleted`() { // Onboarding tamamlanınca flag true olur
        mockkStatic(Log::class) // Android Log mock'u
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        val useCase: ManageUserProfileUseCase = mockk(relaxed = true)
        val vm = MainViewModel(useCase)
        vm.onOnboardingCompleted()
        val state = vm.uiState.value
        assertThat(state.isProfileCompleted).isTrue()
    }
}


