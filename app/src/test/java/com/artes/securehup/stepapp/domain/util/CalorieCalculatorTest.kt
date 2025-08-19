package com.artes.securehup.stepapp.domain.util // Kalori ve mesafe hesaplamalarının birim testleri

import com.artes.securehup.stepapp.domain.model.Gender // Boy/kilo/cinsiyet için
import com.artes.securehup.stepapp.domain.model.UserProfile // Kullanıcı profili modeli
import com.google.common.truth.Truth.assertThat // Okunabilir assertion'lar
import org.junit.Test // Test anotasyonu



/*
Bu sınıfta yaptığımız işlemler şunları içeriyor:
- calculateCaloriesFromActivity: 0 dakika -> 0 kalori
- calculateCaloriesFromActivity: MET arttıkça kalori artmalı
- calculateDistanceFromSteps: Adım ve hız arttıkça mesafe artar
- estimateActivityType: Kadans yükseldikçe aktivite tipi daha yüksek MET'e gider
*/


class CalorieCalculatorTest { // Saf Kotlin sınıfı testleri

    private val calculator = CalorieCalculator() // Test edilen sınıf
    private val user = UserProfile(name = "U", age = 28, height = 175.0, weight = 72.0, gender = Gender.MALE) // Örnek profil

    @Test // Bu test kısmı şunu test ediyor: 0 dakika -> 0 kalori
    fun `calculateCaloriesFromActivity returns zero for non-positive duration`() { // 0 dakika -> 0 kalori
        val result = calculator.calculateCaloriesFromActivity(0.0, user, ActivityType.WALKING_NORMAL) // Bu paranteziçindeki parametreler şuradan geliyor:
        // 0.0: 0 dakika
        // user: Kullanıcı profili (örnek profil)
        // ActivityType.WALKING_NORMAL: Yürüyüş aktivitesi (örnek aktivite tipi)



        assertThat(result).isEqualTo(0)
    }

    @Test // Bu test kısmı şunu test ediyor: MET arttıkça kalori artmalı
    fun `calculateCaloriesFromActivity computes proportional to MET and weight`() { // MET arttıkça kalori artmalı . Literal parantezi içindeki kısım testin adı.Burasının olmasının sebebi: Okunabilirlik. 
        val walk = calculator.calculateCaloriesFromActivity(60.0, user, ActivityType.WALKING_NORMAL)
        val run = calculator.calculateCaloriesFromActivity(60.0, user, ActivityType.RUNNING_NORMAL)
        assertThat(run).isGreaterThan(walk) // AssertThat kullanımının sebebi: Okunabilirlik.
    }

    @Test // Bu test kısmı şunu test ediyor: Adım ve hız arttıkça mesafe artar
    fun `calculateDistanceFromSteps increases with steps and activity type`() { // Adım ve hız arttıkça mesafe artar
        val distWalk = calculator.calculateDistanceFromSteps(1000, user, ActivityType.WALKING_NORMAL)
        val distRun = calculator.calculateDistanceFromSteps(1000, user, ActivityType.RUNNING_NORMAL)
        assertThat(distWalk).isGreaterThan(0.0)
        assertThat(distRun).isGreaterThan(distWalk)
    }

    @Test // Bu test kısmı şunu test ediyor: Kadans yükseldikçe aktivite tipi daha yüksek MET'e gider                       
    fun `estimateActivityType increases with cadence`() { // Kadans yükseldikçe aktivite tipi daha yüksek MET'e gider 
        val slow = calculator.estimateActivityType(80, 2.0)
        val fast = calculator.estimateActivityType(400, 2.0)
        assertThat(slow.metValue).isLessThan(fast.metValue)
    }
}


