# atamamozi_d

このリポジトリは Bukkit / Spigot プラグインです。以下に CI (GitHub Actions) と VS Code でのローカルタスクの実行方法をまとめます。

## CI（GitHub Actions）

- ブランチ push / pull request に対してビルドが実行されます。
- Java 1.8 (Temurin) を使い、Maven の package を実行します（デフォルトでテストはスキップしています）。
- 成果物（target/*.jar）をビルドアーティファクトとして保存します。

ワークフロー定義ファイル:

- `.github/workflows/maven-ci.yml` — build + tests, static analysis, coverage and artifact upload
- `.github/workflows/release.yml` — build and create a GitHub Release when a tag like `v*` is pushed or when run manually

## ローカルでのビルド（Windows, cmd）

以下のコマンドをプロジェクトルートで実行してください。

```
mvn -B -DskipTests package
```

テストを実行する場合:

```
mvn -B test
```

## VS Code のタスク

.vscode/tasks.json に以下のタスクを追加済みです。コマンドパレットからも実行可能です。

- Maven: clean
- Maven: package (skip tests)
- Maven: package (run tests)
- Maven: test

---

現在の CI (maven-ci.yml) が行うこと:

- ビルド & tests: `mvn -B -DskipTests=false verify` を実行してテストを実行します（テストが無い場合はすぐにスキップされます）。
- 静的解析: Checkstyle / PMD / SpotBugs のレポートを生成します（`target/site/` に出力）。
- カバレッジ: JaCoCo のレポートを生成します（`target/site/jacoco/`）。
- アップロード: ビルド済 JAR、テストレポート（`target/surefire-reports/`）、解析・カバレッジレポート（`target/site/**`）を Actions のアーティファクトとして保存します。

リリース (release.yml): タグ `v*` を push するか Actions の手動実行 (workflow_dispatch) でトリガーできます。リリース実行時にはビルドして成果物 JAR を作成し、GitHub Release としてアップロードします。

---

もし CI で失敗させたい静的解析ルール（例: Checkstyle violations を fail にする）や、追加の解析/テスト（FindBugs/SpotBugs ルール厳密化、コードカバレッジ閾値など）を設定したければ、pom.xml にプラグイン設定を追加し、CI をそれに合わせて調整できます。

次に何を変更しましょうか？ (例: Checkstyle ルール追加、パッケージングの自動リリース to Maven Central など)
