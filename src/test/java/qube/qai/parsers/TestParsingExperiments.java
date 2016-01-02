package qube.qai.parsers;

import info.bliki.wiki.filter.HTMLConverter;
import info.bliki.wiki.model.WikiModel;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.main.QaiTestBase;
import qube.qai.persistence.WikiArticle;
import qube.qai.services.SearchServiceInterface;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * Created by rainbird on 12/21/15.
 */
public class TestParsingExperiments extends QaiTestBase {

    private static Logger logger = LoggerFactory.getLogger("TestParsingExperiments");

    private String stockListingPage = "Lists of companies by stock exchange listing.xml";

    private String SnP500Page = "List of S&P 500 companies.xml";

    private String mousePage = "Mouse.xml";

    private String darwinPage = "Charles Darwin.xml";

    private String pythagorasTheorem = "Pythagorean theorem.xml";

    @Inject
    @Named("Wikipedia_en")
    private SearchServiceInterface searchService;

    /**
     * this is only for playing around with "the" page
     * i hope i find a way to integrate
     * i think, in this case, i shall experiment with the name-finder
     * which is specialized for company names.
     * @throws Exception
     */
    public void testSnP500() throws Exception {

        WikiArticle snpPage = searchService.retrieveDocumentContentFromZipFile(SnP500Page);
        assertNotNull("oh, come on!!!", snpPage);

        Tokenizer tokenizer = createTokenizer();
        String[] tokens = tokenizer.tokenize(snpPage.getContent());

        InputStream modelIn = getClass().getResourceAsStream("/opennlp/en/en-ner-organization.bin");
        TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
        modelIn.close();

        NameFinderME nameFinder = new NameFinderME(model);
        Span[] names = nameFinder.find(tokens);
        for (Span span : names) {
            logger.info("found name-span: '" + span.toString() + "'");
            int start = span.getStart();
            int end = span.getEnd();
            StringBuffer buffer = new StringBuffer();
            for (int i = start; i < end; i++) {
                buffer.append(tokens[i]);
                buffer.append(" ");
            }
            logger.info("found name: '" + buffer.toString() + "'");
        }
    }

    /**
     * POS-part of speech analysis experiment
     */
    public void testPOS() throws Exception {

        WikiArticle pythagorasArticle = searchService.retrieveDocumentContentFromZipFile(pythagorasTheorem);
        assertNotNull("oh come on!!!", pythagorasArticle);

        Tokenizer tokenizer = createTokenizer();
        String[] tokens = tokenizer.tokenize(pythagorasArticle.getContent());

        InputStream modelIn = getClass().getResourceAsStream("/opennlp/en/en-pos-maxent.bin");
        POSModel model = new POSModel(modelIn);
        POSTaggerME tagger = new POSTaggerME(model);
        String[] tags = tagger.tag(tokens);
        for (int i = 0; i < tags.length; i++) {
            logger.info("token: '" + tokens[i] + "' tag: '" + tags[i] + "'");
        }

    }

    /**
     * this is an experiment in name-detection
     */
    public void testNameDetection() throws Exception {

        WikiArticle darwinArticle = searchService.retrieveDocumentContentFromZipFile(darwinPage);
        assertNotNull("oh come on!!!", darwinArticle);

        Tokenizer tokenizer = createTokenizer();
        String[] tokens = tokenizer.tokenize(darwinArticle.getContent());

        InputStream modelIn = getClass().getResourceAsStream("/opennlp/en/en-ner-person.bin");
        TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
        modelIn.close();

        NameFinderME nameFinder = new NameFinderME(model);
        Span[] names = nameFinder.find(tokens);
        for (Span span : names) {
            logger.info("found name-span: '" + span.toString() + "'");
            int start = span.getStart();
            int end = span.getEnd();
            StringBuffer buffer = new StringBuffer();
            for (int i = start; i <= end; i++) {
                buffer.append(tokens[i]);
                buffer.append(" ");
            }
            logger.info("found name: '" + buffer.toString() + "'");
        }
    }

    /**
     * this is for trying out how sentence detection works
     */
    public void testSenteceDetection() throws Exception {

        // load first the article we want to try the things out
        WikiArticle mouseArticle = searchService.retrieveDocumentContentFromZipFile(mousePage);
        assertNotNull("oh come on!!!", mouseArticle);

        // this is how you create the trained sentence detection model
        // to be honest, i don't understand what is meant with the model in this case
        // for example...
        InputStream modelIn = getClass().getResourceAsStream("/opennlp/en/en-sent.bin");
        SentenceModel model = new SentenceModel(modelIn);
        modelIn.close();

        // when mode is loaded, we get on to the sentence-detector itself
        SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
        String sentences[] = sentenceDetector.sentDetect(mouseArticle.getContent());
        logger.info("here it goes: '" + mouseArticle.getTitle() + "' in sentences");
        for (String sentence : sentences) {
            logger.info(sentence);
        }
    }

    /**
     * this is mainly for testing Wiki-tables which are then will be
     * converted to stock-quotes
     * @throws Exception
     */
    public void testWikiTableParser() throws Exception {

        // in any case, this is the page we are interested in
        WikiArticle wikiArticle = searchService.retrieveDocumentContentFromZipFile(stockListingPage);
        assertNotNull("if this list cannot be found this would be a very short test indeed", wikiArticle);

        String articleContent = wikiArticle.getContent();
        logger.info("original content: " + articleContent);
        StringBuilder builder = new StringBuilder();
        WikiModel wikiModel = WikiIntegration.createModel(wikiArticle.getContent(), builder);
        Set<String> links = wikiModel.getLinks();
        if (links == null) {
            logger.error("there is something terribly wrong- no links, using bliki instead...");
            fail("parsing a html-text which consists only of links, finding none... fail");
        }
        assertTrue("check that we indeed have links in there", !links.isEmpty());
        // then go through the links to see what pages those are...
        // we want a page with tables...
        WikiArticle linkArticle = null;
        for (String link : links) {
            String filename = convertToFilename(link);
            logger.info("retrieving the link: '" + link + "' with filename: " + filename);
            linkArticle = searchService.retrieveDocumentContentFromZipFile(filename);

            // for the moment being, let's say it's ok that file not found
            // and simply get on to the next one
            if (linkArticle == null) {
                continue;
            } else {
                break;
            }

        }

        if (linkArticle == null) {
            logger.error("could not get hold of any of the articles referenced- this too is fail, i guess");
            fail("could not get hold of any of the articles referenced- this too is fail, i guess");
        }

        // at last we have a content we can play around with
        String linkContent = linkArticle.getContent();
        logger.info("original article content: " + linkContent);

        //WikiIntegration wikiRipper = createRipper(linkArticle);
        String linkHtml = WikiIntegration.wikiToHtml(linkContent);
        logger.info("generated text: " + linkHtml);
    }

    private String convertToFilename(String link) {
        //String filename = StringUtils.replace(link, " ", "_");
        return link + ".xml";
    }

//    private WikiIntegration createRipper(WikiArticle article) {
//        WikiIntegration wikiRipper = new WikiIntegration("${image}", "${title}");
//
//        try {
//            StringBuilder bufferOut = new StringBuilder();
//            WikiModel.toText(wikiRipper, new HTMLConverter(), article.getContent(), bufferOut, false, false);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return wikiRipper;
//    }

    private WikiModel createModel(WikiArticle wikiArticle) {
        WikiModel wikiModel = new WikiModel("${image}", "${title}");

        try {
            StringBuilder bufferOut = new StringBuilder();
            WikiModel.toText(wikiModel, new HTMLConverter(), wikiArticle.getContent(), bufferOut, false, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return wikiModel;
    }

    /**
     * i think this is an excellent occasion for testing tokenizers and parsers
     * @return
     */
    private Tokenizer createTokenizer() {

        Tokenizer tokenizer = null;
        try {
            // /opennlp/en-ner-organization.bin
            InputStream modelIn = getClass().getResourceAsStream("/opennlp/en/en-token.bin");
            TokenizerModel tokenizerModel = new TokenizerModel(modelIn);
            tokenizer = new TokenizerME(tokenizerModel);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize tokenizer, resource: 'en-token.bin' is missing.");
        }

        return tokenizer;
    }
}
