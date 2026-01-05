# Atamamozi_D Ver.2.0 設計ドキュメント

## 設計思想

Ver.1の課題を解決し、保守性・拡張性・テスト可能性を向上させる。

### Ver.1の課題
- `Race_Core`が静的メソッドだらけで、状態管理が複雑
- 責任が不明確（Race_Runner、Race_Core、Race が混在）
- タイマー、スコアボード、参加者管理が散在
- テストが困難
- イベント駆動への移行が困難

### Ver.2.0の目標
- **単一責任の原則**: 各クラスが明確な責任を持つ
- **イベント駆動設計**: Bukkitイベントを活用
- **依存性注入**: 疎結合な設計
- **テスト可能**: ビジネスロジックとBukkit APIを分離
- **不変性**: 可能な限りイミュータブルに

---

## アーキテクチャ概要

```
┌─────────────────────────────────────────────────────────────┐
│                        Main Plugin                          │
│  (Core.java - プラグインライフサイクル管理)                    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Service Layer                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ RaceManager  │  │PlayerManager │  │ScoreManager  │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Domain Layer                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │    Race      │  │ RaceSession  │  │  RacePlayer  │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   Persistence Layer                         │
│  ┌──────────────┐  ┌──────────────┐                         │
│  │Race_Package  │  │  JsonStore   │                         │
│  └──────────────┘  └──────────────┘                         │
└─────────────────────────────────────────────────────────────┘
```

---

## クラス設計

### 1. Persistence Layer（永続化層）

#### Race_Package.java ✅ **既存のまま保持**
```java
/**
 * レースのJSON保存用データクラス（不変）
 * - 基本情報: creator, raceName, UUID
 * - 設定: joinAmount, rap, raceType, icon
 * - コース: startPoints, checkPoints
 */
```

#### JsonStore.java 🆕
```java
/**
 * JSON保存・読み込みを担当
 * 責務:
 * - Race_Package の保存・読み込み
 * - Player_Score の保存・読み込み
 * - ファイルI/O処理の一元管理
 */
public class JsonStore {
    void saveRace(Race_Package racePackage)
    Race_Package loadRace(UUID raceId)
    List<Race_Package> loadAllRaces()
    void saveScore(Player_Score score)
    Player_Score loadScore(UUID playerId, UUID raceId)
}
```

---

### 2. Domain Layer（ドメイン層）

#### Race.java 🔄 **大幅リファクタリング**
```java
/**
 * レースコースの定義（不変）
 * Race_Package から生成される実行時データ
 * 
 * 責務:
 * - コース情報の保持（チェックポイント、スタート地点）
 * - レース設定の提供（参加人数、周回数、タイプ）
 * - 変更不可（イミュータブル）
 */
public class Race {
    private final UUID raceId;
    private final String raceName;
    private final String creator;
    private final int maxPlayers;
    private final int laps;
    private final RaceType raceType;
    private final Material icon;
    private final List<Location> startPoints;      // 不変
    private final List<CheckPoint> checkPoints;    // 不変
    
    // Race_Package から生成
    public static Race fromPackage(Race_Package pkg) { ... }
    
    // ゲッターのみ（セッターなし）
    public UUID getRaceId() { ... }
    public String getRaceName() { ... }
    // ... その他のゲッター
}
```

#### RaceSession.java 🆕 **NEW**
```java
/**
 * 実行中のレースセッション（可変）
 * 
 * 責務:
 * - レース進行状態の管理
 * - 参加プレイヤーの管理
 * - タイマー制御
 * - 状態遷移（WAITING → COUNTDOWN → RUNNING → FINISHED）
 */
public class RaceSession {
    private final Race race;
    private final Map<UUID, RacePlayer> players;  // プレイヤーID → RacePlayer
    private RaceState state;                       // 状態
    private RaceTimer timer;                       // タイマー
    
    public enum RaceState {
        WAITING,      // 参加者待ち
        COUNTDOWN,    // カウントダウン中
        RUNNING,      // レース実行中
        FINISHED      // レース終了
    }
    
    // プレイヤー管理
    void addPlayer(Player player)
    void removePlayer(Player player)
    RacePlayer getPlayer(UUID playerId)
    int getPlayerCount()
    
    // 状態遷移
    void startCountdown()
    void start()
    void finish()
    
    // タイマー制御
    void setTimer(RaceTimer timer)
    void stopTimer()
}
```

#### RacePlayer.java 🆕 **NEW** (旧 Race_Runner を置き換え)
```java
/**
 * レースに参加しているプレイヤーの状態
 * 
 * 責務:
 * - プレイヤーの進行状況管理
 * - チェックポイント通過記録
 * - タイム計測
 */
public class RacePlayer {
    private final Player bukkitPlayer;
    private final UUID raceId;
    private final int joinOrder;              // 参加順（スタート位置決定用）
    private final Location originalLocation;  // 参加前の位置
    
    // 進行状況
    private int currentLap;
    private int currentCheckpoint;
    private long startTime;
    private long finishTime;
    private PlayerState state;
    
    public enum PlayerState {
        JOINED,       // 参加済み（待機中）
        READY,        // レディ状態
        RUNNING,      // レース中
        FINISHED,     // ゴール済み
        SPECTATING    // 観戦モード
    }
    
    // 進行管理
    void passCheckpoint(int checkpointIndex)
    void completeLap()
    void finish()
    boolean isFinished()
    
    // タイム
    void startTimer()
    long getElapsedTime()
    String getFormattedTime()
}
```

#### CheckPoint.java 🆕
```java
/**
 * チェックポイント定義
 */
public class CheckPoint {
    private final Location location;
    private final int radius;
    
    public boolean isPlayerInside(Player player) {
        return player.getLocation().distance(location) <= radius;
    }
}
```

---

### 3. Service Layer（サービス層）

#### RaceManager.java 🆕 **NEW** (旧 Race_Core を置き換え)
```java
/**
 * レースのライフサイクル管理
 * 
 * 責務:
 * - レースの作成・削除
 * - レースセッションの管理
 * - レース検索
 */
public class RaceManager {
    private final Map<UUID, Race> races;              // レース定義
    private final Map<UUID, RaceSession> sessions;    // 実行中セッション
    private final JsonStore jsonStore;
    
    // レース管理
    Race createRace(RaceBuilder builder)
    void deleteRace(UUID raceId)
    Race getRace(UUID raceId)
    Race getRaceByName(String name)
    List<Race> getAllRaces()
    
    // セッション管理
    RaceSession createSession(Race race)
    void endSession(UUID raceId)
    RaceSession getSession(UUID raceId)
    boolean hasActiveSession(UUID raceId)
    
    // 永続化
    void saveRace(Race race)
    void loadAllRaces()
}
```

#### PlayerManager.java 🆕 **NEW**
```java
/**
 * プレイヤーの参加状態管理
 * 
 * 責務:
 * - プレイヤーの参加・退出処理
 * - プレイヤー検索
 * - プレイヤー状態の追跡
 */
public class PlayerManager {
    private final Map<UUID, RacePlayer> activePlayers;  // プレイヤーID → RacePlayer
    
    // 参加管理
    RacePlayer joinRace(Player player, RaceSession session)
    void leaveRace(Player player)
    RacePlayer getPlayer(UUID playerId)
    boolean isInRace(Player player)
    
    // 検索
    RaceSession getPlayerSession(Player player)
    List<RacePlayer> getPlayersInRace(UUID raceId)
}
```

#### ScoreManager.java 🆕 **NEW**
```java
/**
 * スコア・ランキング管理
 * 
 * 責務:
 * - レース結果の記録
 * - ランキング計算
 * - スコアボード表示
 */
public class ScoreManager {
    private final JsonStore jsonStore;
    
    void recordResult(RacePlayer player, long time)
    List<RaceResult> getRanking(UUID raceId)
    void displayRanking(Player player, UUID raceId)
    void updateScoreboard(RacePlayer player)
}
```

---

### 4. Timer System

#### RaceTimer.java 🔄 **リファクタリング**
```java
/**
 * レースタイマー（BukkitRunnable）
 * 
 * 責務:
 * - カウントダウン処理
 * - 定期的なコールバック
 * - シンプルなタイマー機能のみ
 */
public class RaceTimer extends BukkitRunnable {
    private final RaceSession session;
    private final TimerType type;
    private int remainingTime;
    private final Consumer<Integer> onTick;
    private final Runnable onComplete;
    
    public enum TimerType {
        WAITING,      // 参加者待ち
        COUNTDOWN,    // カウントダウン
        RACE          // レース中
    }
    
    @Override
    public void run() {
        if (remainingTime <= 0) {
            onComplete.run();
            cancel();
            return;
        }
        onTick.accept(remainingTime);
        remainingTime--;
    }
}
```

---

### 5. Event System 🆕

#### イベントクラス
```java
// カスタムイベントを定義してイベント駆動設計を実現

public class RaceStartEvent extends Event { ... }
public class RaceEndEvent extends Event { ... }
public class PlayerJoinRaceEvent extends Event { ... }
public class PlayerLeaveRaceEvent extends Event { ... }
public class PlayerPassCheckpointEvent extends Event { ... }
public class PlayerGoalEvent extends Event { ... }
```

#### EventHandler例
```java
@EventHandler
public void onRaceStart(RaceStartEvent event) {
    RaceSession session = event.getSession();
    // レース開始時の処理
}
```

---

## データフロー

### レース作成フロー
```
1. Player が /atd create コマンド実行
2. RaceBuilder でレース設定を構築
3. RaceManager.createRace() でレース作成
4. JsonStore.saveRace() で永続化
5. Race オブジェクト返却
```

### レース参加フロー
```
1. Player が /atd join <race> コマンド実行
2. PlayerManager.joinRace() 呼び出し
3. RaceSession.addPlayer() でセッションに追加
4. RacePlayer オブジェクト生成
5. PlayerJoinRaceEvent 発火
6. スコアボード更新
```

### レース開始フロー
```
1. 定員到達または /atd start コマンド
2. RaceSession.startCountdown()
3. RaceTimer 開始（COUNTDOWN）
4. カウントダウン終了
5. RaceSession.start()
6. RaceStartEvent 発火
7. 各プレイヤーをスタート地点にテレポート
8. RaceTimer 開始（RACE）
```

### ゴールフロー
```
1. Player がゴールライン通過
2. PlayerPassCheckpointEvent 発火
3. RacePlayer.finish() 呼び出し
4. PlayerGoalEvent 発火
5. ScoreManager.recordResult() でタイム記録
6. 全員ゴール判定
7. RaceSession.finish()
8. RaceEndEvent 発火
9. ランキング表示
```

---

## 移行計画

### Phase 1: 基盤整備
- [x] Player_Scores SQLite化＆起動時自動マイグレーション（DB未存在時にJSON取込み）
- [x] JsonStore 実装
- [x] Race リファクタリング（イミュータブル化）
- [x] RacePlayer 実装

### Phase 2: コア機能
- [ ] RaceSession 実装
- [ ] RaceManager 実装
- [ ] PlayerManager 実装

### Phase 3: タイマー・イベント
- [ ] RaceTimer リファクタリング
- [ ] カスタムイベント実装
- [ ] EventHandler 実装

### Phase 4: UI・スコア
- [ ] ScoreManager 実装
- [ ] スコアボード実装
- [ ] メニューシステム更新

### Phase 5: テスト・最適化
- [ ] ユニットテスト
- [ ] 統合テスト
- [ ] パフォーマンス最適化

---

## 設計原則

1. **Single Responsibility**: 各クラスは1つの責任のみ
2. **Open/Closed**: 拡張に開いて、修正に閉じている
3. **Dependency Inversion**: 抽象に依存し、具象に依存しない
4. **Interface Segregation**: 必要最小限のインターフェース
5. **Immutability First**: 可能な限り不変オブジェクトを使用

---

## データベース保護戦略（破損対策）

### 実装済みの保護機構

#### 1. **WAL（Write-Ahead Logging）**
```java
PRAGMA journal_mode = WAL;
```
- **効果**: クラッシュ時のデータ保護
- **仕組み**: 書き込み前にログに記録してから実行
- **利点**: パフォーマンスと安全性のバランス

#### 2. **トランザクション管理**
```java
beginTransaction()   // 複数操作の開始
commitTransaction()  // 確定
rollbackTransaction()// ロールバック
```
- 複数の操作をアトミックに実行
- エラー時は自動ロールバック

#### 3. **定期自動バックアップ**
- **実行間隔**: 1時間ごと
- **保持数**: 最新10世代
- **実行タイミング**:
  - プラグイン起動時
  - プラグイン終了時
  - 1時間ごとのタスク実行
- **バックアップ先**: `plugins/Atamamozi_D/db_backups/`

#### 4. **整合性チェック**
```java
verifyDatabaseIntegrity()  // PRAGMA integrity_check
```
- 初期化時に自動実行
- 破損検出時はログに記録

#### 5. **最適化タスク**
```java
optimizeDatabase()  // VACUUM と ANALYZE
```
- 定期的なデータベース最適化
- 必要に応じて手動実行可能

### PRAGMA設定の詳細

| 設定 | 値 | 目的 |
|------|------|------|
| `journal_mode` | WAL | クラッシュ時の保護 |
| `synchronous` | NORMAL | パフォーマンス＆安全性のバランス |
| `foreign_keys` | ON | 参照整合性の確保 |
| `auto_vacuum` | INCREMENTAL | 断片化対策 |
| `cache_size` | -64000 | パフォーマンス向上 |
| `busy_timeout` | 30000 | デッドロック対策 |

### リカバリー手順

#### **破損が発生した場合**
```bash
1. プラグインを停止
2. plugins/Atamamozi_D/db_backups/ からバックアップを選択
3. database.restoreFromBackup(backupFile) を呼び出し
4. プラグインを再起動
```

### モニタリング

- バックアップ実行ログ: コンソールに出力
- 整合性チェック結果: コンソールに出力
- エラーログ: プラグインログに記録

---

## 次のステップ

このドキュメントを確認し、同意が得られたら Phase 1 から実装を開始します。

**確認事項:**
1. アーキテクチャは適切か？
2. クラスの責務分担は明確か？
3. 追加・変更したい要素はあるか？
