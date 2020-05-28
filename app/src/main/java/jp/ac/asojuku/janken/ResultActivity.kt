package jp.ac.asojuku.janken

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {

    //グーチョキパーを表す定数を定義する
    val tiger = 0; val older = 1;val kiyomasakato = 2;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

    }

    override fun onResume() {
        super.onResume()
        //じゃんけんで選んだView部品のidをインテントから取り出し
        val id = intent.getIntExtra("MY_HAND",0)
        //前の画面で選んだ手を保持する定数を定義する
        val myHand:Int;
        //idの値によって処理を分岐、自分のじゃんけん画像を切り替える
        myHand = when(id){
            R.id.tiger -> {myHandImage.setImageResource(R.drawable.tiger);tiger}
            R.id.older -> {myHandImage.setImageResource(R.drawable.older);older}
            R.id.kiyomasakato -> {myHandImage.setImageResource(R.drawable.kiyomasakato);kiyomasakato}
            else -> tiger
        }
        //コンピューターの手をランダムに決める
        val comHand = getHand();//メソッドで組み立てた手を採用する
        //コンピューターの手に合わせてコンピューターの画像を切り替える
        when(comHand){
            tiger -> comHandImage.setImageResource(R.drawable.tiger)
            older -> comHandImage.setImageResource(R.drawable.older)
            kiyomasakato -> comHandImage.setImageResource(R.drawable.kiyomasakato)
        }
        val gameResult = (comHand - myHand + 3) % 3
        //計算結果に合わせて勝敗メッセージを切り替える
        when(gameResult){
            0-> resultLabel.setText(R.string.titleCall)
            1-> resultLabel.setText(R.string.result_win)
            2-> resultLabel.setText(R.string.result_lose)
        }
        //戻るボタンにタップされた時の処理を設定する
        backButton.setOnClickListener{this.finish()}//戻るボタンが押されたら結果画面を破棄する

        //勝敗とじゃんけんで出した手を保存する
        this.saveData(myHand,comHand,gameResult);//引数：ユーザーの手、コンピューターの手、勝敗を渡す

        //勝ち以外は画面を戻して再戦
        if(gameResult != 1){
            // 3秒後に処理を実行する
            Handler().postDelayed(Runnable {
                var intent = Intent(this,MainActivity::class.java)
                this.startActivity(intent)
                this.finish()
            }, 3000)
        }
    }

    //ResultActivityクラスに勝敗データを保存するメソッドを追加する
    private fun saveData(myHand:Int,comHand:Int,gameResult:Int){
        //共有プリファレンスを使う①インスタンスを取得
        var pref = PreferenceManager.getDefaultSharedPreferences(this);
        //②値を取得する、キーを指定して値を取得する、該当するものがなければデフォルト値が返る
        val gameCount = pref.getInt("GAME_COUNT",0); //デフォルト値：0、勝負の数
        val winningStreakCount = pref.getInt("WINNING_STREAK_COUNT",0)//連勝数
        val lastComHand = pref.getInt("LAST_COM_HAND",0)//前回のコンピューターの手
        val lastGameResult = pref.getInt("LAST_GAME_RESULT",-1)//前回の詳細
        //保存を始めていく、まず値を組み立てる
        //連勝数
        val editWinningStreakCount = when{
            //前回も勝って今回も勝ったら連勝+1を返す
            (lastGameResult == 2 && gameResult == 2) -> (winningStreakCount + 1)
            //それ以外
            else -> 0
        }
        //③共有プリファレンスの編集モードを取得
        val editor = pref.edit();
        //editorのメソッドをメソッドチェーンで呼び出し
        editor.putInt("GAME_COUNT",gameCount+1)//勝負数
            .putInt("WINNING_STREAK_COUNT",editWinningStreakCount)//連勝数
            .putInt("LAST_MY_HAND",myHand)//ユーザーの前の手
            .putInt("LAST_COM_HAND",comHand)//コンピューターの前の手
            .putInt("BEFORE_LAST_COM_HAND",lastComHand)//コンピューターの前々回の手
            .putInt("GAME_RESULT",gameResult)//勝敗
            .apply()//編集モードを確定して閉じる
    }

    //心理学ロジックを使ってグーチョキパーを決めるメソッド
    private fun getHand():Int {
        var hand = (Math.random()*3).toInt();
        //ここから心理学のロジックを使ってhandの値を上書きするかどうか処理する
        //共有プリファレンスにホzんしたデータを取り出すためにインスタンスを取得する
        val pref = PreferenceManager.getDefaultSharedPreferences(this);
        //共有プリファレンスのインスタンス変数prefを使って保存値を取得していく
        val gameCount = pref.getInt("GAME_COUNT",0);
        val winningStreakCount = pref.getInt("WINNING_STREAK_COUNT",0);
        val lastMyHand = pref.getInt("LAST_MY_HAND",0);
        val lastComHand = pref.getInt("LAST_COM_HAND",0);
        val beforeLastComHand = pref.getInt("BEFORE_LAST_COM_HAND",0);
        val gameResult = pref.getInt("GAME_RESULT",-1);

        //取得した保存値を使ってコンピューターの出す手を(戻り値 hand)上書きする
        //全開が一回戦の時
        if(gameCount == 1){
            if(gameResult == 2){
                //前回が一回戦でさらにコンピューターの勝ち
                //コンピューターは次に出す手を変える
                while(lastComHand ==hand){//前回と同じ手なら変更
                    hand = (Math.random()*3).toInt();//ランダムな値で更新
                }
            }else if(gameResult == 1){
                //前回が一回戦で、さらにコンピューターの負け
                //相手が前回出した手に勝つ手を出す
                hand = (lastMyHand +2)%3;
            }
        }else if(winningStreakCount>0){
            //連勝中の時
            if(beforeLastComHand == lastComHand){//同じ手で連勝した
                while(lastComHand ==hand){//前回と同じ手なら変更
                    hand = (Math.random()*3).toInt();//ランダムな値で更新
                }
            }
        }
        return hand;//最終的な値を決定して返す
    }
}
