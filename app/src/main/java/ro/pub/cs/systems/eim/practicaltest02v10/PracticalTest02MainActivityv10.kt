package ro.pub.cs.systems.eim.practicaltest02v10

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import ro.pub.cs.systems.eim.practicaltest02v10.Server
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket

class Server(private val port: Int) : Thread() {
    override fun run() {
        val serverSocket = ServerSocket(port)
        while (!isInterrupted) {
            val clientSocket = serverSocket.accept()
            handleClient(clientSocket)
        }
        serverSocket.close()
    }

    private fun handleClient(clientSocket: Socket) {
        val input = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
        val output: OutputStream = clientSocket.getOutputStream()

        val pokemonName = input.readLine()
        val response = fetchPokemonData(pokemonName)

        output.write(response.toByteArray())
        output.flush()
        clientSocket.close()
    }

    private fun fetchPokemonData(pokemonName: String): String {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://pokeapi.co/api/v2/pokemon/$pokemonName")
            .build()

        val response = client.newCall(request).execute()
        val json = JSONObject(response.body?.string() ?: "")
        val imageUrl = json.getJSONObject("sprites").getString("front_default")
        val abilities = json.getJSONArray("abilities")
        val types = json.getJSONArray("types")

        val abilitiesList = mutableListOf<String>()
        for (i in 0 until abilities.length()) {
            abilitiesList.add(abilities.getJSONObject(i).getJSONObject("ability").getString("name"))
        }

        val typesList = mutableListOf<String>()
        for (i in 0 until types.length()) {
            typesList.add(types.getJSONObject(i).getJSONObject("type").getString("name"))
        }

        return "Abilities: ${abilitiesList.joinToString(", ")}\nTypes: ${typesList.joinToString(", ")}\nImage URL: $imageUrl"
    }
}

class PracticalTest02MainActivityv10 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_practical_test02v10_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val pokemonNameEditText = findViewById<EditText>(R.id.pokemon_name)
        val portNumberEditText = findViewById<EditText>(R.id.port_number)
        val searchButton = findViewById<Button>(R.id.search_button)
        val pokemonImageView = findViewById<ImageView>(R.id.pokemon_image)
        val pokemonAbilitiesTextView = findViewById<TextView>(R.id.pokemon_abilities)
        val pokemonTypesTextView = findViewById<TextView>(R.id.pokemon_types)
        val navigateButton = findViewById<Button>(R.id.navigate_button)

        searchButton.setOnClickListener {
            val pokemonName = pokemonNameEditText.text.toString().trim()
            val portNumber = portNumberEditText.text.toString().trim().toIntOrNull()
            if (pokemonName.isNotEmpty() && portNumber != null) {
                // Start the server
                val server = Server(portNumber)
                server.start()

                // Call the API and update the UI
                fetchPokemonData(pokemonName, pokemonImageView, pokemonAbilitiesTextView, pokemonTypesTextView)
            }
        }

        navigateButton.setOnClickListener {
            val intent = Intent(this, FCMActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchPokemonData(pokemonName: String, imageView: ImageView, abilitiesTextView: TextView, typesTextView: TextView) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://pokeapi.co/api/v2/pokemon/$pokemonName")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body?.let { responseBody ->
                    val json = JSONObject(responseBody.string())
                    val imageUrl = json.getJSONObject("sprites").getString("front_default")
                    val abilities = json.getJSONArray("abilities")
                    val types = json.getJSONArray("types")

                    val abilitiesList = mutableListOf<String>()
                    for (i in 0 until abilities.length()) {
                        abilitiesList.add(abilities.getJSONObject(i).getJSONObject("ability").getString("name"))
                    }

                    val typesList = mutableListOf<String>()
                    for (i in 0 until types.length()) {
                        typesList.add(types.getJSONObject(i).getJSONObject("type").getString("name"))
                    }

                    runOnUiThread {
                        Glide.with(this@PracticalTest02MainActivityv10).load(imageUrl).into(imageView)
                        abilitiesTextView.text = abilitiesList.joinToString(", ")
                        typesTextView.text = typesList.joinToString(", ")
                    }
                }
            }
        })
    }
}