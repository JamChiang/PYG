package cn.itcast.test;

import cn.itcast.impl.BookDaoImpl;
import cn.itcast.pojo.Book;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author JW
 * @createTime 2018/10/12 8:59 PM
 * @desc todo
 */
public class IndexManagerTest {

    @Test
    public void testCreateIndex() {

        BookDaoImpl bookDao = new BookDaoImpl();
        List<Book> bookList = bookDao.queryBookList();
        ArrayList<Document> docList = new ArrayList<>();

        try {
            for (Book book : bookList) {
                Document doc = new Document();
                doc.add(new TextField("bookId",book.getId()+"",Field.Store.YES));
                doc.add(new TextField("bookName",book.getBookname(),Field.Store.YES));
                doc.add(new TextField("bookDesc",book.getBookdesc(),Field.Store.YES));
                doc.add(new TextField("bookPic",book.getPic(),Field.Store.YES));
                doc.add(new TextField("bookPrice",book.getPrice()+"",Field.Store.YES));

                docList.add(doc);
            }

            Analyzer analyzer = new IKAnalyzer();
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);

            File file = new File("/Users/apple/Documents/itcast90/lucene_test");
            Directory directory = FSDirectory.open(file);
            IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);

            for (Document document : docList) {
                indexWriter.addDocument(document);
            }
            indexWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testIndexSearch() throws Exception {

        //分词器
        Analyzer analyzer = new IKAnalyzer();
        //创建查询对象Query
        QueryParser queryParser = new QueryParser("bookName", analyzer);

        Query query = queryParser.parse("java");

        File file = new File("/Users/apple/Documents/itcast90/lucene_test");

        Directory directory = FSDirectory.open(file);

        IndexReader indexReader = DirectoryReader.open(directory);

        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        TopDocs topDocs = indexSearcher.search(query, 10);

        System.out.println("本次查询总命中文档数:" + topDocs.totalHits);

        ScoreDoc[] scoreDocs = topDocs.scoreDocs;

        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("命中文档在Lucene的id:" + scoreDoc.doc + ",命中文档的分值为:" + scoreDoc.score);
            System.out.println("===================================");
            Document doc = indexSearcher.doc(scoreDoc.doc);
            //根据文档在Lucene中的id查找document
            System.out.println("文档id为"+doc.get("bookId"));
            System.out.println("文档name为"+doc.get("bookName"));
            System.out.println("文档pic为"+doc.get("bookPic"));
            System.out.println("文档price为"+doc.get("bookPrice"));
            System.out.println("文档desc为"+doc.get("bookDesc"));
            System.out.println("===================================");
        }
        indexReader.close();
    }
}
