# タスク例：ユーザープロフィール編集機能の追加

## タスクの概要
現在のシステムではユーザー情報の閲覧はできますが、ユーザー自身がプロフィール情報を編集する機能がありません。ユーザーが自分のプロフィール情報（名前、メールアドレス、パスワード）を編集できる機能を追加します。

## 実装したい機能の詳細
1. ユーザーが自分のプロフィール情報を編集できるAPIエンドポイントの追加
   - エンドポイント: PUT /api/users/{userId}
   - 編集可能項目: 名前、メールアドレス、パスワード
   - バリデーション: メールアドレスの形式チェック、パスワードの強度チェック（8文字以上、英数字混在）

2. フロントエンドにプロフィール編集画面の追加
   - React コンポーネントの作成
   - フォームバリデーション
   - 成功/エラーメッセージの表示

## 変更が必要なファイル
### バックエンド
- src/main/java/com/auctionmachine/resources/controller/UserController.java
  - プロフィール更新用のエンドポイントを追加
- src/main/java/com/auctionmachine/resources/service/UserService.java
  - ユーザー情報更新ロジックの実装
- src/main/java/com/auctionmachine/resources/model/request/UserUpdateRequest.java
  - 新規作成：ユーザー情報更新リクエストモデル

### フロントエンド
- frontend/src/hooks/useUsers.js
  - ユーザー情報更新APIの呼び出し関数を追加
- frontend/src/page/pageUserProfile.jsx
  - 新規作成：ユーザープロフィール編集ページ
- frontend/src/routers.jsx
  - 新しいページへのルーティングを追加

## 期待される結果
- ログインユーザーが自分のプロフィールページにアクセスし、情報を編集できる
- 不正な入力（無効なメールアドレス形式、弱いパスワードなど）に対してはエラーメッセージが表示される
- 正常に更新された場合は成功メッセージが表示され、更新された情報が画面に反映される

## その他の注意点や要件
- セキュリティ：ユーザーは自分のプロフィールのみ編集可能とし、他のユーザーのプロフィールは編集できないようにする
- パスワード変更時は現在のパスワードの入力を必須とする
- メールアドレス変更時は、新しいメールアドレスの確認メールを送信する機能も検討する（オプション）

## テスト方法
### APIテスト
```bash
# ユーザープロフィール更新
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -d '{"name":"新しい名前","email":"new@example.com","password":"NewPassword123"}'
```

### UI動作確認
1. ログイン後、プロフィールページにアクセス
2. 各項目を編集して保存ボタンをクリック
3. 成功メッセージが表示され、情報が更新されることを確認
