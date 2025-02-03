import com.google.mlkit.nl.smartreply.SmartReply
import com.google.mlkit.nl.smartreply.TextMessage

class MLKitFAQSearch(private val faqs: List<FAQ>) {

    fun search(query: String, callback: (String) -> Unit) {
        val smartReply = SmartReply.getClient()

        val messages = faqs.map { TextMessage.createForRemoteUser(it.question, System.currentTimeMillis(), "faq") }

        smartReply.suggestReplies(messages).addOnSuccessListener { result ->
            if (result.suggestions.isNotEmpty()) {
                callback(result.suggestions[0].text)
            } else {
                callback("No matching answer found.")
            }
        }.addOnFailureListener {
            callback("Error processing the request.")
        }
    }
}
