package com.uchicago.yifan.popmovies.data;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

;

/**
 * Created by Yifan on 3/28/16.
 */
public class TestDb extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testCreateDB(){
        mContext.deleteDatabase(MovieDBHelper.DATABASE_NAME);

        SQLiteDatabase db = new MovieDBHelper(this.mContext).getWritableDatabase();

        assertEquals(true, db.isOpen());
    }
}
