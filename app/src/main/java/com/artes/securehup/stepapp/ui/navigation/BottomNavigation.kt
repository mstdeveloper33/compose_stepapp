package com.artes.securehup.stepapp.ui.navigation // Package tanımı - hangi klasörde olduğunu belirtir

import androidx.compose.material.icons.Icons // Material Design icon'larını kullanmak için
import androidx.compose.material.icons.filled.* // Dolu icon'ları (Home, Person vs.) import eder
import androidx.compose.material3.* // Material 3 UI componentlerini import eder
import androidx.compose.runtime.* // Compose runtime fonksiyonlarını import eder
import androidx.compose.ui.graphics.vector.ImageVector // Icon tipi için gerekli
import androidx.compose.ui.res.stringResource
import com.artes.securehup.stepapp.R

sealed class BottomNavItem( // Alt menü öğelerini tanımlayan sınıf (sealed = sadece bu dosyada extend edilebilir)
    val route: String, // Sayfa adresi (örn: "home", "profile")
    val titleRes: Int, // Tab'da görünecek yazının string resource ID'si
    val icon: ImageVector // Tab'da görünecek icon (örn: ev ikonu)
) {
    object Home : BottomNavItem( // Ana sayfa tab'ı tanımı
        route = "home", // Ana sayfa route'u
        titleRes = R.string.nav_home, // Tab'da "Ana Sayfa" yazısı görünür
        icon = Icons.Default.Home // Ev ikonu görünür
    )

    object Stats : BottomNavItem( // İstatistikler tab'ı tanımı
        route = "stats/0", // İstatistikler route'u (0 = ilk tab)
        titleRes = R.string.nav_stats, // Tab'da "İstatistikler" yazısı görünür
        icon = Icons.Default.DateRange // Takvim ikonu görünür
    )

    object Profile : BottomNavItem( // Profil tab'ı tanımı
        route = "profile", // Profil route'u
        titleRes = R.string.nav_profile, // Tab'da "Profil" yazısı görünür
        icon = Icons.Default.Person // Kişi ikonu görünür
    )
}

@Composable // Bu fonksiyon UI componenti olduğunu belirtir
fun HealthBottomNavigation( // Alt menü UI componentinin ana fonksiyonu
    currentRoute: String?, // Şu anda hangi sayfada olduğumuz (seçili tab'ı belirlemek için)
    onNavigate: (String) -> Unit // Tab'a tıklandığında çalışacak fonksiyon
) {
    val items = listOf( // Tab'ların listesini oluştur
        BottomNavItem.Home, // Ana sayfa tab'ını listeye ekle
        BottomNavItem.Stats, // İstatistikler tab'ını listeye ekle
        BottomNavItem.Profile // Profil tab'ını listeye ekle
    )

    NavigationBar { // Material 3 alt menü componentini oluştur
        items.forEach { item -> // Her tab için döngü başlat
            NavigationBarItem( // Her tab için NavigationBarItem oluştur
                icon = { // Tab'ın iconunu tanımla
                    Icon( // Icon componentini oluştur
                        imageVector = item.icon, // Tab'ın iconunu al (örn: Home ikonu)
                        contentDescription = stringResource(item.titleRes) // Erişebilirlik için icon açıklaması
                    )
                },
                label = { // Tab'ın yazısını tanımla
                    Text(text = stringResource(item.titleRes)) // Tab'ın ismini göster (örn: "Ana Sayfa")
                },
                selected = when { // Tab'ın seçili olup olmadığını kontrol et
                    item == BottomNavItem.Home -> currentRoute == item.route // Ana sayfa tab'ı: route tam eşleşmeli
                    item == BottomNavItem.Stats -> currentRoute?.startsWith("stats/") == true // İstatistikler tab'ı: "stats/" ile başlayan herhangi bir route
                    else -> currentRoute == item.route // Diğer tab'lar: route tam eşleşmeli
                },
                onClick = { // Tab'a tıklandığında ne olacağını tanımla
                    onNavigate(item.route) // İlgili route'a git (örn: "home", "stats/0", "profile")
                }
            )
        }
    }
} 