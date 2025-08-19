## Test Altyapısı (Unit)

- libs.versions.toml içine unit test bağımlılık versiyonları eklendi: mockk, coroutines-test, turbine, truth.
- app/build.gradle.kts içine unit test bağımlılıkları eklendi.
- Bir sonraki adım: MainDispatcherRule ve örnek unit test dosyaları oluşturulacak.

### Eklenen Unit Testler
- GetTodayHealthDataUseCaseTest, UpdateStepsUseCaseTest
- HomeViewModelTest, StatsViewModelTest, ProfileViewModelTest, MainViewModelTest
- CalorieCalculatorTest

# Proje Görev Günlüğü

## 2025-08-12 — Splash Screen Entegrasyonu

### Sorun ve Çözüm
- **Sorun**: Android 12+ SplashScreen API özelliklerinin (`windowSplashScreenIconSize`, `windowSplashScreenAnimationDuration`) mevcut hedef API'da tanınmaması.
- **Çözüm**: Hibrit yaklaşım - manifest splash + Compose splash kombinasyonu.

### Yapılan Değişiklikler

#### 1. Tema Konfigürasyonu (`app/src/main/res/values/themes.xml`)
```xml
<style name="Theme.Stepapp.Splash" parent="Theme.Stepapp">
    <item name="android:windowBackground">@drawable/splash_background</item>
    <item name="android:windowNoTitle">true</item>
    <item name="android:windowActionBar">false</item>
    <item name="android:windowFullscreen">true</item>
</style>
```

#### 2. Splash Background (`app/src/main/res/drawable/splash_background.xml`)
- Beyaz arka plan + ortalanmış `@drawable/splash` görseli
- `layer-list` ile `gravity="center"` kullanımı

#### 3. Compose Splash Ekranı (`app/src/main/java/com/artes/securehup/stepapp/ui/screen/SplashScreen.kt`)
- 2 saniyelik basit splash ekranı
- `LaunchedEffect` ile otomatik geçiş

#### 4. MainActivity Entegrasyonu
- Manifest tema: `@style/Theme.Stepapp.Splash`
- Compose içinde koşullu splash gösterimi
- `keepSplashForMinTime` flag'i ile minimum 2 saniye görünürlük

### Teknik Notlar
- **Compat Kütüphanesi**: `androidx.core:core-splashscreen` bağımlılığı korundu
- **Fallback Yaklaşım**: Sistem splash çalışmazsa Compose splash devreye girer
- **Performance**: Manifest splash anında görünür, Compose splash sorunsuz geçiş sağlar

### Final Çözüm - Sadece Manifest Splash
- ✅ Compose splash kaldırıldı
- ✅ `instant_splash.xml` ile 200dp boyutunda logo
- ✅ `android:gravity="center"` ile tam ortalama
- ✅ Anında görünür, performanslı
- ✅ Tek splash sistemi

### Test Edilmesi Gerekenler
- [x] Beyaz ekran elimine edildi
- [x] Logo tam ortada ve doğru boyutta
- [x] Tek splash gösterimi
- [ ] Farklı ekran yoğunluklarında test

### Teknik Detaylar
- Manifest splash ile anında yükleme
- `setTheme()` ile normal tema'ya geçiş  
- 200dp logo boyutu (Compose ile aynı)
- Performans optimizasyonu

## 2025-01-12 — Çoklu Dil Desteği (i18n) Entegrasyonu

### Yapılan Değişiklikler

#### 1. String Kaynakları Oluşturuldu
- **Türkçe**: `/app/src/main/res/values/strings.xml`
- **İngilizce**: `/app/src/main/res/values-en/strings.xml`
- Navigation, Profile, Settings, Common ve Motivation mesajları için string kaynaları eklendi

#### 2. Dil Yönetimi Altyapısı
- **LanguageManager**: Dil değişimi ve uygulama işlemleri için singleton sınıf
- **PreferencesManager**: Kullanıcı tercihlerini kalıcı olarak saklama
- **Language** data class: Mevcut dilleri temsil eden model

#### 3. ProfileViewModel Güncellemeleri
- Dil yönetimi fonksiyonları eklendi
- `changeLanguage()`, `getCurrentLanguage()` ve `getAvailableLanguages()` fonksiyonları
- UI state'ine `languageChanged` flag'i eklendi

#### 4. ProfileScreen Dil Seçimi
- Dil seçenekleri menü öğesi aktivleştirildi
- `LanguageSelectionDialog` komponenti eklendi
- RadioButton ile dil seçimi arayüzü
- Real-time dil değişikliği callback'i

#### 5. MainActivity Dil Yönetimi
- `attachBaseContext()` ile uygulama başlatılmadan dil uygulanması
- Activity yeniden başlatma (`recreate()`) dil değişikliği için
- `applyLanguageToContext()` helper fonksiyonu

#### 6. Navigation Güncellemeleri
- `AppNavigation` dil değişikliği callback'i
- `BottomNavigation` string resource ID tabanlı yapı
- ProfileScreen'e dil değişikliği event forwarding

#### 7. UI String Kaynaklarına Geçiş
- ProfileScreen: Tüm hard-coded stringler kaldırıldı
- HomeScreen: Ana başlık ve stat card başlıkları güncellendi
- BottomNavigation: Tab isimleri için string resource kullanımı

### Teknik Uygulama Detayları

#### Dil Değişimi Akışı
1. Kullanıcı Profile > Dil Seçenekleri'ne tıklar
2. Dialog açılır ve kullanıcı dil seçer
3. LanguageManager tercih kaydeder
4. ProfileViewModel UI state günceller
5. ProfileScreen callback tetikler
6. MainActivity activity'yi yeniden başlatır
7. `attachBaseContext()` kayıtlı dili uygular

#### Kalıcı Dil Tercihi
- SharedPreferences ile saklanır (`step_app_prefs`)
- Uygulama kapatılıp açıldığında korunur
- Sistem dili değişse bile kullanıcı tercihi öncelikli

#### String Resource Yapısı
```
values/strings.xml      -> Türkçe (varsayılan)
values-en/strings.xml   -> İngilizce
```

### Test Edilmesi Gerekenler
- [x] Dil seçimi dialog'u çalışıyor
- [x] Seçilen dil anında uygulanıyor
- [x] Uygulama yeniden başlatıldığında dil korunuyor
- [x] Tüm ekranlarda string kaynakları kullanılıyor
- [ ] Farklı cihazlarda test edilmeli
- [ ] Sistem dili değişikliği testi

### ✅ Ek Güncellemeler (2025-01-12)

#### Kalan Ekranların Dil Desteği Tamamlandı
- **StatsScreen**: Tüm tab isimleri ve motivasyonel mesajlar string resource'lara geçirildi
- **ProfileEditScreen**: Form başlıkları, label'lar ve buton metinleri güncellendi
- **GoalsEditScreen**: Hedef kartları, açıklamalar ve öneriler dinamik hale getirildi

#### String Kaynakları Genişletildi
- Motivasyonel mesajlar için ayrı string kategorisi
- Edit ekranları için özel string'ler
- Goal öneri metinleri ve birimler

#### Final Düzeltmeler
- **Progress Metinleri**: "Hedefinizin %'i" artık dinamik string resource kullanıyor
- **Birim Metinleri**: StatsScreen'deki "adım", "kcal", "km", "dk" birimler dinamik
- **String Format**: Parametreli string kullanımı (%1$s) İngilizce uyumlu
- **Trend Başlığı**: "Steps Trendi" → "${Steps} ${Trend}" (Dinamik)
- **Gün Kısaltmaları**: Pt, Sa, Ça, Pe, Cu, Ct, Pz → Mo, Tu, We, Th, Fr, Sa, Su (Dile göre)
- **No Data Message**: "Henüz veri yok" → "No data yet" (Dinamik)
- **Tarih Formatı**: "12 Ağustos 2025" → "August 12, 2025" (Locale göre)
- **Current Locale**: Context'ten dinamik locale alınması
- **Motivasyon Mesajları**: HomeScreen'deki tüm motivasyon metinleri dinamik
- **buildMotivationMessage → getMotivationMessage**: Composable fonksiyona dönüştürme
- **Active Time Kart Boyutu**: Diğer kartlarla aynı boyutta olacak şekilde düzeltildi
- **Responsive Tasarım**: Ekran boyutuna göre kart ve text boyutları ayarlanıyor
- **Bottom Bar Overflow**: Kartların bottom bar'ın altına geçme sorunu çözüldü
- **Scroll Desteği**: Scroll kaldırıldı, kartlar ekrana sığdırıldı

## 2025-08-18 — Onboarding Ekranları: Scroll Kaldırma ve Alt Sabit Butonlar

### Sorun
- Onboarding ekranlarında gereksiz `verticalScroll` kullanımı vardı; butonlar sayfa sonunda scroll içinde kalıyordu.

### Çözüm
- Tüm onboarding ekranları `Scaffold` ile yapılandırıldı, alt sabit butonlar `bottomBar` içinde konumlandırıldı.
- İçerik üst-orta-alt bölgeler arasında `Spacer(weight = 1f)` ile esnetilerek responsive yerleşim sağlandı.
- Sistem çubukları ve klavye için `WindowInsets.safeDrawing` ve `imePadding()` kullanıldı.

### Değişen Dosyalar
- `app/src/main/java/com/artes/securehup/stepapp/ui/screen/onboarding/WelcomeScreen.kt`
  - `verticalScroll` kaldırıldı
  - `Scaffold` + `bottomBar` eklendi (Başla butonu ve hint)
  - Progress bar içerik alanının en altında, butondan ayrı konumlandırıldı
- `app/src/main/java/com/artes/securehup/stepapp/ui/screen/onboarding/PersonalInfoScreen.kt`
  - `Scaffold` + alt sabit Devam Et butonu eklendi
  - Form alanları esnek yerleşimle düzenlendi, scroll kaldırıldı
  - `WindowInsets.safeDrawing` ve `imePadding()` ile inset yönetimi
  - Uyarılar için `@Suppress("UNUSED_PARAMETER")` eklendi
- `app/src/main/java/com/artes/securehup/stepapp/ui/screen/onboarding/GoalsScreen.kt`
  - `Scaffold` + alt sabit Kaydet butonu eklendi
  - Hedef tipleri ve kart listesi scroll olmadan sığacak şekilde aralıklarla düzenlendi
  - `@Suppress("UNUSED_PARAMETER")` ile kullanılmayan parametre uyarısı bastırıldı

### Teknik Notlar
- Alt butonlar `RoundedCornerShape(cornerRadiusXLarge)` ile görsel tutarlılık sağlıyor.
- Progress bar'larda gereksiz `clip` kaldırıldı; performans ve sadeleşme.
- Build: `./gradlew :app:assembleDebug` başarılı (uyarılar yalnızca deprecated API kullanımları).

### Test
- [x] Küçük ekranlarda butonların görünürlüğü
- [x] Klavye açıldığında butonların taşmaması (`imePadding`)
- [x] Dark/Light tema ile kontrast
- [ ] Farklı ekran yoğunlukları

## 2025-08-18 — Stats Takvimi: İlk Açılışta Bugüne Odak

- `StatsScreen -> DateSelector` içinde `rememberLazyListState` ve `LaunchedEffect(Unit)` eklendi.
- İlk render’da bugünün indeksi hesaplanıp `listState.scrollToItem(todayIndex)` ile LazyRow bugüne kaydırılıyor.
- Görsel durumlar (seçili/bugün) aynen korunuyor; sadece başlangıç odağı kullanıcı deneyimini iyileştiriyor.
- Not: Derlemede KSP kaynaklı “duplicate class” uyarıları görülürse `./gradlew clean assembleDebug` ile temiz derleme önerilir.
