import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import kotlin.math.log10

class TFIDFSearch(private val faqs: List<FAQ>) {

    private val tokenizerFactory = DefaultTokenizerFactory().apply {
        tokenPreProcessor = CommonPreprocessor()
    }

    private val vocabulary = mutableSetOf<String>()
    private val termFrequencyVectors = mutableListOf<INDArray>()
    private val documentFrequencies = mutableMapOf<String, Int>()
    private val idfValues = mutableMapOf<String, Double>()

    init {
        preprocessQuestions()
        computeIDF()
    }

    private fun preprocessQuestions() {
        for (faq in faqs) {
            val tokenizedWords = tokenize(faq.question)
            val termCounts = mutableMapOf<String, Double>()

            for (word in tokenizedWords) {
                termCounts[word] = termCounts.getOrDefault(word, 0.0) + 1
            }

            // Compute Term Frequency (TF)
            val totalWords = tokenizedWords.size.toDouble()
            val tfVector = Nd4j.zeros(vocabulary.size)

            termCounts.forEach { (word, count) ->
                val index = vocabulary.indexOf(word)
                if (index >= 0) {
                    tfVector.putScalar(index, count / totalWords)
                }
            }

            termFrequencyVectors.add(tfVector)

            // Compute Document Frequency (DF)
            for (word in termCounts.keys) {
                documentFrequencies[word] = documentFrequencies.getOrDefault(word, 0) + 1
            }
        }
    }

    private fun computeIDF() {
        val totalDocs = faqs.size.toDouble()
        for ((word, df) in documentFrequencies) {
            idfValues[word] = log10(totalDocs / (df + 1))
        }
    }

    fun search(query: String): String {
        val queryVector = tokenize(query).groupingBy { it }
            .eachCount()
            .mapValues { (_, count) -> count.toDouble() / query.length }

        val queryTFIDFVector = Nd4j.zeros(vocabulary.size)
        for ((word, tf) in queryVector) {
            val index = vocabulary.indexOf(word)
            if (index >= 0) {
                queryTFIDFVector.putScalar(index, tf * (idfValues[word] ?: 0.0))
            }
        }

        // Compute cosine similarity
        val results = faqs.mapIndexed { index, faq ->
            val docVector = termFrequencyVectors[index]
            val similarity = cosineSimilarity(queryTFIDFVector, docVector)
            faq to similarity
        }

        val bestMatch = results.maxByOrNull { it.second }
        return bestMatch?.first?.answer ?: "Sorry, I couldn't find a matching question."
    }

    private fun tokenize(text: String): List<String> {
        return tokenizerFactory.create(text.lowercase()).tokens
    }

    private fun cosineSimilarity(vec1: INDArray, vec2: INDArray): Double {
        val dotProduct = vec1.mul(vec2).sumNumber().toDouble()
        val norm1 = vec1.norm2Number().toDouble()
        val norm2 = vec2.norm2Number().toDouble()
        return if (norm1 == 0.0 || norm2 == 0.0) 0.0 else dotProduct / (norm1 * norm2)
    }
}
