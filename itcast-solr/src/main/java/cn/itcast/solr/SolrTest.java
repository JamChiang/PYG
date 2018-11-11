package cn.itcast.solr;

import com.pinyougou.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author JW
 * @createTime 2018/10/30 5:05 PM
 * @desc todo
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-solr.xml")
public class SolrTest {

    @Autowired
    private SolrTemplate solrTemplate;


    @Test
    public void addTest() {
        TbItem item = new TbItem();
        item.setId(1L);
        item.setTitle("Apple iPhone XS Max (A2104) 256GB 金色 移动联通电信4G手机 双卡双待");
        solrTemplate.saveBean(item);
        solrTemplate.commit();
    }

    @Test
    public void deleteTest() {
        solrTemplate.deleteById("1");
        solrTemplate.commit();
    }

    @Test
    public void deleteByQueryTest() {
        SimpleQuery query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    //根据关键字分页查询
    @Test
    public void testQueryInPage() {
        SimpleQuery query = new SimpleQuery("*:*");
        query.setOffset(10);//分页起始索引
        query.setRows(10);//分页页大小
        ScoredPage<TbItem> items = solrTemplate.queryForPage(query, TbItem.class);

        showPage(items);

    }

    private void showPage(ScoredPage<TbItem> items) {
        System.out.println("总记录数:" + items.getTotalElements());
        System.out.println("总页数:" + items.getTotalPages());
        List<TbItem> tbItems = items.getContent();
        for (TbItem item : tbItems) {
            System.out.println(item.toString());
        }
    }

    //多条件查询
    @Test
    public void testMulitiQuery() {
        SimpleQuery query = new SimpleQuery("*:*");
        Criteria criteria1 = new Criteria("item_title").contains("小米");
        query.addCriteria(criteria1);
        Criteria criteria2 = new Criteria("item_price").greaterThanEqual("1000");
        query.addCriteria(criteria2);

        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query, TbItem.class);
        showPage(tbItems);
    }

}
