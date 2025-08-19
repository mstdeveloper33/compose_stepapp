package com.artes.securehup.stepapp.domain.usecase // Kullanıcı profili yönetimi use case testleri

import com.artes.securehup.stepapp.domain.model.Gender // Cinsiyet enum
import com.artes.securehup.stepapp.domain.model.UserProfile // Kullanıcı profili modeli
import com.artes.securehup.stepapp.domain.repository.UserRepository // Repo arayüzü (mock)
import com.artes.securehup.stepapp.testutil.MainDispatcherRule // Coroutine test kuralı
import com.google.common.truth.Truth.assertThat // Assertions
import io.mockk.coEvery // Suspend stub
import io.mockk.coVerify // Çağrı doğrulama
import io.mockk.every // Normal stub
import io.mockk.mockk // Mock üretimi
import kotlinx.coroutines.ExperimentalCoroutinesApi // runTest opt-in
import kotlinx.coroutines.flow.first // Flow'dan ilk öğeyi almak
import kotlinx.coroutines.flow.flowOf // Sabit flow oluşturma
import kotlinx.coroutines.test.runTest // Coroutine test runner
import org.junit.Rule // JUnit kuralı
import org.junit.Test // Test anotasyonu
import java.util.Date // Tarih (bu dosyada kullanılmıyor)



/*
Bu sınıfta yaptığımız işlemler şunları içeriyor:
- getUserProfileFlow: Flow'dan profil alınır
- updateUserProfile: Profil güncellenir
- updateGoals: Hedefler güncellenir
*/



@OptIn(ExperimentalCoroutinesApi::class) // runTest kullanımı
class ManageUserProfileUseCaseTest { // Test sınıfı

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule() // Dispatchers.Main kontrolü

    private val userRepository: UserRepository = mockk(relaxed = true) // Repo mock'u

    @Test // Bu test kısmı şunu test ediyor: Flow'dan profil alınır
    fun `getUserProfileFlow returns data`() = runTest { // Flow'dan profil alınır
        val profile = UserProfile(name = "A", age = 22, height = 170.0, weight = 60.0, gender = Gender.FEMALE) // Örnek profil
        every { userRepository.getUserProfile() } returns flowOf(profile) // Repo akışı stub'u every burada şunu sağlıyor: Her çağrıda profile dön. coEveryden farkı: coEvery suspend fonksiyonlar için kullanılır.

        val useCase = ManageUserProfileUseCase(userRepository)
        val result = useCase.getUserProfileFlow() // SUT
        val first = result.first() // İlk öğe
        assertThat(first).isEqualTo(profile) // Doğrula
    }

    @Test // Bu test kısmı şunu test ediyor: Profil güncellenir
    fun `updateUserProfile returns success and calls repo`() = runTest { // Update delegasyonu
        val profile = UserProfile(name = "B", age = 30, height = 180.0, weight = 80.0, gender = Gender.MALE)
        coEvery { userRepository.updateUserProfile(profile) } returns Unit // Başarılı güncelleme

        val useCase = ManageUserProfileUseCase(userRepository)
        val result = useCase.updateUserProfile(profile)

        assertThat(result.isSuccess).isTrue()
        coVerify { userRepository.updateUserProfile(profile) } // Çağrı yapıldı
    }

    @Test // Bu test kısmı şunu test ediyor: Hedef güncellemeleri delegasyonu   
    fun `updateGoals delegates to repo for non-null values`() = runTest { // Hedef güncellemeleri delegasyonu.Bu delegasyon aslında kullanıcı profili güncellemeye yönlendiriliyor. Burası olmazsa kullanıcı profili güncellenemeyecek. Burasının olmasının sebebi: Hedeflerin güncellenmesi için kullanıcı profili güncellenmesi gerekiyor.
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


