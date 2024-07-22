
## MVVMによる分離(hiltによるDI）

- hiltによるDI🙆‍♀️
- [MVVMによる分離](https://qiita.com/sdkei/items/a48ae24536562ed000b3)
  - view,viewModel,Modelとあるが、今はModelがない感じ。 


## フォロー画面の改善

- 現在：ユーザ検索して、即フォロー対象になっている
- あるべき1:ユーザ検索とフォローは分けるべき
- あるべき2:フォローは相手の承認を持って完了とすべき
- Jetpack Compose で Google風 検索バー


## 友達一覧を表示

- 現状
  - firestoreのユーザデータ全量の表示　🙆‍♀️
  - フォローしているものだけを表示🙆‍
    - authのuidとfirebaseのuserのドキュメントIDを一致🙆‍♀️
  - ユーザをタップ後、統計情報画面に遷移 🙆‍
  - 画面を今風に
    - card　🙆‍♀️
    - 画像と名前の表示

## profileの編集

    - 画像
        - 画像アップロード🙆‍♀️   
    - 名前
    - フォロー/フォロワー
    - 通知

    


[//]: # (- ポケモンサーチを例に)

[//]: # (  - 前提知識；fragment)

[//]: # (    - 以下により考慮不要)

[//]: # (      fragmentはcomposeが出る前に考慮が必要だったが、 composeでは不要)

[//]: # (    > fragmentの問題)

[//]: # (    )
[//]: # (    > class CounterActivity : AppCompatActivity&#40;&#41; {)

[//]: # (    private var count = 0)

[//]: # (    override fun onCreate&#40;savedInstanceState: Bundle?&#41; {)

[//]: # (    super.onCreate&#40;savedInstanceState&#41;)

[//]: # (    setContentView&#40;R.layout.activity_counter&#41;)

[//]: # (        // カウンターの値を表示するTextView)

[//]: # (        val counterTextView = findViewById<TextView>&#40;R.id.counterTextView&#41;)

[//]: # ()
[//]: # (        // カウントをインクリメントしてTextViewに表示する)

[//]: # (        count++)

[//]: # (        counterTextView.text = "Count: $count")

[//]: # (    })

[//]: # (    })

[//]: # (    この例だと、onCreate時にcountの生成やインクリメントをしているが、何かの事情でアクティビティが破棄された場合)

[//]: # (    countの値が初期値されてしまう。)

[//]: # (    そもそもfragmentを使う理由は1画面の機能ごとにactivityを用意する場合、)

[//]: # (    メモリの過剰消費に繋がるため、同画面内の部品ごとにfragmentとして構築していた（activityのライフサイクルにfragmentは紐づいているので）)

[//]: # (  )
[//]: # (    一方、composableはライフサイクルとは独立しているため、ライフサイクルによる影響はない)

[//]: # (　　)
[//]: # (  > @Composable)

[//]: # (    fun CounterScreen&#40;&#41; {)

[//]: # (    var count by remember { mutableStateOf&#40;0&#41; )

[//]: # (    Column&#40;)

[//]: # (    verticalArrangement = Arrangement.Center,)

[//]: # (    horizontalAlignment = Alignment.CenterHorizontally,)

[//]: # (    modifier = Modifier.fillMaxSize&#40;&#41;)

[//]: # (    &#41; {)

[//]: # (    Text&#40;text = "Count: $count", fontSize = 24.sp&#41;)

[//]: # (    Button&#40;onClick = { count++ }&#41; {)

[//]: # (    Text&#40;text = "Increment"&#41;)

[//]: # (    })

[//]: # (    })

[//]: # (    })

[//]: # ()
[//]: # (  )
[//]: # (  - 前提知識；viewmodel)

[//]: # (        - viewmodelとcompoableの比較（というかcomposableで使用が必要か？）)




## 統計情報取得タイミング
- 取得:アプリ起動時
- 取得：oncreate時なので、アプリ起動時のタイミング。アプリを開きっぱだと古い情報だが、良い？

## 統計情報アップロードタイミング
- アップロード：ログイン後（ログイン前だとキャッシュによりuidが変わらない。実機は同じ端末で同じユーザだから考慮は不要な気がするが ）
- インスタンス化タイミングの検討
- ユーザ登録周りの挙動が怪しい（@icloud.com)でログインできない
- こルーチンでの非同期処理とする→OK

## 統計情報取得
- ユーザごとに統計情報を取得→OK
- 機能テストはOKなので、実際のアプリの動きに沿って改良する
  - 友達の一覧が画面に表示されている状態で、クリックしたら統計情報が取得される。
    この取得の際に権限チェックがされる
    - 友達A
    - 友達B
  - 権限チェック
    - 相手が自分のことをFollowingしていたら取得？

## データモデル
statistics (コレクション)
└── {user_id} (ドキュメント)
└── dailyStatistics (サブコレクション)
├── {date} (ドキュメント) <同一日の更新は上書き>
│    ├── com.google.android.apps.nexuslauncher (マップ)
│    ├── com.android.settings (マップ)
│    ├── com.example.myapplication2 (マップ)
│    └── com.google.android.settings.intelligence (マップ)
└── {date} (ドキュメント)
├── com.google.android.apps.nexuslauncher (マップ)
├── com.android.settings (マップ)
├── com.example.myapplication2 (マップ)
└── com.google.android.settings.intelligence (マップ)

- ドキュメントが空（サブコレクションにてデータを管理する）場合、コレクションに対するクエリが失敗する（何も返さない）
  そのため、ドキュメントに設定されているドキュメントidをコレクションに対するクエリで取得して、、、その後
  db.collection("statistics").document("userid").subcollection()
  みたいなことができない。

## FireSore

[クライアントサイドジョイン](https://www.google.com/search?q=client+side+join&oq=client+side+join&gs_lcrp=EgZjaHJvbWUyBggAEEUYOTIKCAEQABiABBiiBDIKCAIQABiABBiiBDIKCAMQABiABBiiBNIBCDM4NzdqMGo0qAIAsAIB&sourceid=chrome&ie=UTF-8#fpstate=ive&vld=cid:3146b0e4,vid:KnGnxufvYGg,st:0)
[リレーションシップについて](https://qiita.com/1amageek/items/d606dcee9fbcf21eeec6)
[サブコレクション](https://qiita.com/1amageek/items/d2ef7a49bccf5b4ea78e)
[セキュリティルールとQueryの関係はこちらに記載されています。](https://firebase.google.com/docs/firestore/security/rules-query?hl=ja)
[Firestoreのモデリング公式動画を解説してみた](https://qiita.com/KosukeSaigusa/items/860b5a2a6a02331d07cb)

### 例1(簡単な写真アプリ)

> このアプリの仕様はとってもシンプルでユーザーが写真をFirebaseにアップロードできるアプリです。
> そして写真を見ることができるのはアップロードしたユーザーだけです。
> 複数のユーザーで写真を共有できるグループ機能を追加しましょう。
> GroupというModelを追加して複数ユーザーで写真が見えるようにしましょう。

#### 案１
users
    - userA
        L name:A
    - userB
        L name:B

gruop
    - groupA
      L member:userID(A),userID(B)
      L name:groupA
      L photo : photoID(A)  

photo
    - photo0
      L owener:userID(A)
      L url :http://a
    - photo1
      L owener:userID(b)
      L url :http://b

#### 案2
> photoはuserの持ち物なので、サブコレクションとして

users
    - userA
     L name:A 
        photo
            - photoA
                L owner:userID(A)
                L URL:http://a1
            - photoB
                L owner:userID(A)
                L URL:http://a2

gruop
    - groupA
    L member:userID(A),userID(B)
    L name:groupA
    L photo : photoID(A)  

### セキュリティ考慮(SAME ID)

> CloudFirestoreのセキュリティルールではフィールド単位でセキュリティをかけることができません。
> つまり高いセキュリティを持つドキュメントと公開可能なドキュメントは別々に保持する必要があります。

#### 1_subcollection

> 以下secureサブコレクションの配下にセキュリティルールを儲ける

users
    - user A
     L name:A
     L UID:UID
        A_UID
         - secure
            L address:Tokoy
            
#### SameIDを使用したセキュア

users
    - user A
        L name:A
        L UID:UID

secure users（セキュアなルールを儲ける）

secure_users
    - user A
     L name:A
     L UID:UID(usersのUIDと同じ）

#### SameIDを使用したソーシャルアプリ

users
    - user A
     L name:A
     L UID:UID

Social_users 
    - user A
     L folowwcount
     L followercount

> （フォロワーとフォローはリアルタイム性が必要）
> Userデータは自分以外から更新させたくない
> Socialデータは自分以外からの更新できるようにしたい
> 
> 

### Collection Group

>  Cloud FirestoreではCollectionを飛び越えたQueryをリクエストできないからです

>要件
支払い情報はその支払いを行ったユーザーだけが読み込めるまた書き込める
各ユーザーは支払い金額の合計を算出できる
システムでも金額の合計を算出できる

#### 案１

> 以下ではuser,chargeをまとめてqueryすることは不可

user
    - userA
        L name:userA
        L 

charge 
    - userA
     L  

#### 案2

> 以下では同じコレクション内なので、をまとめてqueryすることは可

Root
    - UserA
      L UID:UID
      L name:userA
        
    - Charge
      L
      L

### Junction Collection

> User AからUser Bに送られた招待状が未開封のままである。

Junction 
 - Invitation
    L "fromID": "userA",
    L "toID": "userB",
    L "status": "isUnopened"
        user
         - userA
            L name:
            L 
              Relation
                - Follow
                    userC
         - userB
            L name:
            L 
              Relation
                - Follow
                    userC 


### 上位層をドメインとして定義する

#### 公開ユーザーと非公開ユーザーをPathで分離

> 公開ユーザ
/public/v1/users/:uid
/public/v1/posts/:post

>　非公開ユーザ
/private/v1/users/:uid

#### ドメインによる分離

>例えば今、ECサービスを作っていると仮定しましょう。
このECサービスでは次のようなとても簡単な要件があるとします。
買い物ができる機能
決済ができる機能
このときUserを次のように定義していたとしましょう。


>少し想像して欲しいのですが、この要件でサービスを作り終えました👏🏻👏🏻👏🏻👏🏻
そしてリリースしたとしましょう。
ここで次の要件を追加されました。
[お客さんとメッセージする機能]
さてどうしますか？上位にUserのメッセージに必要なデータも保存しますか？いきなりデータをマイグレーションする必要が発生するかもしれません。
しかし、ドメインによって次のような設計になっていたらどうでしょう。

/payment/v1/users/:user_id
/public/v1/users/:user_id
/message/v1/users/:user_id
    


### レストラン検索アプリ

restaurant(コレクション)
    - resutaurant_A
        name:
        location:
            


## 備忘
statisticsのDBモデリング通りにアップロードしたい
uidをユーザがわのuidと紐付けた。