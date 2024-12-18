package com.edaakca.beyazperdeprojesi.view

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.edaakca.beyazperdeprojesi.databinding.FragmentKullaniciBinding
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.edaakca.beyazperdeprojesi.R
import com.edaakca.beyazperdeprojesi.adapter.MenuAdapter
import com.edaakca.beyazperdeprojesi.view.KullaniciFragmentDirections
import com.google.firebase.auth.FirebaseAuth

class KullaniciFragment : Fragment() {

    private var _binding: FragmentKullaniciBinding? = null
    private val binding get() = _binding!!

    // İzin istemek için launcher tanımlama
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // İzin verildi, bildirim gönderebilirsiniz
                sendNotification()
            } else {
                // İzin verilmedi
                Toast.makeText(requireContext(), "Bildirim izni verilmedi", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKullaniciBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bildirim izni kontrolü
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {  // Android 13 ve sonrası
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // İzin verilmemiş, izin istemek için başlatma
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                // İzin zaten verilmişse bildirim gönderilebilir
                sendNotification()
            }
        } else {
            // Android 13'ten önce, bildirim iznine gerek yok
            sendNotification()
        }

        val menuItems = listOf(
            Pair("Favorilerim", R.drawable.ic_favorite), // Başlık ve icon
            Pair("İzleme Listem", R.drawable.ic_list),
            Pair("Çıkış", R.drawable.ic_out)
        )


        val adapter = MenuAdapter(menuItems) { menuItem ->
            when (menuItem) {
                "Favorilerim" -> {
                    sendNotification() // Favori aktörlerinin yeni filmi çıktığında bildirim gönder
                    val action = KullaniciFragmentDirections.actionKullaniciFragmentToFavorilerFragment()
                    Navigation.findNavController(view).navigate(action)
                }
                "İzleme Listem" -> {

                    val action = KullaniciFragmentDirections.actionKullaniciFragmentToIzlemeListFragment()
                    Navigation.findNavController(view).navigate(action)

                }
                "Çıkış" -> {

                    FirebaseAuth.getInstance().signOut() // Kullanıcı oturumunu kapatıyor
                    Toast.makeText(requireContext(), "Çıkış yapıldı", Toast.LENGTH_SHORT).show()

                    // SharedPreferences'tan çıkışı kaydet
                    val sharedPreferences = requireContext().getSharedPreferences("userPrefs", AppCompatActivity.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("isLoggedIn", false)  // Çıkış yaptı
                    editor.apply()

                    // ProfileFragment'e yönlendir
                    val action = KullaniciFragmentDirections.actionKullaniciFragmentToProfileFragment()
                    Navigation.findNavController(view).navigate(action)
                }
            }
        }

        binding.menuRecyclerView.adapter = adapter
        binding.menuRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun sendNotification() {

        createNotificationChannel()
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(requireContext(), "film_notifications")
            .setSmallIcon(R.drawable.ic_notification) // Uygulamanızda uygun bir simge kullanın
            .setContentTitle("Yeni Film Çıktı!")
            .setContentText("Yeni filmi çıktı. Hemen izleyebilirsiniz!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(1, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Film Bildirimleri"
            val descriptionText = "Favori aktörlerinden yeni filmler hakkında bildirimler"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("film_notifications", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
