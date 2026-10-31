<div align="center">

# 🎵 SimpleMusicPlayer

### Premium Offline Music Player for Android

*Minimalis · Elegan · Ringan · Tanpa Iklan*

[![Platform](https://img.shields.io/badge/Platform-Android%206.0%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://www.android.com)
[![Language](https://img.shields.io/badge/Language-Java%207-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com)
[![License](https://img.shields.io/badge/License-MIT-10B981?style=for-the-badge)](LICENSE)
[![Release](https://img.shields.io/badge/Release-v1.0-10B981?style=for-the-badge)](https://github.com/Ikyletwar/SimpleMusicPlayer/releases)

</div>

---

## 📖 Tentang Proyek

**SimpleMusicPlayer** adalah aplikasi pemutar musik offline untuk Android yang dirancang dengan filosofi **"less is more"**. Dibangun dari nol menggunakan **Java murni** dan **Android SDK bawaan**, tanpa library eksternal, tanpa framework berat, dan tanpa dependensi jaringan.

Fokus utama proyek ini adalah memberikan pengalaman mendengarkan musik yang **bersih, cepat, dan menyenangkan** — dengan UI modern bergaya aplikasi fintech yang premium, namun tetap ringan di perangkat kelas menengah ke bawah.

> *"Dibuat untuk mereka yang percaya bahwa aplikasi musik tidak harus ribet untuk terasa mewah."*

---

## ✨ Fitur Utama

<table>
<tr>
<td width="50%">

### 🎧 Playback
- ✅ Putar audio dari penyimpanan lokal
- ✅ Background playback (via Foreground Service)
- ✅ Kontrol dari layar kunci & notifikasi
- ✅ Seek manual (-10s / +10s)
- ✅ Auto-pause saat ada panggilan masuk (Audio Focus)

</td>
<td width="50%">

### 🎨 Antarmuka
- ✅ Dark Mode & Light Mode
- ✅ UI bergaya fintech premium
- ✅ Animasi fade yang halus
- ✅ Haptic feedback pada sentuhan
- ✅ Ripple effect pada list

</td>
</tr>
<tr>
<td width="50%">

### 📂 Manajemen File
- ✅ Pemilihan lagu via **SAF** (Storage Access Framework)
- ✅ Tanpa perlu izin storage yang membingungkan
- ✅ Kompatibel dengan Android 6 hingga Android 14
- ✅ Dukungan format MP3, WAV, AAC, M4A, OGG

</td>
<td width="50%">

### 🔔 Notifikasi
- ✅ Notifikasi kustom (RemoteViews)
- ✅ Tombol Play / Pause interaktif
- ✅ Progress bar real-time
- ✅ Kontrol seek tanpa buka aplikasi
- ✅ Tampil di lock screen

</td>
</tr>
</table>

---

## 🏗️ Arsitektur

```
SimpleMusicPlayer/
│
├── app/
│   └── src/main/
│       ├── java/com/testing/
│       │   ├── GlobalApplication.java    # Crash handler & global state
│       │   ├── MainActivity.java         # UI utama & logika interaksi
│       │   └── MusicService.java         # Foreground service & playback
│       │
│       ├── res/
│       │   ├── drawable/                 # Shape & background custom
│       │   ├── layout/                   # Layout XML (activity, dialog, notif)
│       │   └── values/                   # Warna, style, string
│       │
│       └── AndroidManifest.xml           # Deklarasi permission & komponen
│
└── build.gradle                          # Konfigurasi build
```

### 🔩 Komponen Inti

| Komponen | Peran |
|---|---|
| `MainActivity` | Menampilkan UI, menangkap aksi user (play/pause/seek/pick) |
| `MusicService` | Menjalankan `MediaPlayer` di background, mengelola notifikasi |
| `NotificationReceiver` | Menerima broadcast dari tombol notifikasi |
| `GlobalApplication` | Menangkap crash, logging ke file, menampilkan `CrashActivity` |

---

## 🚀 Instalasi

### Untuk Pengguna (via APK)

1. Buka halaman [**Releases**](https://github.com/Ikyletwar/SimpleMusicPlayer/releases)
2. Download file `app.apk`
3. Izinkan **"Install from unknown sources"** jika diminta
4. Buka aplikasi → tekan **PILIH LAGU** → pilih file audio dari HP

### Untuk Developer (via Source Code)

```bash
# 1. Clone repository
git clone https://github.com/Ikyletwar/SimpleMusicPlayer.git
cd SimpleMusicPlayer

# 2. Buka di Android Studio / AIDE
#    - Android Studio: File → Open → pilih folder project
#    - AIDE: Import Project → pilih folder yang sama

# 3. Build
./gradlew assembleDebug

# 4. Install ke device
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 📱 Spesifikasi Teknis

| Kategori | Detail |
|---|---|
| Bahasa | Java 7 (tanpa lambda, tanpa stream API) |
| Min SDK | 16 (Android 4.1) |
| Target SDK | 26 (Android 8.0) |
| Compile SDK | 30 (Android 11) |
| Build Tools | AGP 7.0.2 · Gradle 7.x |
| Library | AndroidX AppCompat, Material Components |
| Ukuran APK | ~2 MB |
| Dependensi Jaringan | ❌ Tidak ada (100% offline) |

---

## 🔐 Privasi

Kami percaya bahwa aplikasi musik tidak perlu mengakses internet untuk memutar file lokal. Karena itu:

- ✅ Tidak ada analytics / tracking
- ✅ Tidak ada iklan
- ✅ Tidak ada permission internet
- ✅ Tidak ada pengumpulan data pengguna
- ✅ Semua pemutaran terjadi 100% di perangkat

---

## 🤝 Kontribusi

Kontribusi sangat diterima! Baik berupa:

- 🐛 Laporan bug → buka Issues
- 💡 Saran fitur → buka Discussions
- 🔧 Pull request → fork repo, buat branch baru, ajukan PR

Panduan singkat:

```bash
# Fork repo, lalu:
git clone https://github.com/USERNAME/SimpleMusicPlayer.git
cd SimpleMusicPlayer
git checkout -b fitur-baru
# ... lakukan perubahan ...
git commit -m "Add: fitur baru"
git push origin fitur-baru
# Buka Pull Request di GitHub
```

---

## 📄 Lisensi

Proyek ini dilisensikan di bawah MIT License — bebas digunakan, dimodifikasi, dan didistribusikan, dengan tetap mencantumkan kredit kepada pembuat asli.

Lihat file LICENSE untuk detail lengkap.

---

## 👤 Author

<div align="center">

**Hizkia Letwar**

Siswa TJKT 1 — SMK Negeri 1 Maluku Tengah

[![GitHub](https://img.shields.io/badge/GitHub-Ikyletwar-181717?style=for-the-badge&logo=github)](https://github.com/Ikyletwar)

"Alasan diciptakan: gabut. Alasan dilanjutkan: karena seru."

</div>

---

## 🙏 Ucapan Terima Kasih

- Google — untuk Android SDK yang luar biasa
- JetBrains — untuk IntelliJ IDEA & AIDE
- Komunitas GitHub — untuk inspirasi & referensi
- Kamu — yang sudah mau mencoba dan membaca sampai sini 💚 (AI yg tulis bkn krn gw gay klo yg baca cwo)

---

<div align="center">

⭐ Jika proyek ini bermanfaat, jangan lupa beri bintang!

</div>
