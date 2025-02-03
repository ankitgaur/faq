import com.google.mlkit.nl.smartreply.SmartReply
import com.google.mlkit.nl.smartreply.TextMessage

class MLKitChatbot(private val faqs: List<FAQ>) {

    private val smartReply = SmartReply.getClient()

    fun getReply(query: String, callback: (String) -> Unit) {
        val messages = faqs.map { TextMessage.createForRemoteUser(it.question, System.currentTimeMillis(), "faq") }

        smartReply.suggestReplies(messages).addOnSuccessListener { result ->
            if (result.suggestions.isNotEmpty()) {
                callback(result.suggestions[0].text)
            } else {
                callback("I'm not sure, can you rephrase your question?")
            }
        }.addOnFailureListener {
            callback("Error generating response.")
        }
    }
}
