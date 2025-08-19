package com.artes.securehup.stepapp.domain.usecase // Adım güncelleme iş kuralını test eden sınıf

import com.artes.securehup.stepapp.domain.model.Gender // Kullanıcı profili için cinsiyet
import com.artes.securehup.stepapp.domain.model.UserProfile // Kullanıcı profili modeli
import com.artes.securehup.stepapp.domain.repository.HealthRepository // Sağlık verisi repo arayüzü (mock)
import com.artes.securehup.stepapp.domain.repository.UserRepository // Kullanıcı repo arayüzü (mock)
import com.artes.securehup.stepapp.domain.util.ActivityType // Aktivite türleri (gerekirse)
import com.artes.securehup.stepapp.domain.util.CalorieCalculator // Gerçek hesaplayıcı (saf Kotlin)
import com.artes.securehup.stepapp.testutil.MainDispatcherRule // Coroutine test kuralı
import com.google.common.truth.Truth.assertThat // Assertion
import io.mockk.Called // Kullanılmama doğrulaması için (artık exact:0 kullanıyoruz)
import io.mockk.coEvery // Suspend stub
import io.mockk.coJustRun // (Kullanılmıyor; önceki versiyondan kalma)
import io.mockk.coVerify // Çağrı doğrulama
import io.mockk.mockk // Mock üretimi
import kotlinx.coroutines.ExperimentalCoroutinesApi // runTest opt-in
import kotlinx.coroutines.test.runTest // Coroutine test runner
import org.junit.Rule // JUnit kuralı
import org.junit.Test // Test anotasyonu
import java.util.Date // Tarih tipi

/*
Bu sınıfta yaptığımız işlemler şunları içeriyor:
- updateSteps: Adım güncellenir
- updateSteps: Adım güncellenir
*/

@OptIn(ExperimentalCoroutinesApi::class) // Test API'leri için opt-in
class UpdateStepsUseCaseTest { // Use case testleri

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule() // Dispatchers.Main kontrolü

    private val healthRepository: HealthRepository = mockk(relaxed = true) // Yan etkili çağrılar için relaxed
    private val userRepository: UserRepository = mockk(relaxed = true) // Profil erişimi mock'u
    private val calorieCalculator = CalorieCalculator() // Saf hesaplama, gerçek nesne

    private fun user(): UserProfile = UserProfile( // Testlerde kullanılacak örnek profil
        name = "John",
        age = 30,
        height = 180.0,
        weight = 80.0,
        gender = Gender.MALE
    )

    // every ve coEvery arasındaki fark: every normal fonksiyonlar için kullanılır. coEvery suspend fonksiyonlar için kullanılır.

    @Test // Bu test kısmı şunu test ediyor: Hareket varsa türetilmiş metrikler güncellenir
    fun `updates totals and derived metrics when moving`() = runTest { // Hareket varsa türetilmiş metrikler güncellenir
        val today = Date() // (Şu an direkt kullanılmıyor)
        coEvery { userRepository.getUserProfileSync() } returns user() // Profil mevcut. Bu kısımda şunu sağlıyor: Her çağrıda user() dön.
        coEvery { healthRepository.getHealthDataByDate(any()) } returns null // Bugün için önceki veri yok. Bu kısımda şunu sağlıyor: Her çağrıda null dön.
        coEvery { healthRepository.updateStepsForToday(any()) } returns Result.success(Unit) // Yan etkiler başarılı. Bu kısımda şunu sağlıyor: Her çağrıda Result.success(Unit) dön. Yan etkiler dediğimiz şey: healthRepository'nin updateStepsForToday fonksiyonunun çağrılması.
        coEvery { healthRepository.updateCaloriesForToday(any()) } returns Result.success(Unit) // Yan etkiler başarılı. Bu kısımda şunu sağlıyor: Her çağrıda Result.success(Unit) dön. Yan etkiler dediğimiz şey: healthRepository'nin updateCaloriesForToday fonksiyonunun çağrılması.
        coEvery { healthRepository.updateDistanceForToday(any()) } returns Result.success(Unit) // Yan etkiler başarılı. Bu kısımda şunu sağlıyor: Her çağrıda Result.success(Unit) dön. Yan etkiler dediğimiz şey: healthRepository'nin updateDistanceForToday fonksiyonunun çağrılması.
        coEvery { healthRepository.updateActiveTimeForToday(any()) } returns Result.success(Unit) // Yan etkiler başarılı. Bu kısımda şunu sağlıyor: Her çağrıda Result.success(Unit) dön. Yan etkiler dediğimiz şey: healthRepository'nin updateActiveTimeForToday fonksiyonunun çağrılması.

        val useCase = UpdateStepsUseCase(healthRepository, userRepository, calorieCalculator) // SUT

        val result = useCase(totalSteps = 2000, deltaSteps = 120, deltaMillis = 60_000) // 120 adım/1 dk -> hareket var

        assertThat(result.isSuccess).isTrue() // Başarılı sonuç. AssertThat kullanımının sebebi: Okunabilirlik.
        coVerify { healthRepository.updateStepsForToday(2000) } // Toplam adım yazıldı. coVerify kullanımının sebebi: Çağrı doğrulama.
        coVerify { healthRepository.updateCaloriesForToday(any()) } // Kalori güncellendi. coVerify kullanımının sebebi: Çağrı doğrulama.
        coVerify { healthRepository.updateDistanceForToday(any()) } // Mesafe güncellendi. coVerify kullanımının sebebi: Çağrı doğrulama.
        coVerify { healthRepository.updateActiveTimeForToday(any()) } // Aktif süre güncellendi. coVerify kullanımının sebebi: Çağrı doğrulama.
    }

    @Test // Bu test kısmı şunu test ediyor: Hareket yoksa (kadans/süre 0) türev güncelleme yok
    fun `does not update derived metrics when not moving`() = runTest { // Hareket yoksa (kadans/süre 0) türev güncelleme yok
        coEvery { userRepository.getUserProfileSync() } returns user() // Profil mevcut
        coEvery { healthRepository.getHealthDataByDate(any()) } returns null // Bugün için önceki veri yok
        coEvery { healthRepository.updateStepsForToday(any()) } returns Result.success(Unit) // Toplam adım yazılabilir

        val useCase = UpdateStepsUseCase(healthRepository, userRepository, calorieCalculator) // SUT

        val result = useCase(totalSteps = 1000, deltaSteps = 0, deltaMillis = 0) // Hareket yok

        assertThat(result.isSuccess).isTrue() // Başarılı sonuç
        coVerify { healthRepository.updateStepsForToday(1000) } // Sadece toplam adım güncellendi
        coVerify(exactly = 0) { healthRepository.updateCaloriesForToday(any()) } // Kalori güncellenmedi
        coVerify(exactly = 0) { healthRepository.updateDistanceForToday(any()) } // Mesafe güncellenmedi
        coVerify(exactly = 0) { healthRepository.updateActiveTimeForToday(any()) } // Aktif süre güncellenmedi
    }
}


