import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tfidfSearch: TFIDFSearch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Lucene Code
        //val faqModel = FAQModel(this)
        //val luceneSearch = LuceneTFIDFSearch(faqModel.faqs)

        //val query = "What is the My Spectrum App?"
        //val answer = luceneSearch.search(query)
        //println(answer) 
        // Output: The My Spectrum App is a mobile application...

        //MLkit code
        //val faqModel = FAQModel(this)
        //val chatbot = MLKitChatbot(faqModel.faqs)

        //val userQuery = "Who can use the My Spectrum App?"
        //chatbot.getReply(userQuery) { response ->
        //    println("Chatbot: $response")
        //}

        //DL4j code
        val faqModel = FAQModel(this)
        tfidfSearch = TFIDFSearch(faqModel.faqs)


        val editTextQuery = findViewById<EditText>(R.id.editTextQuery)
        val buttonSearch = findViewById<Button>(R.id.buttonSearch)
        val textViewResults = findViewById<TextView>(R.id.textViewResults)

        buttonSearch.setOnClickListener {
            val query = editTextQuery.text.toString()
            if (query.isNotEmpty()) {
                val answer = tfidfSearch.search(query)
                textViewResults.text = answer
            } else {
                textViewResults.text = "Enter a search question"
            }
        }
    }
}
