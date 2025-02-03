import org.apache.lucene.analysis.core.SimpleAnalyzer
import org.apache.lucene.document.*
import org.apache.lucene.index.*
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.*
import org.apache.lucene.store.RAMDirectory

class LuceneTFIDFSearch(private val faqs: List<FAQ>) {

    private val index = RAMDirectory()
    private val analyzer = SimpleAnalyzer()

    init {
        indexDocuments()
    }

    private fun indexDocuments() {
        val config = IndexWriterConfig(analyzer)
        val writer = IndexWriter(index, config)

        for (faq in faqs) {
            val doc = Document()
            doc.add(TextField("question", faq.question, Field.Store.YES))
            doc.add(TextField("answer", faq.answer, Field.Store.YES))
            writer.addDocument(doc)
        }
        writer.close()
    }

    fun search(queryText: String): String {
        val reader = DirectoryReader.open(index)
        val searcher = IndexSearcher(reader)
        val parser = QueryParser("question", analyzer)
        val query = parser.parse(queryText)

        val topDocs = searcher.search(query, 1) // Return the best match
        if (topDocs.scoreDocs.isNotEmpty()) {
            val doc = searcher.doc(topDocs.scoreDocs[0].doc)
            return doc.get("answer") ?: "No answer found."
        }
        return "Sorry, I couldn't find a matching question."
    }
}
