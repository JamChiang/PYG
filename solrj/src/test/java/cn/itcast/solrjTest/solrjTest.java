package cn.itcast.solrjTest;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Test;

import javax.swing.text.Highlighter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author JW
 * @createTime 2018/10/14 4:36 PM
 * @desc todo
 */
public class solrjTest {

    HttpSolrServer httpSolrServer;

    @Before
    public void setUp() {
        String baseUrl = "http://127.0.0.1:8080/solr/collection2";
        httpSolrServer = new HttpSolrServer(baseUrl);
    }

    /**
     * 添加和更新
     * @throws Exception
     */
    @Test
    public void addOrUpdateTest() throws Exception {

        SolrInputDocument solrInputDocument = new SolrInputDocument();
        solrInputDocument.setField("id", 123L);
        solrInputDocument.setField("item_title", "1231312荣耀 V10 高配版 6GB+64GB 幻夜黑 移动联通电信4G全面屏游戏手机 双卡双待");
        solrInputDocument.setField("item_price", 2099);
        solrInputDocument.setField("item_catalog_name", "手机");
        solrInputDocument.setField("item_image", "http://www.itcast.cn");

        httpSolrServer.add(solrInputDocument);
        httpSolrServer.commit();
    }

    /**
     * 根据id删除
     * @throws IOException
     * @throws SolrServerException
     */
    @Test
    public void deleteById() throws IOException, SolrServerException {
        httpSolrServer.deleteById("123");
        httpSolrServer.commit();
    }

    /**
     * 根据条件删除
     * @throws IOException
     * @throws SolrServerException
     */
    @Test
    public void deleteByQuery() throws IOException, SolrServerException {
        httpSolrServer.deleteByQuery("item_title:荣耀");
        httpSolrServer.commit();
    }


    /**
     * 根据条件查询
     * @throws SolrServerException
     */
    @Test
    public void queryTest() throws SolrServerException {

        SolrQuery solrQuery = new SolrQuery().setQuery("item_title:荣耀");

        QueryResponse queryResponse = httpSolrServer.query(solrQuery);
        SolrDocumentList results = queryResponse.getResults();

        System.out.println("符合查询条件的总记录数:" + results.getNumFound());

        for (SolrDocument result : results) {
            System.out.println("id = " + result.get("id"));
            System.out.println("item_title = " + result.get("item_title"));
            System.out.println("item_price = " + result.get("item_price"));
            System.out.println("item_catalog_name = " + result.get("item_catalog_name"));
            System.out.println("item_image = " + result.get("item_image"));
        }

    }

    @Test
    public void queryHighLightTest() throws SolrServerException {

        SolrQuery solrQuery = new SolrQuery().setQuery("item_title:荣耀");

        //设置高亮
        solrQuery.setHighlight(true);
        solrQuery.addHighlightField("item_title");
        solrQuery.setHighlightSimplePre("<em>");
        solrQuery.setHighlightSimplePost("</em>");

        QueryResponse queryResponse = httpSolrServer.query(solrQuery);

        //获取高亮返回结果
        Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
        SolrDocumentList results = queryResponse.getResults();

        System.out.println("符合查询条件的总记录数:" + results.getNumFound());

        for (SolrDocument result : results) {
            String title = highlighting.get(result.get("id")).get("item_title").get(0);
            System.out.println("高亮标题是:" + title);

            System.out.println("id = " + result.get("id"));
            System.out.println("item_title = " + result.get("item_title"));
            System.out.println("item_price = " + result.get("item_price"));
            System.out.println("item_catalog_name = " + result.get("item_catalog_name"));
            System.out.println("item_image = " + result.get("item_image"));
        }

    }


    /**
     * 查询不同的solrCore
     * @throws SolrServerException
     */
    @Test
    public void querySolrCoreTest() throws SolrServerException {

        String baseUrl1 = "http://127.0.0.1:8080/solr/collection2";
        httpSolrServer = new HttpSolrServer(baseUrl1);

        SolrQuery solrQuery = new SolrQuery().setQuery("item_title:荣耀");

        QueryResponse queryResponse = httpSolrServer.query(solrQuery);
        SolrDocumentList results = queryResponse.getResults();

        System.out.println("符合查询条件的总记录数:" + results.getNumFound());

        for (SolrDocument result : results) {
            System.out.println("id = " + result.get("id"));
            System.out.println("item_title = " + result.get("item_title"));
            System.out.println("item_price = " + result.get("item_price"));
            System.out.println("item_catalog_name = " + result.get("item_catalog_name"));
            System.out.println("item_image = " + result.get("item_image"));
        }

    }
}
