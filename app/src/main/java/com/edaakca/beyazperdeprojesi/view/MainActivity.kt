package com.edaakca.beyazperdeprojesi.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.edaakca.beyazperdeprojesi.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // bottomNavigationView öğesini doğru şekilde başlatıyoruz
        bottomNavigationView = findViewById(R.id.bottomNavigation)

        navController = findNavController(R.id.nav_host_fragment)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)




        // Bottom navigation menüye tıklama dinleyicisi ekleyin
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.anasayfa -> {
                    navController.navigate(R.id.anasayfaFragment)
                    true
                }
                R.id.filmler -> {
                    navController.navigate(R.id.filmlerFragment)
                    true
                }
                R.id.arama -> {
                    navController.navigate(R.id.aramaFragment)
                    true
                }
                R.id.favoriler -> {
                    if (kullaniciGirisKontrol()) {
                        navController.navigate(R.id.favorilerFragment) // Giriş yapılmışsa git
                        true
                    } else {
                        navController.navigate(R.id.girisMessageFragment)
                        false

                    }
                }
                R.id.profil -> {
                    navController.navigate(R.id.profileFragment)
                    true
                }
                else -> false
            }
        }

        // Giriş durumunu kontrol et ve favoriler sekmesini aktifleştir veya devre dışı bırak
        checkGirisDurumu()
        // İlk açıldığında Anasayfa fragmentını göster
        navController.navigate(R.id.anasayfaFragment)
    }

    // Kullanıcı giriş kontrolü
    private fun kullaniciGirisKontrol(): Boolean {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser != null
    }

    // Giriş durumu kontrol edilerek favoriler sekmesi devre dışı bırakılır
    private fun checkGirisDurumu() {
        if (kullaniciGirisKontrol()) {
            // Giriş yapılmışsa, favoriler sekmesi aktif
            bottomNavigationView.menu.findItem(R.id.favoriler).isEnabled = true
        } else {
            // Giriş yapılmamış

        }
    }

}
