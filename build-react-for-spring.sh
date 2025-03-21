#!/bin/bash

# Reactアプリをビルドしてスプリングの静的リソースディレクトリに配置するスクリプト
echo "Reactアプリをビルドしています..."
cd frontend && npm run build

echo "ビルドされたファイルをSpringの静的リソースディレクトリにコピーしています..."
rm -rf ../src/main/resources/static/*
cp -r build/* ../src/main/resources/static/

echo "完了しました！"
echo "Springアプリケーションを再起動せずに、ブラウザでhttp://localhost:8080にアクセスして変更を確認できます。"
