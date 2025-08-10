package com.artes.securehup.stepapp.ui.navigation // Package tanımı - hangi klasörde olduğunu belirtir

import androidx.compose.runtime.* // Compose runtime fonksiyonlarını import eder
import androidx.hilt.navigation.compose.hiltViewModel // Hilt ViewModel injection için
import androidx.navigation.NavHostController // Navigation controller tipi
import androidx.navigation.compose.NavHost // Ana navigation container'ı
import androidx.navigation.compose.composable // Route tanımlama fonksiyonu
import androidx.navigation.compose.rememberNavController // Navigation controller oluşturma
import com.artes.securehup.stepapp.domain.model.Gender // Cinsiyet enum'u
import com.artes.securehup.stepapp.ui.screen.onboarding.WelcomeScreen // Hoş geldin sayfası
import com.artes.securehup.stepapp.ui.screen.onboarding.PersonalInfoScreen // Kişisel bilgiler sayfası
import com.artes.securehup.stepapp.ui.screen.onboarding.GoalsScreen // Hedefler sayfası
import com.artes.securehup.stepapp.ui.viewmodel.OnboardingViewModel // Onboarding ViewModel'i

sealed class OnboardingRoute(val route: String) { // Onboarding route'larını tanımlayan sınıf
    object Welcome : OnboardingRoute("welcome") // Hoş geldin sayfası route'u
    object PersonalInfo : OnboardingRoute("personal_info") // Kişisel bilgiler sayfası route'u
    object Goals : OnboardingRoute("goals") // Hedefler sayfası route'u
}

@Composable // Bu fonksiyon UI componenti olduğunu belirtir
fun OnboardingNavigation( // Onboarding navigation ana fonksiyonu
    onOnboardingComplete: () -> Unit, // Onboarding tamamlandığında çalışacak fonksiyon
    navController: NavHostController = rememberNavController(), // Navigation controller (varsayılan: yeni oluştur)
    viewModel: OnboardingViewModel = hiltViewModel() // ViewModel injection (Hilt ile otomatik)
) {
    val uiState by viewModel.uiState.collectAsState() // ViewModel'den UI state'ini reactive olarak al
    
    // Temporary storage for onboarding data
    var userData by remember { mutableStateOf<UserData?>(null) } // Geçici kullanıcı verisi (sayfalar arası taşıma için)
    
    // Observe completion
    LaunchedEffect(uiState.isCompleted) { // isCompleted değiştiğinde çalışacak effect
        if (uiState.isCompleted) { // Eğer onboarding tamamlandıysa
            onOnboardingComplete() // Ana uygulamaya geçiş fonksiyonunu çağır
        }
    }
    
    NavHost( // Ana navigation container'ı
        navController = navController, // Navigation controller'ı ver
        startDestination = OnboardingRoute.Welcome.route // İlk sayfa: Hoş geldin ("welcome")
    ) {
        composable(OnboardingRoute.Welcome.route) { // "welcome" route'u için sayfa tanımı
            WelcomeScreen( // Hoş geldin sayfasını göster
                onNavigateNext = { // "Devam" butonuna tıklandığında
                    navController.navigate(OnboardingRoute.PersonalInfo.route) // Kişisel bilgiler sayfasına git
                }
            )
        }
        
        composable(OnboardingRoute.PersonalInfo.route) { // "personal_info" route'u için sayfa tanımı
            PersonalInfoScreen( // Kişisel bilgiler sayfasını göster
                onNavigateNext = { name, age, height, weight, gender -> // "Devam" butonuna tıklandığında (parametreler ile)
                    userData = UserData(name, age, height, weight, gender) // Girilen verileri geçici olarak kaydet
                    navController.navigate(OnboardingRoute.Goals.route) // Hedefler sayfasına git
                },
                onNavigateBack = { // "Geri" butonuna tıklandığında
                    navController.popBackStack() // Önceki sayfaya dön (Welcome)
                }
            )
        }
        
        composable(OnboardingRoute.Goals.route) { // "goals" route'u için sayfa tanımı
            GoalsScreen( // Hedefler sayfasını göster
                onComplete = { stepGoal, calorieGoal, distanceGoal, activeTimeGoal -> // "Tamamla" butonuna tıklandığında
                    userData?.let { data -> // Eğer kullanıcı verisi varsa
                        viewModel.saveUserProfile( // ViewModel'e tüm verileri gönder
                            name = data.name, // İsim
                            age = data.age, // Yaş
                            height = data.height, // Boy
                            weight = data.weight, // Kilo
                            gender = data.gender, // Cinsiyet
                            stepGoal = stepGoal, // Adım hedefi
                            calorieGoal = calorieGoal, // Kalori hedefi
                            distanceGoal = distanceGoal, // Mesafe hedefi
                            activeTimeGoal = activeTimeGoal // Aktif süre hedefi
                        )
                    }
                },
                onNavigateBack = { // "Geri" butonuna tıklandığında
                    navController.popBackStack() // Önceki sayfaya dön (PersonalInfo)
                }
            )
        }
    }
    
    // Show error dialog if needed
    uiState.error?.let { error -> // Eğer bir hata varsa
        LaunchedEffect(error) { // Hata değiştiğinde çalışacak effect
            // Here you can show a snackbar or dialog
            // For now, we'll just clear the error after showing
            viewModel.clearError() // Hata mesajını temizle
        }
    }
}

data class UserData( // Kullanıcı verilerini taşıyacak geçici data class
    val name: String, // İsim
    val age: Int, // Yaş
    val height: Double, // Boy (cm)
    val weight: Double, // Kilo (kg)
    val gender: Gender // Cinsiyet (enum)
)