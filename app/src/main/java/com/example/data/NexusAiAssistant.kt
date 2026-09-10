package com.example.data

import com.example.BuildConfig
import com.example.model.AppLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object NexusAiAssistant {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    suspend fun getResponse(userMessage: String, language: AppLanguage): Pair<String, String?> {
        // First check if Gemini API key is configured
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val apiResult = callGeminiApi(userMessage, apiKey, language)
                if (apiResult.isNotBlank()) {
                    val navIntent = detectNavigationIntent(userMessage)
                    return Pair(apiResult, navIntent)
                }
            } catch (e: Exception) {
                // Fallback to local intelligent assistant
            }
        }

        return generateLocalResponse(userMessage, language)
    }

    private suspend fun callGeminiApi(prompt: String, apiKey: String, language: AppLanguage): String = withContext(Dispatchers.IO) {
        val langStr = when (language) {
            AppLanguage.PT -> "Português"
            AppLanguage.EN -> "English"
            AppLanguage.ES -> "Español"
        }
        val systemPrompt = "Você é o 'Nexus IA', o assistente inteligente integrado ao aplicativo social 'Nexus Chat'. O Nexus Chat conta com mensagens, áudios com player de onda, ligações de voz e vídeo com criptografia de ponta a ponta avançada, status efêmeros (24h) e status públicos, grupos, comunidades, feed de postagens social, e configurações completas. Responda de forma clara, amigável, prestativa e concisa no idioma $langStr. Se o usuário perguntar como navegar, explique exatamente onde clicar no app."

        val jsonBody = JSONObject().apply {
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().put(JSONObject().put("text", systemPrompt)))
            })
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().put(JSONObject().put("text", prompt)))
                })
            })
        }

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
            .post(jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
            .build()

        val response = httpClient.newCall(request).execute()
        val body = response.body?.string() ?: ""
        if (!response.isSuccessful) {
            return@withContext ""
        }

        val resJson = JSONObject(body)
        val candidates = resJson.optJSONArray("candidates")
        val content = candidates?.optJSONObject(0)?.optJSONObject("content")
        val parts = content?.optJSONArray("parts")
        parts?.optJSONObject(0)?.optString("text") ?: ""
    }

    private fun detectNavigationIntent(input: String): String? {
        val lower = input.lowercase()
        return when {
            lower.contains("conversa") || lower.contains("chat") || lower.contains("mensagem") -> "nav_chats"
            lower.contains("adicionar") || lower.contains("contato") || lower.contains("pessoa") || lower.contains("número") -> "action_add_person"
            lower.contains("grupo") -> "action_create_group"
            lower.contains("comunidade") -> "action_create_community"
            lower.contains("status") || lower.contains("story") || lower.contains("stories") -> "nav_status"
            lower.contains("feed") || lower.contains("post") || lower.contains("social") || lower.contains("publicação") -> "nav_feed"
            lower.contains("configura") || lower.contains("tema") || lower.contains("perfil") || lower.contains("idioma") -> "nav_settings"
            lower.contains("chamada") || lower.contains("ligação") || lower.contains("ligar") -> "nav_calls"
            else -> null
        }
    }

    private fun generateLocalResponse(message: String, language: AppLanguage): Pair<String, String?> {
        val lower = message.lowercase()
        val navIntent = detectNavigationIntent(message)

        val reply = when (language) {
            AppLanguage.PT -> when {
                lower.contains("adicionar") || lower.contains("pessoa") || lower.contains("contato") ->
                    "Para adicionar uma pessoa no Nexus Chat:\n1. Vá na aba 'Conversas'\n2. Toque no botão '+ Adicionar pessoa'\n3. Digite o número de telefone da pessoa (com DDD ou DDI).\n\nUma conversa criptografada será iniciada instantaneamente!"

                lower.contains("grupo") ->
                    "Para criar um grupo:\n1. Acesse a aba 'Conversas'\n2. Toque no botão 'Criar grupo'\n3. Dê um nome e descrição.\n\nTodos os participantes desfrutam de criptografia de ponta a ponta avançada!"

                lower.contains("comunidade") ->
                    "As Comunidades reúnem vários grupos e canais de avisos oficiais sob um mesmo guarda-chuva. Para criar uma, use o botão 'Criar comunidade' na aba Conversas ou acerte o filtro 'Comunidades'!"

                lower.contains("status") ->
                    "No Nexus Chat existem dois tipos de status:\n• Status Efêmeros: desaparecem em 24 horas para seus contatos.\n• Status Públicos: visíveis para toda a rede social do Nexus Chat.\n\nVá para a aba 'Status' para ver ou publicar o seu!"

                lower.contains("criptografia") || lower.contains("segurança") || lower.contains("privacidade") ->
                    "O Nexus Chat utiliza criptografia de ponta a ponta avançada em todas as mensagens de texto, gravações de áudio e chamadas de voz e vídeo. Isso significa que apenas você e o destinatário possuem as chaves necessárias para decifrar a comunicação."

                lower.contains("perfil") || lower.contains("visitante") || lower.contains("foto") || lower.contains("banner") ->
                    "Na aba 'Ajustes' você pode gerenciar seu perfil:\n• Se você entrou como visitante, verá o aviso 'esse perfil não existe, faça login' e o botão para criar sua conta.\n• Com a conta criada, você pode alterar foto de perfil, banner, nome e número de telefone!"

                lower.contains("feed") || lower.contains("post") || lower.contains("publica") ->
                    "A aba 'Social' é o nosso feed com postagens públicas. Você pode curtir, comentar, compartilhar ideias e interagir com tags populares!"

                lower.contains("ligação") || lower.contains("chamada") || lower.contains("áudio") ->
                    "Você pode fazer ligações de voz e chamadas de vídeo seguras diretamente em qualquer conversa, ou gravar notas de áudio pressionando o microfone no chat!"

                else ->
                    "Olá! Estou aqui para ajudar você a explorar todas as funcionalidades do Nexus Chat. Você pode me perguntar como adicionar contatos por telefone, criar grupos ou comunidades, usar status efêmeros e públicos, ou ajustar seu perfil!"
            }

            AppLanguage.EN -> when {
                lower.contains("add") || lower.contains("person") || lower.contains("contact") ->
                    "To add a person in Nexus Chat:\n1. Go to 'Chats' tab\n2. Tap '+ Add person'\n3. Enter the person's phone number.\n\nAn encrypted chat will open immediately!"
                lower.contains("group") ->
                    "To create a group:\n1. Go to 'Chats' tab\n2. Tap 'Create group'\n3. Enter a title and description.\n\nEnjoy advanced end-to-end encryption!"
                lower.contains("status") ->
                    "Nexus Chat features both 24-hour Ephemeral Statuses and discoverable Public Statuses. Visit the 'Status' tab to explore!"
                lower.contains("encrypt") || lower.contains("security") ->
                    "All messages, audio notes, and calls are shielded with advanced 256-bit end-to-end encryption."
                else ->
                    "Hi! I am Nexus AI, ready to guide your experience across chats, audio notes, calls, communities, and settings."
            }

            AppLanguage.ES -> when {
                lower.contains("añadir") || lower.contains("persona") || lower.contains("contacto") ->
                    "Para añadir una persona en Nexus Chat:\n1. Ve a la pestaña 'Chats'\n2. Toca '+ Adicionar pessoa'\n3. Introduce el número de teléfono.\n\n¡Se abrirá un chat cifrado al instante!"
                lower.contains("grupo") ->
                    "Para crear un grupo, toca 'Criar grupo' en la pestaña de Chats y añade nombre y descripción."
                lower.contains("status") || lower.contains("estado") ->
                    "Descubre Estados Efímeros de 24 horas y Estados Públicos en la pestaña 'Estados'."
                else ->
                    "¡Hola! Soy Nexus IA, tu asistente inteligente para navegar en Nexus Chat."
            }
        }

        return Pair(reply, navIntent)
    }
}
