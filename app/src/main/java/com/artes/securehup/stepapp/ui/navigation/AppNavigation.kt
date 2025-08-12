package com.artes.securehup.stepapp.ui.navigation // Package tanımı - hangi klasörde olduğunu belirtir

import androidx.compose.foundation.layout.padding // Padding eklemek için
import androidx.compose.material3.* // Material 3 UI componentlerini import eder
import androidx.compose.runtime.* // Compose runtime fonksiyonlarını import eder
import androidx.compose.ui.Modifier // UI modifier'ları için
import androidx.navigation.NavHostController // Navigation controller tipi
import androidx.navigation.compose.NavHost // Ana navigation container'ı
import androidx.navigation.compose.composable // Route tanımlama fonksiyonu
import androidx.navigation.compose.currentBackStackEntryAsState // Şu anki route'u almak için
import androidx.navigation.compose.rememberNavController // Navigation controller oluşturma
import androidx.navigation.navArgument // Route parametreleri tanımlama
import com.artes.securehup.stepapp.ui.screen.HomeScreen // Ana sayfa
import com.artes.securehup.stepapp.ui.screen.StatsScreen // İstatistikler sayfası
import com.artes.securehup.stepapp.ui.screen.ProfileScreen // Profil sayfası
import com.artes.securehup.stepapp.ui.screen.ProfileEditScreen // Profil düzenleme sayfası
import com.artes.securehup.stepapp.ui.screen.GoalsEditScreen // Hedef düzenleme sayfası

@OptIn(ExperimentalMaterial3Api::class) // Deneysel Material 3 API'sini kullan
@Composable // Bu fonksiyon UI componenti olduğunu belirtir
fun AppNavigation( // Ana uygulama navigation fonksiyonu
    navController: NavHostController = rememberNavController(), // Navigation controller (varsayılan: yeni oluştur)
    onLanguageChanged: () -> Unit = {} // Dil değişimi callback'i
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState() // Şu anki navigation state'ini reactive olarak al
    val currentRoute = navBackStackEntry?.destination?.route // Şu anki route adresini al (örn: "home", "stats/0")
    
    Scaffold( // Ana sayfa yapısını oluştur (alt menü + içerik)
        bottomBar = { // Alt menü alanını tanımla
            HealthBottomNavigation( // Alt menü componentini göster
                currentRoute = currentRoute, // Şu anki route'u ver (seçili tab'ı belirlemek için)
                onNavigate = { route -> // Tab'a tıklandığında çalışacak fonksiyon
                    when (route) { // Hangi route'a tıklandığına göre farklı davranış
                        BottomNavItem.Home.route -> { // Ana sayfa tab'ına tıklandıysa
                            // Anasayfaya giderken stats sayfasından çık
                            if (currentRoute?.startsWith("stats/") == true) { // Eğer stats sayfasındaysak
                                navController.navigate(route) { // Ana sayfaya git
                                    popUpTo("stats/0") { // Stats sayfalarını back stack'ten çıkar
                                        inclusive = true // stats/0'ı da dahil et
                                    }
                                    launchSingleTop = true // Aynı sayfadan birden fazla oluşturma
                                }
                            } else { // Stats sayfasında değilsek
                                navController.navigate(route) { // Ana sayfaya git
                                    popUpTo(navController.graph.startDestinationId) { // İlk sayfaya kadar temizle
                                        saveState = true // Sayfa durumunu kaydet
                                    }
                                    launchSingleTop = true // Aynı sayfadan birden fazla oluşturma
                                    restoreState = true // Sayfa durumunu geri yükle
                                }
                            }
                        }
                        BottomNavItem.Stats.route -> { // İstatistikler tab'ına tıklandıysa
                            // Stats sayfasına git
                            navController.navigate(route) { // İstatistikler sayfasına git
                                popUpTo(navController.graph.startDestinationId) { // İlk sayfaya kadar temizle
                                    saveState = true // Sayfa durumunu kaydet
                                }
                                launchSingleTop = true // Aynı sayfadan birden fazla oluşturma
                                restoreState = true // Sayfa durumunu geri yükle
                            }
                        }
                        else -> { // Diğer tab'lara tıklandıysa (Profil)
                            navController.navigate(route) { // İlgili sayfaya git
                                popUpTo(navController.graph.startDestinationId) { // İlk sayfaya kadar temizle
                                    saveState = true // Sayfa durumunu kaydet
                                }
                                launchSingleTop = true // Aynı sayfadan birden fazla oluşturma
                                restoreState = true // Sayfa durumunu geri yükle
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding -> // Scaffold'dan gelen padding (alt menü için yer)
        NavHost( // Ana navigation container'ı
            navController = navController, // Navigation controller'ı ver
            startDestination = BottomNavItem.Home.route, // İlk sayfa: Ana sayfa ("home")
            modifier = Modifier.padding(innerPadding) // Alt menü için padding ekle
        ) { // Route tanımlarını başlat
            composable(BottomNavItem.Home.route) { // "home" route'u için sayfa tanımı
                HomeScreen( // Ana sayfa componentini göster
                    onNavigateToStats = { selectedTab -> // Ana sayfadan istatistiklere geçiş fonksiyonu
                        navController.navigate("stats/$selectedTab") { // Belirli tab ile stats sayfasına git
                            // Stats sayfasına giderken anasayfayı back stack'ten çıkar
                            popUpTo(BottomNavItem.Home.route) { // Ana sayfayı back stack'ten çıkar
                                saveState = true // Ana sayfa durumunu kaydet
                            }
                            // Yeni bir instance oluştur
                            launchSingleTop = true // Aynı sayfadan birden fazla oluşturma
                            // State'i geri yükle
                            restoreState = true // Sayfa durumunu geri yükle
                        }
                    }
                )
            }
            
            composable( // Parametreli route tanımı
                route = "stats/{selectedTab}", // Route parametresi: hangi tab seçili olacak
                arguments = listOf( // Route parametrelerini tanımla
                    navArgument("selectedTab") { // "selectedTab" parametresi
                        type = androidx.navigation.NavType.IntType // Parametre tipi: sayı
                        defaultValue = 0 // Varsayılan değer: 0 (ilk tab)
                    }
                )
            ) { backStackEntry -> // Route parametrelerini al
                val selectedTab = backStackEntry.arguments?.getInt("selectedTab") ?: 0 // Seçili tab numarasını al
                StatsScreen( // İstatistikler sayfasını göster
                    initialSelectedTab = selectedTab, // Başlangıçta hangi tab seçili olacak
                    onNavigateBack = { // Geri butonuna tıklandığında
                        navController.navigate(BottomNavItem.Home.route) { // Ana sayfaya git
                            popUpTo("stats/$selectedTab") { // Stats sayfasını back stack'ten çıkar
                                inclusive = true // Bu sayfayı da dahil et
                            }
                            launchSingleTop = true // Aynı sayfadan birden fazla oluşturma
                        }
                    }
                )
            }
            
            composable(BottomNavItem.Stats.route) { // "stats/0" route'u için sayfa tanımı (alt menüden gelince)
                StatsScreen( // İstatistikler sayfasını göster
                    initialSelectedTab = 0, // İlk tab seçili
                    onNavigateBack = { // Geri butonuna tıklandığında
                        navController.navigate(BottomNavItem.Home.route) { // Ana sayfaya git
                            popUpTo(BottomNavItem.Stats.route) { // Stats sayfasını back stack'ten çıkar
                                inclusive = true // Bu sayfayı da dahil et
                            }
                            launchSingleTop = true // Aynı sayfadan birden fazla oluşturma
                        }
                    }
                )
            }
            
            composable(BottomNavItem.Profile.route) { // "profile" route'u için sayfa tanımı
                ProfileScreen( // Profil sayfasını göster
                    onNavigateToEdit = { // "Profili Düzenle" butonuna tıklandığında
                        navController.navigate("profile_edit") // Profil düzenleme sayfasına git
                    },
                    onNavigateToGoalsEdit = { // "Hedefleri Düzenle" butonuna tıklandığında
                        navController.navigate("goals_edit") // Hedef düzenleme sayfasına git
                    },
                    onLanguageChanged = onLanguageChanged // Dil değişimi callback'ini geçir
                )
            }
            
            composable("profile_edit") { // "profile_edit" route'u için sayfa tanımı
                ProfileEditScreen( // Profil düzenleme sayfasını göster
                    onNavigateBack = { // "Geri" veya "Kaydet" butonuna tıklandığında
                        navController.popBackStack() // Önceki sayfaya dön (Profil sayfası)
                    }
                )
            }
            
            composable("goals_edit") { // "goals_edit" route'u için sayfa tanımı
                GoalsEditScreen( // Hedef düzenleme sayfasını göster
                    onNavigateBack = { // "Geri" veya "Kaydet" butonuna tıklandığında
                        navController.popBackStack() // Önceki sayfaya dön (Profil sayfası)
                    }
                )
            }
        }
    }
} 