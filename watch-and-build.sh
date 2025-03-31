#!/bin/bash

# fswatch（ファイル監視ツール）を使用して、ファイル変更を監視し、
# 変更があった場合に自動的に./gradlew clean buildを実行するスクリプト

# fswatch（ファイル監視ツール）がインストールされているか確認
if ! command -v fswatch &> /dev/null; then
    echo "fswatch（ファイル監視ツール）がインストールされていません。"
    echo "以下のコマンドでインストールできます："
    echo "  brew install fswatch"
    echo ""
    echo "インストール後に再度このスクリプトを実行してください。"
    exit 1
fi

echo "ファイル変更の監視を開始します..."
echo "変更を検出すると自動的に./gradlew clean buildを実行します"
echo "終了するにはCtrl+Cを押してください"
echo ""

# 監視対象のディレクトリ
WATCH_DIRS="src/main/java src/main/resources"

# 最小間隔（秒）- この間隔内の複数の変更は1回のビルドにまとめられます
MIN_INTERVAL=5

# 最後のビルド時刻を記録
LAST_BUILD=$(date +%s)

# ファイル変更を監視
fswatch -0 -e ".*\\.class$" -e ".*~$" -e ".*\\.swp$" $WATCH_DIRS | while read -d "" event
do
    # 現在の時刻
    CURRENT_TIME=$(date +%s)
    
    # 最後のビルドから最小間隔以上経過している場合のみビルドを実行
    if [ $((CURRENT_TIME - LAST_BUILD)) -gt $MIN_INTERVAL ]; then
        echo "変更を検出しました: $event"
        echo "ビルドを実行します..."
        
        # ビルドを実行
        ./gradlew clean build
        
        # 最後のビルド時刻を更新
        LAST_BUILD=$(date +%s)
        
        echo "ビルドが完了しました。次の変更を監視しています..."
        echo ""
    fi
done
