# React開発ワークフロー

このプロジェクトでは、Reactの開発とSpringへの配備を効率的に行うための2つのワークフローを提供しています。

## 開発中のワークフロー

開発中は、Reactの開発サーバーを使用して即時に変更を確認できます。

### 開発サーバーの起動

以下のいずれかの方法で開発サーバーを起動できます：

```bash
# シェルスクリプトを使用する場合
./start-react-dev.sh

# または、frontendディレクトリ内でnpmコマンドを使用する場合
cd frontend
npm start
```

これにより、Reactの開発サーバーが起動し、`http://localhost:3000`でアクセスできるようになります。
コードを変更すると、ブラウザが自動的に更新され、変更が即時に反映されます。

### 注意点

- 開発サーバーは`http://localhost:3000`で実行されますが、APIリクエストは`http://localhost:8080`（Springサーバー）に送信されます。
- CORSは適切に設定されているため、開発サーバーからのAPIリクエストは正常に機能します。
- WebSocketも同様に設定されています。

## Springへの配備

開発が完了し、変更をSpringアプリケーションに配備する準備ができたら、以下のいずれかの方法でReactアプリをビルドしてSpringの静的リソースディレクトリに配置できます：

```bash
# シェルスクリプトを使用する場合
./build-react-for-spring.sh

# または、frontendディレクトリ内でnpmコマンドを使用する場合
cd frontend
npm run build-for-spring
```

これにより、以下の処理が行われます：

1. Reactアプリがビルドされます
2. `src/main/resources/static/`ディレクトリの内容がクリアされます
3. ビルドされたファイルが`src/main/resources/static/`ディレクトリにコピーされます

### 注意点

- Springアプリケーションを再起動する必要はありません。
- ブラウザで`http://localhost:8080`にアクセスすると、更新されたReactアプリが表示されます。
- 本番環境にデプロイする場合は、このビルド手順を実行してからデプロイしてください。

## 設定ファイル

### application.properties

このプロジェクトでは、`application.properties`ファイルはGitリポジトリから除外されています。これは、このファイルにJWTシークレットキーやAWS認証情報などの機密情報が含まれているためです。

新しく環境をセットアップする場合は、以下の手順に従ってください：

1. `src/main/resources/application.properties.sample`ファイルを`src/main/resources/application.properties`にコピーします。
2. 必要な設定値（JWTシークレットキー、AWS認証情報など）を適切な値に置き換えます。

```bash
cp src/main/resources/application.properties.sample src/main/resources/application.properties
# その後、エディタでapplication.propertiesを開いて必要な値を設定します
```

### 重要な設定項目

- **JWTシークレットキー**: HS256アルゴリズムを使用する場合、キーは256ビット以上の長さが必要です。
- **CORS設定**: 開発環境では`http://localhost:3000`と`http://127.0.0.1:3000`が許可されています。本番環境では適切な値に変更してください。
- **AWS認証情報**: 実際の値は環境変数または外部設定ファイルで提供することをお勧めします。
