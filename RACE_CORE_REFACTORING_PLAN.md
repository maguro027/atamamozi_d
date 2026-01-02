# Race_Core リファクタリング計画 (Ver.1.5)

## 概要

このドキュメントは、`Race_Core.java` の段階的なリファクタリング計画を説明しています。
**RaceBuilderExample.javaの統合パターンに基づいて、命名規則の統一とStream API導入を進めます。**

---

## フェーズ 1: フィールド名の camelCase 統一

### 現在の PascalCase フィールド
```java
// 現在
public static ArrayList<Race> Race_list = new ArrayList<>();
public static ArrayList<Race_Runner> Race_Runner_List = new ArrayList<>();
public static HashMap<UUID, Race_Runner> Race_Runner_Map = new HashMap<>();
public static HashMap<UUID, ArrayList<Race_Runner>> Race_Run = new HashMap<>();
public static ArrayList<Race_Timer> Timers = new ArrayList<>();
public static ArrayList<String> TOP_MENU = new ArrayList<>();
```

### 改善後の camelCase フィールド
```java
// 改善後
public static ArrayList<Race> raceList = new ArrayList<>();
public static ArrayList<Race_Runner> raceRunnerList = new ArrayList<>();
public static HashMap<UUID, Race_Runner> raceRunnerMap = new HashMap<>();
public static HashMap<UUID, ArrayList<Race_Runner>> raceRunners = new HashMap<>();
public static ArrayList<Race_Timer> timers = new ArrayList<>();
public static ArrayList<String> topMenu = new ArrayList<>();
```

### 影響するファイル
- `Race_Core.java` (すべてのメソッド内での参照)
- `Menus.java` (Race_list への参照)
- `Main.java` (Race_list, Race_Runner_List, Race_packages への参照)
- `Core.java` (Race_Run, TOP_MENU への参照)
- `Event.java` (複数の Race_Core フィールド参照)
- `Race_Runner.java` (Race_Runner_List, Race_Run への参照)
- `Race_Scoreboard.java` (Race_Run への参照)

---

## フェーズ 2: RaceBuilderExample との統合

### 現在の addRace メソッド (問題箇所)
```java
// 行75: 構文エラー
public static void addRace(Race Race) {
     Race_packages.add(new Race_Package));  // ← 不完全
     // 他の処理...
}
```

### 改善後の addRace メソッド (RaceBuilder統合版)
```java
/**
 * RaceBuilderで作成されたRaceオブジェクトをシステムに登録。
 * 
 * <pre>
 * // 使用例 (RaceBuilderExample参照)
 * RaceBuilder builder = RaceBuilderSession.startSession(player);
 * builder.name("MyRace").type(Race_Type.BOAT).icon(Material.BOAT);
 * Race race = builder.build();
 * Race_Core.addRace(race);  // ← このメソッドに登録
 * </pre>
 */
public static void addRace(Race race) {
     if (race == null) {
          return;
     }
     
     // 既に同じIDのレースが存在しないかチェック
     if (raceList.stream().anyMatch(r -> r.getRace_ID().equals(race.getRace_ID()))) {
          return;
     }
     
     // リストに追加
     raceList.add(race);
     
     // 永続化: Race_Package をJSON保存
     Race_Package pkg = new Race_Package(
          race.getRace_ID(),
          race.getCreator(),
          race.getRace_name(),
          race.getJoin_Amount(),
          race.getRap(),
          race.getRace_Type(),
          race.getIcon());
     // CreateJson.saveRacePackage(pkg); // ← 要実装
}
```

### joinRace メソッドの改善
```java
/**
 * プレイヤーがレースに参加 (参加フロー最適化版)
 */
public static void joinRace(Race race, Player player) {
     if (race == null || player == null) {
          return;
     }
     
     // 既に参加していないかチェック
     if (getRunner(player) != null && getRunner(player).getRaceID().equals(race.getRace_ID())) {
          player.sendMessage(CollarMessage.setWarning() + "既にこのレースに参加しています");
          return;
     }
     
     // 参加人数チェック
     ArrayList<Race_Runner> currentParticipants = raceRunners.getOrDefault(race.getRace_ID(), 
          new ArrayList<>());
     if (currentParticipants.size() >= race.getJoin_Amount()) {
          player.sendMessage(CollarMessage.setWarning() + "参加定員に達しています");
          return;
     }
     
     // Race_Runner 生成
     Race_Runner runner = new Race_Runner(player, race.getRace_ID(), race.getRace_Type());
     
     // 3つのコレクションに登録
     raceRunnerList.add(runner);
     raceRunnerMap.put(player.getUniqueId(), runner);
     currentParticipants.add(runner);
     
     if (!raceRunners.containsKey(race.getRace_ID())) {
          raceRunners.put(race.getRace_ID(), currentParticipants);
     }
     
     // 参加メッセージ送信
     sendJoinMessage(race, player);
}
```

---

## フェーズ 3: Stream API 活用で重複ロジック削減

### 現在のコード例 (removeRunner 内)
```java
// 現在: 繰り返しループ + 条件判定
for (UUID a : Race_Run.keySet())
     if (run.getRaceID() != null && run.getRaceID().equals(a) && Race_Run.get(a) != null)
          Race_Run.get(a).remove(run);
```

### 改善後の Stream API 版
```java
// 改善後: Stream + filter + forEach
raceRunners.values().stream()
     .forEach(runners -> runners.removeIf(r -> r.getPlayer().getUniqueId().equals(player.getUniqueId())));
```

### その他の Stream API 活用例

**getRace メソッド の改善**
```java
// 現在
public static Race getRace(String raceName) {
     if (Race_list.isEmpty())
          return null;
     for (Race val : Race_list)
          if (val.getRace_name().equals(raceName))
               return val;
     return null;
}

// 改善後
public static Race getRace(String raceName) {
     return raceList.stream()
          .filter(r -> r.getRace_name().equals(raceName))
          .findFirst()
          .orElse(null);
}
```

**getRunner メソッド の改善**
```java
// 改善後
public static Race_Runner getRunner(Player player) {
     if (player == null) {
          return null;
     }
     
     // マップから高速検索
     Race_Runner mapped = raceRunnerMap.get(player.getUniqueId());
     if (mapped != null) {
          return mapped;
     }
     
     // フォールバック: ストリーム検索 + キャッシング
     Optional<Race_Runner> found = raceRunnerList.stream()
          .filter(r -> r.getPlayer().getUniqueId().equals(player.getUniqueId()))
          .findFirst();
     
     if (found.isPresent()) {
          raceRunnerMap.put(player.getUniqueId(), found.get());
          return found.get();
     }
     
     return null;
}
```

**isJoin メソッド の改善**
```java
// 改善後: Optional パターン
public static boolean isJoin(Player player) {
     return Optional.ofNullable(getRunner(player))
          .map(r -> r.getMode() != Race_Runner_Mode.NO_ENTRY)
          .orElse(false);
}
```

---

## フェーズ 4: Optional パターンで null 安全性向上

### 現在のコード (null チェック多用)
```java
if (run == null)
     return;
Race race = getRace(run.getRaceID());
if (race == null)
     return;
```

### 改善後のコード (Optional活用)
```java
Optional<Race_Runner> runnerOpt = Optional.ofNullable(getRunner(player));
if (!runnerOpt.isPresent()) {
     return;
}

Race_Runner run = runnerOpt.get();
Optional<Race> raceOpt = Optional.ofNullable(getRace(run.getRaceID()));

if (raceOpt.isPresent()) {
     raceList.remove(raceOpt.get());
}

// または ifPresent を使用
raceOpt.ifPresent(race -> raceList.remove(race));
```

---

## フェーズ 5: ヘルパーメソッドで責務分割

### removeRunner 内の複雑なロジックを分割
```java
// 内部ヘルパーメソッド (private)
private static void handleActiveRaceExit(Race_Runner run) { ... }
private static void removeRunnerFromCollections(Player player, UUID raceId) { ... }
private static void stopTimerIfNeeded(UUID raceId) { ... }
private static void checkAllGoalCondition(UUID raceId) { ... }
private static void clearPlayerScoreboard(Player player) { ... }
```

### メッセージ送信ヘルパー
```java
private static void sendJoinMessage(Race race, Player newPlayer) { ... }
private static void sendLeaveMessage(Race race, Player leavingPlayer) { ... }
private static void notifyEditorsRaceStatus(UUID raceId, Race_Mode mode) { ... }
```

---

## フェーズ 6: Method Renaming で API 一貫性確保

### 現在の名前 → 改善後の名前

| 現在 | 改善後 | 理由 |
|------|--------|------|
| `Race_Start` | `raceStart` | camelCase統一 |
| `AllGoal` | `allGoal` | camelCase統一 |
| `Race_Goal` | `raceGoal` | camelCase統一 |
| `JoinMesseage` | `sendJoinMessage` | タイプミス修正 + 責務明確化 |
| `LeaveMesseage` | `sendLeaveMessage` | タイプミス修正 + 責務明確化 |
| `RemoveCar` | `removeCar` | camelCase統一 |

---

## 改善による効果

### 1. **命名規則の統一**
- PascalCase → camelCase で Java慣例に準拠
- すべてのフィールド・メソッドが統一規約に従う

### 2. **コードの可読性向上**
- Stream API で複雑なループロジックを簡潔化
- Optional パターンで null チェック戻り値を明確化

### 3. **保守性の向上**
- ヘルパーメソッド分割で各メソッドの責務が明確
- RaceBuilder との統合で新しいレース追加フローが一貫

### 4. **バグリスク低減**
- null チェックを Optional で統一 → NullPointerException削減
- メソッド分割で副作用が限定される

### 5. **永続化の準備**
- addRace() で Race_Package を JSON保存対応
- CreateJson.saveRacePackage() の実装準備

---

## 実装スケジュール

### Phase 1 (Day 1)
- Race_Core.java フィールド名 camelCase化 (raceList, raceRunnerList等)
- すべての依存ファイル更新 (Menus.java, Main.java, Core.java等)

### Phase 2 (Day 1-2)
- addRace() メソッド の修正 + RaceBuilder統合
- joinRace() 参加フロー最適化

### Phase 3-4 (Day 2)
- Stream API 導入
- Optional パターン導入

### Phase 5 (Day 3)
- ヘルパーメソッド分割
- メソッド命名規則統一

### Phase 6 (Day 3)
- テスト実行
- JavaDoc更新

---

## 注意事項

1. **既存レースデータの互換性**
   - メソッド変更後、既存の Race_Core.Race_list データは自動的に使用可能

2. **JSON永続化**
   - `CreateJson.saveRacePackage(Race_Package)` メソッド実装が必要
   - Race_Package はimmutableなので永続化に適している

3. **テストカバレッジ**
   - 各メソッドの動作検証が重要
   - 特に removeRunner() のモード別処理をカバー

4. **レース実行中の変更**
   - Phase 1-2 はコンパイルできるまで完了
   - その後、段階的に Phase 3-5 を進める推奨

---

## Code Sample: 統合版 addRace()

```java
/**
 * RaceBuilderで作成されたRaceオブジェクトをシステムに登録します。
 * 
 * @param race RaceBuilder で構築された Race オブジェクト
 */
public static void addRace(Race race) {
     if (race == null) {
          Bukkit.getLogger().warning("Race_Core: null race を登録しようとしました");
          return;
     }

     // 既に同じIDのレースが存在しないかチェック (Stream版)
     if (raceList.stream().anyMatch(r -> r.getRace_ID().equals(race.getRace_ID()))) {
          Bukkit.getLogger()
               .warning("Race_Core: 既に ID " + race.getRace_ID() + " のレースが存在します");
          return;
     }

     // リストに追加
     raceList.add(race);

     // 永続化: Race_Package をJSON保存
     Race_Package pkg = new Race_Package(
          race.getRace_ID(),
          race.getCreator(),
          race.getRace_name(),
          race.getJoin_Amount(),
          race.getRap(),
          race.getRace_Type(),
          race.getIcon());
     // CreateJson.saveRacePackage(pkg); // <- 要実装

     Bukkit.getLogger()
          .info("レース登録: " + race.getRace_name() + " (ID: " + race.getRace_ID() + ")");
}
```

---

## RaceBuilderExample との接続

```java
// RaceBuilderExample.java の finishRaceCreation() メソッド
public static void finishRaceCreation(Player player) {
     RaceBuilder builder = RaceBuilderSession.getSession(player);
     if (builder == null) {
          player.sendMessage(CollarMessage.setWarning() + "レース作成セッションがありません");
          return;
     }

     // 最終的なRaceオブジェクトを作成
     Race race = builder.build();

     // ↓ 改善後のrace_Coreに登録
     Race_Core.addRace(race);  // ← camelCase + Builder統合版

     // セッションを終了
     RaceBuilderSession.endSession(player);

     player.sendMessage(CollarMessage.setInfo() + "§aレースを作成しました！");
}
```

---

## 参考リンク

- **RaceBuilderExample.java**: `src/main/java/.../race/RaceBuilderExample.java`
- **RaceBuilder.java**: `src/main/java/.../race/RaceBuilder.java`
- **Race_Package.java**: `src/main/java/.../race/Race_Package.java`
- **Stream API docs**: https://docs.oracle.com/javase/tutorial/collections/streams/

---

**Ver.1.5 の目標達成に向けて、段階的に進めていきます！**
