## 仕様

コレクション
  ドキュメント
    コレクション
      ドキュメント

🔳例えばチャットアプリ(1ファイル1MB制約)

rooms
  roomA
  name: "mychat"
    messages
      message1
      from:"alex"
      msg:"Hello World"
      message2
      from:"alex"
      msg:"Hello World"

🔳統計情報取得におけるDB設計
コレクション(userA)
  ドキュメント(統計情報)
コレクション(userb)
  ドキュメント(統計情報)

コレクション（統計情報）
  ドキュメント(ユーザA統計情報)
  ドキュメント(ユーザB統計情報)

# スケーラビリティ:
この設計では、すべてのユーザの統計情報が単一のコレクション内に格納されます。ユーザ数が増えるにつれて、単一のコレクション内のドキュメント数が増加し、Firestoreの制限に達する可能性があります。Firestoreは単一のコレクション内のドキュメント数に制限を設けており、大規模なアプリケーションではそれを考慮する必要があります。
# セキュリティ:
すべてのユーザの統計情報が同じコレクション内に格納されるため、データのセキュリティが保護されません。ユーザが誤って他のユーザの統計情報にアクセスする可能性があります。

# クエリの複雑性:
特定のユーザの統計情報を取得する際に、コレクション全体からクエリを実行する必要があります。この場合、クエリの複雑性が増し、パフォーマンスが低下する可能性があります。

🔳備忘
uidをもとに各ユーザごとのコレクションを作成するが
uidはfirebase authで認証機能を搭載してから
