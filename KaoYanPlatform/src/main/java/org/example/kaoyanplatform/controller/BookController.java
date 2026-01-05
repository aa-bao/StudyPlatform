package org.example.kaoyanplatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.kaoyanplatform.common.Result;
import org.example.kaoyanplatform.entity.Book;
import org.example.kaoyanplatform.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/list-by-subject")
    public Result getBooksBySubject(@RequestParam String subjectId) {
        LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Book::getSubjectId, subjectId);
        List<Book> list = bookService.list(queryWrapper);
        return Result.success(list);
    }
}
