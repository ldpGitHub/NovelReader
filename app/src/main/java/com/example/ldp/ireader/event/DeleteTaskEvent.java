package com.example.ldp.ireader.event;

import com.example.ldp.ireader.model.bean.CollBookBean;

/**
 * Created by ldp on 17-5-27.
 */

public class DeleteTaskEvent {
    public CollBookBean collBook;

    public DeleteTaskEvent(CollBookBean collBook){
        this.collBook = collBook;
    }
}
