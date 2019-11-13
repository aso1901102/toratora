package jp.ac.asojuku.janken

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //アプリ起動時に共有リファレンスを初期化する
        //共有リファレンスのインスタンスを取得
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        //編集モードを取得して、その編集モードのclear()メソッドを実行して初期化させる
    }

    //追加したライフサイクルメソッド
    override fun onResume() {
        super.onResume()
        //ボタンがクリックされたら処理を呼び出し
        tiger.setOnClickListener{onJankenButtonTapped(it);}
        older.setOnClickListener{onJankenButtonTapped(it);}
        kiyomasakato.setOnClickListener{onJankenButtonTapped(it);}
    }

    //ボタンがクリックされたら呼び出される処理
    fun onJankenButtonTapped(view: View?){
        //画面遷移のためのインテントのインスタンスを作る
        val intent = Intent(this,ResultActivity::class.java)
        //インテントにおまけ情報(Extra)でどのボタンを選んだかを設定する
        intent.putExtra("MY_HAND",view?.id)
        //OSにインテントを引き渡して画面遷移を実行してもらう
        startActivity(intent)
    }
}
