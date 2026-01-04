package org.example.kaoyanplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.kaoyanplatform.entity.Book;
import org.example.kaoyanplatform.mapper.BookMapper;
import org.example.kaoyanplatform.service.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {
}
