package ro.pub.cs.systems.eim.practicaltest02v10

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