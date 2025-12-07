# Atamamozi_D

Bukkit/Spigot レースプラグイン - プレイヤー同士が競走できるレースシステム

## デモ動画

[プラグインのデモ動画はこちら](https://drive.google.com/file/d/1-AKq-8x0gPaDmMF9oiVwFKusLaYxrQYu/view)

## 概要

Atamamozi_D は、Minecraft サーバー用の包括的なレースプラグインです。プレイヤーはカスタムレースコースを作成し、チェックポイントベースのレースに参加できます。

## 主な機能

- **レース作成**: カスタムレースコースとチェックポイントの作成
- **複数のレースタイプ**: 徒歩レース、ボートレース対応
- **ランキングシステム**: プレイヤースコアと順位の追跡
- **GUI メニュー**: 直感的なインベントリベースのインターフェース
- **看板サポート**: レース参加用の看板設置
- **スコアボード表示**: リアルタイムのレース進捗表示

## 必要要件

- Java 8 以上
- Spigot/Bukkit 1.18.2 以上

## ビルド方法

```bash
mvn clean package
```

ビルドされたプラグインは `target/atamamozi_d-0.1.jar` に生成されます。

## インストール

1. プラグインjarファイルをサーバーの `plugins/` フォルダにコピー
2. サーバーを再起動
3. `plugins/atamamozi_d/` に設定ファイルが生成されます

## 使用方法

### コマンド

- `/race list` - 利用可能なレース一覧を表示
- `/race create` - 新しいレースを作成
- `/race join <レース名>` - レースに参加
- `/race leave` - 現在のレースから退出
- `/race help` - ヘルプメッセージを表示

### レースの作成

1. `/race create` でレース作成メニューを開く
2. レースタイプ（徒歩/ボート）を選択
3. ラップ数と参加人数を設定
4. スタート地点とチェックポイントを設定
5. レースを保存

### 看板でのレース参加

看板の1行目に `[race]` または `[Race]` と記入し、2行目にレース名を入力すると、右クリックでレースに参加できる看板が作成されます。

## 設定

プラグインの設定は `plugins/atamamozi_d/config.yml` で行えます。

## 開発

### プロジェクト構成

```
src/main/java/waterpunch/atamamozi_d/plugin/
├── event/          # イベントハンドラー
├── main/           # コアプラグインロジック
├── menus/          # GUI メニュー
├── race/           # レースシステム
│   ├── checkpoint/ # チェックポイント管理
│   ├── enums/      # レース関連の列挙型
│   └── export/     # エクスポート機能
├── score/          # スコアリングシステム
└── tool/           # ユーティリティ
    ├── Location/   # 位置表示
    ├── Scoreboaed/ # スコアボード管理
    └── Timers/     # タイマー機能
```

### 依存関係

- Spigot API 1.18.2-R0.1-SNAPSHOT
- Gson (JSON処理)
- SnakeYAML (設定処理)

## ライセンス

このプロジェクトは開発中です。

## 貢献

Issue報告やプルリクエストを歓迎します。

## 作者

waterpunch
