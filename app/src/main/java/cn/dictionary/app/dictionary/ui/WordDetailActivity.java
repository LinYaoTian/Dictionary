package cn.dictionary.app.dictionary.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import cn.dictionary.app.dictionary.R;
import cn.dictionary.app.dictionary.db.WordBookDao;
import cn.dictionary.app.dictionary.entity.Words;
import cn.dictionary.app.dictionary.view.ResultFragment;

public class WordDetailActivity extends AppCompatActivity {

    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail);
        Intent intent = getIntent();
        String word = intent.getStringExtra("input");
        Words words = WordBookDao.getInstance().queryWord(word);
        new ResultFragment().showData(words);
    }
}
