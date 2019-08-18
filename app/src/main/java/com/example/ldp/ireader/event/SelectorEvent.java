package com.example.ldp.ireader.event;

import com.example.ldp.ireader.model.flag.BookDistillate;
import com.example.ldp.ireader.model.flag.BookSort;
import com.example.ldp.ireader.model.flag.BookType;

/**
 * Created by ldp on 17-4-21.
 */

public class SelectorEvent {

    public BookDistillate distillate;

    public BookType type;

    public BookSort sort;

    public SelectorEvent(BookDistillate distillate,
                         BookType type,
                         BookSort sort) {
        this.distillate = distillate;
        this.type = type;
        this.sort = sort;
    }
}
