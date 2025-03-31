#!/bin/bash

# .reloadtriggerファイルを更新してSpringアプリケーションの再起動をトリガーするスクリプト

# 現在の日時を取得
CURRENT_DATE=$(date "+%Y-%m-%d %H:%M:%S")

# .reloadtriggerファイルを更新
echo "# このファイルを変更するとSpring Bootアプリケーションが再起動します" > .reloadtrigger
echo "# 現在の時刻: $CURRENT_DATE" >> .reloadtrigger
echo "# トリガー更新 - コード変更の反映" >> .reloadtrigger

echo "Spring Bootアプリケーションの再起動をトリガーしました。"
echo "変更が反映されるまで少々お待ちください..."
