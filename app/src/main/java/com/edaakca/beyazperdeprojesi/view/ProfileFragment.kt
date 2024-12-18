package com.edaakca.beyazperdeprojesi.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.edaakca.beyazperdeprojesi.R
import com.edaakca.beyazperdeprojesi.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        auth = FirebaseAuth.getInstance() // FirebaseAuth örneği oluştur

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.kayitButton.setOnClickListener {kayitOl(it)}
        binding.girisButton.setOnClickListener {girisYap(it) }

        val guncelKullanici=auth.currentUser
        if(guncelKullanici!=null)
        {
            //Kullanıcı daha önce giriş yapmış
            val action=ProfileFragmentDirections.actionProfileFragmentToKullaniciFragment()
            Navigation.findNavController(view).navigate(action)
        }

    }
    fun kayitOl(view: View){
        val email=binding.emailText.text.toString()
        val password=binding.passwordText.text.toString()
        if(email.isNotEmpty() && password.isNotEmpty())
        {
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task->
                if(task.isSuccessful){

                    // Kayıt başarılı, SharedPreferences'a giriş yapıldığını kaydediyoruz
                    val sharedPreferences = requireContext().getSharedPreferences("userPrefs", AppCompatActivity.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("isLoggedIn", true)  // Kullanıcı giriş yaptı
                    editor.apply()

                    Toast.makeText(requireContext(), "Kayıt başarılı! ", Toast.LENGTH_LONG).show()
                    val action = ProfileFragmentDirections.actionProfileFragmentToKullaniciFragment()
                    Navigation.findNavController(view).navigate(action)
                }
                else{
                    // Kayıt başarısızsa hata mesajı göster
                    Toast.makeText(requireContext(), "Kayıt başarısız: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener { exception->
                // Hata durumunda kullanıcıya hata mesajı göster
                Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }else{
            // E-posta ve şifre boşsa uyarı göster
            Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show()
        }

    }
    fun girisYap(view: View){

        val email=binding.emailText.text.toString()
        val password=binding.passwordText.text.toString()
        if(email.isNotEmpty() && password.isNotEmpty()){
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {

                // Giriş başarılı olduğunda SharedPreferences'a kaydediyoruz
                val sharedPreferences = requireContext().getSharedPreferences("userPrefs", AppCompatActivity.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("isLoggedIn", true)  // Kullanıcı giriş yaptı
                editor.apply()

                val action=ProfileFragmentDirections.actionProfileFragmentToKullaniciFragment()
                Navigation.findNavController(view).navigate(action)

            }.addOnFailureListener { exception->
                Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_LONG).show()

            }
        }else {
            // E-posta veya şifre boşsa uyarı göster
            Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show()
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
