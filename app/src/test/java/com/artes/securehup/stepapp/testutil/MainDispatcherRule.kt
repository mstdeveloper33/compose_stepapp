package com.artes.securehup.stepapp.testutil // Testlerde ana dispatcher'ı kontrol edebilmek için yardımcı kuralın paket bildirimi

import kotlinx.coroutines.Dispatchers // Kotlin Coroutines ana dispatcher referansı
import kotlinx.coroutines.ExperimentalCoroutinesApi // Test API'leri için opt-in anotasyonu
import kotlinx.coroutines.test.StandardTestDispatcher // Deterministik test zamanlayıcısı
import kotlinx.coroutines.test.TestDispatcher // Testlerde kullanılan dispatcher arayüzü
import kotlinx.coroutines.test.resetMain // Test bitiminde Main dispatcher'ı eski haline alma
import kotlinx.coroutines.test.setMain // Test başlangıcında Main dispatcher'ı test dispatcher'a yönlendirme
import org.junit.rules.TestWatcher // JUnit kuralı: test başlangıç/bitiş kancaları sağlar
import org.junit.runner.Description // Test açıklaması/metaverisi



/*
Bu sınıfın amacı: Testlerde Dispatchers.Main'i test dispatcher'a yönlendirmek.
Burası olmazsa şöyle bir sorun olacak:
- Testlerde Dispatchers.Main'i test dispatcher'a yönlendirilmez.
- Testlerde Dispatchers.Main'i orijinal haline getirilmez.
*/




@OptIn(ExperimentalCoroutinesApi::class) // Deneysel test API'lerini kullanacağımızı belirtir
class MainDispatcherRule( // JUnit kuralı olarak kullanılacak sınıf
    val testDispatcher: TestDispatcher = StandardTestDispatcher() // Varsayılan deterministik dispatcher
) : TestWatcher() { // TestWatcher'dan türetip life-cycle kancalarını override ederiz. TestWatcher kullanımının sebebi: Testlerin başlangıç ve bitiş zamanlarını kontrol etmek.
    override fun starting(description: Description) { // Her test başlamadan önce çağrılır
        Dispatchers.setMain(testDispatcher) // Dispatchers.Main'i test dispatcher'a yönlendirir
    }

    override fun finished(description: Description) { // Her test bittikten sonra çağrılır
        Dispatchers.resetMain() // Dispatchers.Main'i orijinal haline getirir
    }
}


