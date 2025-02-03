import android.content.Context
import org.json.JSONObject
import java.io.InputStream

data class FAQ(val question: String, val answer: String)

class FAQModel(context: Context) {
    val faqs: List<FAQ>

    init {
        faqs = loadFAQs(context)
    }

    private fun loadFAQs(context: Context): List<FAQ> {
        val jsonString = readJSONFromAssets(context, "faqs.json")
        val jsonObject = JSONObject(jsonString)
        val faqArray = jsonObject.getJSONArray("faqs")

        val faqList = mutableListOf<FAQ>()
        for (i in 0 until faqArray.length()) {
            val faqObject = faqArray.getJSONObject(i)
            val question = faqObject.getString("question")
            val answer = faqObject.getString("answer")
            faqList.add(FAQ(question, answer))
        }

        return faqList
    }

    private fun readJSONFromAssets(context: Context, filename: String): String {
        val inputStream: InputStream = context.assets.open(filename)
        return inputStream.bufferedReader().use { it.readText() }
    }
}
