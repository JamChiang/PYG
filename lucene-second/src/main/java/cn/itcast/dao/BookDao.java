package cn.itcast.dao;

import cn.itcast.pojo.Book;

import java.util.List;

public interface BookDao {

    /**
     * 查询图书列表
     * @return Book
     */
    List<Book> queryBookList();
}
