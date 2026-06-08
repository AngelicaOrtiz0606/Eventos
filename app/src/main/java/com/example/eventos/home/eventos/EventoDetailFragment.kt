package com.example.eventos.home.eventos

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.eventos.R
import com.example.eventos.core.model.Evento
import com.example.eventos.core.network.UnsafeOkHttpClient
import com.example.eventos.databinding.FragmentEventoDetailBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.Request

class EventoDetailFragment : Fragment() {

    private var _binding: FragmentEventoDetailBinding? = null
    private val binding get() = _binding!!
    private val unsafeClient = UnsafeOkHttpClient.getUnsafeOkHttpClient()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    private var imageBytes: ByteArray? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventoDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        @Suppress("DEPRECATION")
        val evento = arguments?.getParcelable<Evento>("evento")
        evento?.let { setupUI(it) }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupUI(evento: Evento) {
        binding.tvDetailTitle.text = evento.titulo
        binding.tvDetailDescription.text = evento.descripcion
        binding.tvDetailType.text = evento.tipo.uppercase()

        // Verificar si ya está inscrito
        checkIfAlreadyRegistered(evento)

        // 1. Botón Inscribirse
        binding.btnSignUp.setOnClickListener {
            registerForEvent(evento)
        }

        // 2. Botón Calendario
        binding.btnAddCalendar.setOnClickListener {
            addToCalendar(evento)
        }

        // 3. Botón Ver Imagen Full
        binding.btnViewImage.setOnClickListener {
            showFullImage()
        }

        binding.btnCloseFullImage.setOnClickListener {
            binding.fullImageContainer.isVisible = false
        }

        // Carga de imagen con el cliente seguro para saltar errores de SSL
        if (evento.imagenUrl.isNotEmpty()) {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val bytes = withContext(Dispatchers.IO) {
                        val request = Request.Builder()
                            .url(evento.imagenUrl)
                            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                            .build()
                        
                        unsafeClient.newCall(request).execute().use { response ->
                            if (!response.isSuccessful) null else response.body?.bytes()
                        }
                    }

                    imageBytes = bytes
                    if (bytes != null) {
                        Glide.with(this@EventoDetailFragment)
                            .load(bytes)
                            .placeholder(R.drawable.fca)
                            .error(R.drawable.fca)
                            .centerCrop()
                            .into(binding.ivEventoDetail)
                    }
                } catch (e: Exception) {
                    Log.e("DetailLoad", "Error imagen: ${evento.imagenUrl}", e)
                    binding.ivEventoDetail.setImageResource(R.drawable.fca)
                }
            }
        } else {
            binding.ivEventoDetail.setImageResource(R.drawable.fca)
        }
    }

    private fun checkIfAlreadyRegistered(evento: Evento) {
        val user = auth.currentUser ?: return
        
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val result = db.collection("inscripciones")
                    .whereEqualTo("userId", user.uid)
                    .whereEqualTo("eventoId", evento.id)
                    .get()
                    .await()
                
                if (!result.isEmpty) {
                    binding.btnSignUp.isEnabled = false
                    binding.btnSignUp.text = "YA ESTÁS INSCRITO"
                    binding.btnSignUp.alpha = 0.6f
                }
            } catch (e: Exception) {
                Log.e("CheckReg", "Error al verificar inscripción", e)
            }
        }
    }

    private fun registerForEvent(evento: Evento) {
        val user = auth.currentUser
        if (user == null) {
            Snackbar.make(binding.root, "Debes iniciar sesión para inscribirte", Snackbar.LENGTH_LONG).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Obtenemos el numero de cuenta del perfil del usuario
                val userDoc = db.collection("users").document(user.uid).get().await()
                val studentId = userDoc.getString("studentId") ?: ""

                val registration = hashMapOf(
                    "userId" to user.uid,
                    "userEmail" to user.email,
                    "studentId" to studentId,
                    "eventoId" to evento.id,
                    "eventoTitulo" to evento.titulo,
                    "fechaRegistro" to Timestamp.now()
                )

                binding.btnSignUp.isEnabled = false
                db.collection("inscripciones")
                    .add(registration)
                    .await()

                Snackbar.make(binding.root, "¡Inscripción exitosa!", Snackbar.LENGTH_LONG).show()
                binding.btnSignUp.text = "YA ESTÁS INSCRITO"
                binding.btnSignUp.alpha = 0.6f
            } catch (e: Exception) {
                binding.btnSignUp.isEnabled = true
                Snackbar.make(binding.root, "Error al inscribirse: ${e.message}", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun addToCalendar(evento: Evento) {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, evento.titulo)
            putExtra(CalendarContract.Events.DESCRIPTION, evento.descripcion)
            putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PUBLIC)
            putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
        }
        startActivity(intent)
    }

    private fun showFullImage() {
        if (imageBytes != null) {
            binding.fullImageContainer.isVisible = true
            Glide.with(this)
                .load(imageBytes)
                .into(binding.ivFullImage)
        } else {
            Toast.makeText(requireContext(), "Cargando imagen...", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
