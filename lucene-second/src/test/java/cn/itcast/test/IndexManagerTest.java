package cn.itcast.test;

import cn.itcast.impl.BookDaoImpl;
import cn.itcast.pojo.Book;
import com.sun.tools.corba.se.idl.constExpr.And;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import javax.xml.soap.Text;
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
    public void updateIndexBoost() throws IOException {


        IKAnalyzer analyzer = new IKAnalyzer();
        FSDirectory directory = FSDirectory.open(new File("/Users/apple/Documents/itcast90/lucene_test"));
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);

        Document doc = new Document();
        doc.add(new StringField("bookId", "5", Field.Store.YES));
        TextField field = new TextField("bookName", "Lucene Java精华版", Field.Store.YES);

        //权重默认为1.0
        field.setBoost(2.0F);
        doc.add(field);

        doc.add(new FloatField("bookPrice", 110F, Field.Store.YES));
        doc.add(new StoredField("bookPic", "1.jpg"));
        doc.add(new TextField("bookPrice", "落实到家里发生激烈的快捷方式来得及莲富大厦", Field.Store.NO));

        Term term = new Term("bookId", "5");
        indexWriter.updateDocument(term, doc);

        indexWriter.close();
    }

    @Test
    public void queryParser() throws Exception {

        IKAnalyzer analyzer = new IKAnalyzer();
        QueryParser queryParser = new QueryParser("bookName", analyzer);
        Query query = queryParser.parse("bookName:lucene AND bookName:java");

        search(query);

    }

    @Test
    public void booleanQuery() throws Exception {

        BooleanQuery query = new BooleanQuery();

        NumericRangeQuery<Float> query1 = NumericRangeQuery.newFloatRange("bookPrice", 80F, 100F, true, true);

        TermQuery query2 = new TermQuery(new Term("bookName", "lucene"));

        query.add(query1, BooleanClause.Occur.MUST);
        query.add(query2, BooleanClause.Occur.MUST);

        search(query);

    }

    @Test
    public void numericRangerQuery() throws Exception {

//        NumericRangeQuery<Float> query = NumericRangeQuery.newFloatRange("bookPrice", 80F, 100F, true, false);
        NumericRangeQuery<Float> query = NumericRangeQuery.newFloatRange("bookPrice", 80F, 100F, false, true);
        search(query);
    }

    @Test
    public void termQuery() throws Exception {
        TermQuery query = new TermQuery(new Term("bookName", "java"));
        search(query);
    }


    public void search(Query query) throws Exception {

        Directory directory = FSDirectory.open(new File("/Users/apple/Documents/itcast90/lucene_test"));
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        TopDocs topDocs = indexSearcher.search(query, 10);

        ScoreDoc[] docs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : docs) {
            System.out.println("命中文档在Lucene的id:" + scoreDoc.doc + ",命中文档的分值为:" + scoreDoc.score);
            System.out.println("===================================");
            Document doc = indexSearcher.doc(scoreDoc.doc);
            //根据文档在Lucene中的id查找document
            System.out.println("文档id为" + doc.get("bookId"));
            System.out.println("文档name为" + doc.get("bookName"));
            System.out.println("文档pic为" + doc.get("bookPic"));
            System.out.println("文档price为" + doc.get("bookPrice"));
            System.out.println("文档desc为" + doc.get("bookDesc"));
        }
        indexReader.close();

    }

    @Test
    public void updateIndexByTerm() throws Exception {

        Analyzer analyzer = new IKAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
        Directory directory = FSDirectory.open(new File("/Users/apple/Documents/itcast90/lucene_test"));
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
        Document doc = new Document();
        doc.add(new StringField("id", "123", Field.Store.YES));
        doc.add(new TextField("name", "spring and struts and springmvc and mybatis", Field.Store.YES));

        //创建条件对象Term
        Term term = new Term("name", "mybatis");
        //根据词条更新,如果存在则更新,不存在则新建
        indexWriter.updateDocument(term, doc);

        indexWriter.close();

    }

    @Test
    public void deleteAllIndex() throws Exception {

        Analyzer analyzer = new IKAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
        Directory directory = FSDirectory.open(new File("/Users/apple/Documents/itcast90/lucene_test"));
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
        //根据词条删除文档
        indexWriter.deleteAll();

        indexWriter.close();
    }

    @Test
    public void deleteIndexByTerm() throws Exception {

        Analyzer analyzer = new IKAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
        Directory directory = FSDirectory.open(new File("/Users/apple/Documents/itcast90/lucene_test"));
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
        //创建条件对象Term
        Term term = new Term("bookName", "lucene");
        //根据词条删除文档
        indexWriter.deleteDocuments(term);

        indexWriter.close();
    }

    @Test
    public void testCreateIndex() {

        BookDaoImpl bookDao = new BookDaoImpl();
        List<Book> bookList = bookDao.queryBookList();
        ArrayList<Document> docList = new ArrayList<>();

        try {
            for (Book book : bookList) {
                Document doc = new Document();
                /**
                 * IntField整数类型域,TextField文本类型域,FlatField浮点型类型域
                 * 参数1:域名--对应数据库中字段名
                 * 参数2:域值
                 * 参数3:是否需要存储
                 */
                doc.add(new StringField("bookId", book.getId() + "", Field.Store.YES));
                doc.add(new TextField("bookName", book.getBookname(), Field.Store.YES));
                doc.add(new TextField("bookDesc", book.getBookdesc(), Field.Store.NO));
                doc.add(new StoredField("bookPic", book.getPic()));
                doc.add(new FloatField("bookPrice", book.getPrice(), Field.Store.YES));

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
            System.out.println("文档id为" + doc.get("bookId"));
            System.out.println("文档name为" + doc.get("bookName"));
            System.out.println("文档pic为" + doc.get("bookPic"));
            System.out.println("文档price为" + doc.get("bookPrice"));
            System.out.println("文档desc为" + doc.get("bookDesc"));
            System.out.println("===================================");
        }
        indexReader.close();
    }
}
