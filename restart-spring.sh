#!/bin/bash

# Springサーバーを再起動するスクリプト

echo "現在実行中のSpringサーバーを停止しています..."
pkill -f "gradlew bootRun"

echo "Springサーバーを起動しています..."
./gradlew bootRun
