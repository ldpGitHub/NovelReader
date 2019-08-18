package com.example.ldp.ireader.event;

import com.example.ldp.ireader.model.bean.CollBookBean;

/**
 * Created by ldp on 17-5-27.
 */

public class DeleteResponseEvent {
    public boolean isDelete;
    public CollBookBean collBook;
    public DeleteResponseEvent(boolean isDelete,CollBookBean collBook){
        this.isDelete = isDelete;
        this.collBook = collBook;
    }
}
