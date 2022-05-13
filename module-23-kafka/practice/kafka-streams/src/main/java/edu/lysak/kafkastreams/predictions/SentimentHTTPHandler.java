package edu.lysak.kafkastreams.predictions;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Properties;


/*This class finds the sentiment of a given text using the
StanfordNLP Library
 */
@Slf4j
public class SentimentHTTPHandler implements HttpHandler {

    //Handle an incoming request
    public void handle(HttpExchange exchange) throws IOException {
        String inputStr = IOUtils.toString(exchange.getRequestBody());
        log.info("Received input " + inputStr);

        //Get sentiment
        String sentiment = getSentiment(inputStr);

        //Write response to output
        exchange.getResponseHeaders().add("Content-Type", "text/html");
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK,
                sentiment.length());
        IOUtils.write(sentiment, exchange.getResponseBody());
        log.info("Returning Sentiment " + sentiment);
        exchange.close();
    }

    public String getSentiment(String text) {

        String[] sentiments = {"Very Negative", "Negative",
                "Neutral", "Positive", "Very Positive"};

        Properties props = new Properties();
        //Neutral by default
        int prediction_class = 2;

        log.info("Doing Sentiment Analysis");

        //Setup sentiment  pipeline
        props.setProperty("annotators",
                "tokenize, ssplit, pos, parse, sentiment");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        //Find sentiment
        Annotation annotation = pipeline.process(text);

        log.info("Pipeline processed");

        //Extract sentiment
        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
            prediction_class = RNNCoreAnnotations.getPredictedClass(tree);
        }
        log.info("Got sentiment : " + prediction_class);
        return sentiments[prediction_class];
    }

    public static void main(String[] args) {

        SentimentHTTPHandler thh = new SentimentHTTPHandler();
        log.info(thh.getSentiment("Hello how are you doing"));
    }
}
