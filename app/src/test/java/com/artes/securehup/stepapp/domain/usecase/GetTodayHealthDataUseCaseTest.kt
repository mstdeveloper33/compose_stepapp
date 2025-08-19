package com.artes.securehup.stepapp.domain.usecase // Bugünün sağlık verisini getiren use case'in unit testleri

import com.artes.securehup.stepapp.domain.model.HealthData // Domain model: testte beklenen değerleri üretmek için
import com.artes.securehup.stepapp.domain.repository.HealthRepository // Use case'in bağımlılığı; mock'lanacak
import com.artes.securehup.stepapp.testutil.MainDispatcherRule // Coroutines Main dispatcher kuralı
import io.mockk.coEvery // Suspend fonksiyonlar için MockK stub helper
import io.mockk.mockk // MockK ile mock üretimi
import kotlinx.coroutines.ExperimentalCoroutinesApi // runTest için opt-in
import kotlinx.coroutines.test.runTest // Coroutine test runner
import com.google.common.truth.Truth.assertThat // Assertion kütüphanesi
import org.junit.Rule // JUnit kural anotasyonu
import org.junit.Test // Test anotasyonu
import java.util.Date // Tarih tipi



// 1. Test: Repo'da bugünün data'sı varsa expected dönecek 
// 2. Test: Repo'da bugünün data'sı yoksa null dönecek




@OptIn(ExperimentalCoroutinesApi::class) // runTest kullanımı için
class GetTodayHealthDataUseCaseTest { // Test sınıfı

    @get:Rule  // Bunun sebebi: Testleri Dispatchers.Main'i test dispatcher'a sabitlemek için
     val mainDispatcherRule = MainDispatcherRule() // Dispatchers.Main'i test dispatcher'a sabitle

    private val healthRepository: HealthRepository = mockk() // Use case bağımlılığı mock'u

    @Test // Test kuralı Burada şu durumda test edilecek: Repo'da bugünün data'sı varsa expected dönecek 
    fun `invoke returns today's data when repository has entry`() = runTest { // Mutlu patika bu kısımda 
        val today = Date() // Test referans tarihi
        val expected = HealthData(date = today, steps = 1234, distance = 1.2, calories = 80, activeTime = 10) // Beklenen model bugünün data'sını dönecek 
        coEvery { healthRepository.getHealthDataByDate(any()) } returns expected // Repo mock'u: her çağrıda expected dön bunu bak 

        val useCase = GetTodayHealthDataUseCase(healthRepository) // SUT 

        val result = useCase() // Çalıştır

        assertThat(result).isEqualTo(expected) // Doğrula
    }

    @Test // Test kuralı Burada şu durumda test edilecek: Repo'da bugünün data'sı yoksa null dönecek 
    fun `invoke returns null when repository has no entry for today`() = runTest { // Veri yok patikası
        coEvery { healthRepository.getHealthDataByDate(any()) } returns null // Repo mock'u: boş dön

        val useCase = GetTodayHealthDataUseCase(healthRepository) // SUT

        val result = useCase() // Çalıştır

        assertThat(result).isNull() // Doğrula
    }
}


