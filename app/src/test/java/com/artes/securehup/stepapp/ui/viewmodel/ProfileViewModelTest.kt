package com.artes.securehup.stepapp.ui.viewmodel // ProfileViewModel'in akış ve güncelleme davranışlarını test eder

import app.cash.turbine.test // Flow/StateFlow test aracı
import com.artes.securehup.stepapp.domain.model.Gender // Profil cinsiyet
import com.artes.securehup.stepapp.domain.model.UserProfile // Profil modeli
import com.artes.securehup.stepapp.domain.usecase.ManageUserProfileUseCase // Use case mock'u
import com.artes.securehup.stepapp.domain.util.LanguageManager // Dil yöneticisi bağımlılığı
import com.artes.securehup.stepapp.testutil.MainDispatcherRule // Main dispatcher kuralı
import com.google.common.truth.Truth.assertThat // Assertion'lar
import io.mockk.coEvery // Suspend stub
import io.mockk.every // Normal stub
import io.mockk.mockk // Mock üretimi
import kotlinx.coroutines.ExperimentalCoroutinesApi // runTest opt-in
import kotlinx.coroutines.flow.MutableSharedFlow // Test akışı için sıcak akış
import kotlinx.coroutines.flow.MutableStateFlow // (kullanılmıyor)
import kotlinx.coroutines.flow.flowOf // Sabit akış üretimi
import kotlinx.coroutines.test.advanceUntilIdle // Zamanlayıcıyı boşalt
import kotlinx.coroutines.test.runTest // Coroutine test runner
import org.junit.Rule // JUnit kuralı
import org.junit.Test // Test anotasyonu

@OptIn(ExperimentalCoroutinesApi::class) // runTest kullanımı
class ProfileViewModelTest { // Test sınıfı

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule() // Dispatchers.Main kontrolü

    private val useCase: ManageUserProfileUseCase = mockk() // Use case mock'u
    private val languageManager: LanguageManager = mockk() // Dil yöneticisi mock'u

    @Test
    fun `observes profile and updates successMessage on update`() = runTest { // Profil akışı ve update sonucu
        val flow = MutableSharedFlow<UserProfile?>(replay = 1) // İlk değeri saklayacak sıcak akış
        val profile = UserProfile(name = "X", age = 25, height = 170.0, weight = 60.0, gender = Gender.FEMALE)
        every { useCase.getUserProfileFlow() } returns flow // Use case akışı stub'u
        coEvery { useCase.updateUserProfile(any()) } returns Result.success(Unit) // Update başarı stub'u

        val vm = ProfileViewModel(useCase, languageManager) // SUT

        // İlk profili yayınla
        flow.emit(profile)

        vm.uiState.test {
            // initial default state
            val initial = awaitItem()
            assertThat(initial.isLoading).isFalse()

            // collecting started -> loading
            val loading = awaitItem()
            assertThat(loading.isLoading).isTrue()

            // loaded with profile
            val loaded = awaitItem()
            assertThat(loaded.isLoading).isFalse()
            assertThat(loaded.userProfile?.name).isEqualTo("X")
            cancelAndIgnoreRemainingEvents() // Testi sonlandır
        }

        vm.updateProfile(profile) // Güncelleme tetikle
        advanceUntilIdle() // Asenkron işler bitsin
        val state = vm.uiState.value
        assertThat(state.successMessage).isNotEmpty() // Başarı mesajı set olmalı
        assertThat(state.error).isNull() // Hata olmamalı
    }

    @Test
    fun `changeLanguage updates state with selected language`() = runTest { // Dil değişimi state'i günceller
        every { languageManager.setLanguage(any()) } returns Unit // Stub: ayar yazımı
        every { languageManager.getAvailableLanguages() } returns emptyList() // (Kullanılmıyor)
        val useCase = mockk<ManageUserProfileUseCase>() // Akış için stub
        every { useCase.getUserProfileFlow() } returns flowOf(null)

        val vm = ProfileViewModel(useCase, languageManager)
        vm.changeLanguage(LanguageManager.LANGUAGE_ENGLISH) // EN seç
        advanceUntilIdle()

        val state = vm.uiState.value
        assertThat(state.languageChanged).isTrue()
        assertThat(state.selectedLanguage).isEqualTo(LanguageManager.LANGUAGE_ENGLISH)
    }
}


