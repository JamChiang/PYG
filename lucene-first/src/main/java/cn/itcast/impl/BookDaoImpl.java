package cn.itcast.impl;

import cn.itcast.dao.BookDao;
import cn.itcast.pojo.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDaoImpl implements BookDao {

    public List<Book> queryBookList() {
        ArrayList<Book> bookList = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");

            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/mybatis", "root", "123456");

            statement = connection.createStatement();

            rs = statement.executeQuery("select * from book");

            Book book = null;
            while (rs.next()) {
                book = new Book();
                book.setId(rs.getInt("id"));
                book.setBookname(rs.getString("bookname"));
                book.setBookdesc(rs.getString("bookdesc"));
                book.setPic(rs.getString("pic"));
                book.setPrice(rs.getFloat("price"));

                bookList.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return bookList;
    }

    public static void main(String[] args) {
        BookDao bookDao = new BookDaoImpl();
        List<Book> bookList = bookDao.queryBookList();
        for (Book book : bookList) {
            System.out.println(book);
        }
    }
}
