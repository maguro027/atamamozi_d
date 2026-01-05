# Git リポジトリ破損原因分析レポート

## 概要
2026年1月5日に atamamozi_d リポジトリが深刻な破損状態に陥りました。複数のブランチのオブジェクトが missing となり、リポジトリが機能不全に陥った状態でした。

---

## 破損の症状

### 検出されたエラー
```
1. fatal: missing object 853d542fbdc9cc0d6dcfa25fdc03bbf3a8ab4e2d for refs/heads/Ver.1
2. fatal: unable to read tree (9c0ce165e2aa8d5eaec84bfcc9af6adaa3e8cd82) for Ver.2.0
3. fatal: missing object c345db44179a5c174b6ce809efcd2f810d6a3cd5 for refs/remotes/origin/HEAD
```

### fsck 結果概要
- **Missing オブジェクト**: 複数（commit, tree, blob）
- **Broken リンク**: 複数のコミット・ツリー間の参照が壊れている
- **Dangling オブジェクト**: 約200個以上

---

## 破損原因の特定

### 1. **リモートフェッチ時の不完全なダウンロード**
最も可能性が高い原因：
- GitHub からのフェッチ/プッシュ中にネットワーク接続が断絶
- パッケージダウンロード時の中断により、一部のオブジェクトのみがローカルに保存
- 参照（refs）は更新されたが、実体のオブジェクトが不在

### 2. **并行な Git 操作による競合**
- 複数のプロセスが同時にリポジトリにアクセス（VS Code + git cli など）
- .git/objects ディレクトリへの同時書き込み

### 3. **pack-refs ファイルの不一致**
```
packed-refs ファイルに記録されたコミット:
- f6a4cc578797b5f7eacb62bcf1f8d39542da0b36 (Ver.1.5) ✅ 存在
- c52a5670922eba67ece17548a38898aeba84c082 (Ver.2.0) ✅ 存在

実際の .git/objects/ には存在するが:
- 過去のコミット履歴の中間オブジェクトが未解決参照として存在
```

### 4. **ローカルブランチと GitHub 上の不整合**
GitHub の状態:
- Ver.1, Ver.1.5, Ver.2.0 が存在
- HEAD が Ver.1 を指していた（現在は Ver.2.0）
- 削除されたはずのブランチの参照が残存

---

## 破損が起きたタイミング

### 可能性のある操作シーケンス

```
1. Ver.1.5 ブランチで作業中
2. 新しいブランチ Ver.2.0 を作成・プッシュ
   ↓
3. ローカルとリモート間でフェッチ/プッシュ実行
   ↓
4. VS Code が同時にリポジトリ参照を読み取り
   ↓
5. ネットワーク遅延またはプロセス競合でオブジェクト未同期
   ↓
6. GitHub HEAD が Ver.1 を指したまま
   ↓
7. 複数の git コマンドがこの不整合を読み込み
   ↓
8. 参照の循環参照 + オブジェクトの欠損発生
```

---

## 破損が進行した理由

### 1. **Git の参照キャッシュ機構**
一度 missing オブジェクトが検出されると：
- その参照を含むすべての操作が失敗
- `git fsck` 実行により dangling オブジェクトが増加
- エラー修復の試みが新たなエラーを生成

### 2. **Packed-refs の不完全な更新**
```
packed-refs は複数の参照を1ファイルで管理するため：
- 部分的な更新でファイルが破損する可能性
- loose refs（個別ファイル）との同期が失われる
```

### 3. **HEAD 参照の矛盾**
```
refs/remotes/origin/HEAD が Ver.1 を指す
↓
リモートに対する操作が Ver.1 を基準とする
↓
Ver.2.0 への操作と矛盾
↓
オブジェクト解決エラーの連鎖
```

---

## 修復方法（実施済み）

### 採用した修復戦略
```
1. 既存の破損ブランチ（Ver.1, Ver.2.0）を削除
2. 健全な main ブランチから新しい Ver.2.0 を作成
3. 新しい Ver.2.0 を GitHub にプッシュ（強制上書き）
4. origin HEAD を Ver.2.0 に設定
```

### なぜこの方法で解決したか
- `git gc` や `git fsck --lost-found` では部分的な修復に止まる
- 破損した歴史を持つブランチは削除が最善
- 新規ブランチ作成により、すべてのオブジェクトが fresh な状態

---

## 今後の予防策

### 1. **リモート操作時のネットワーク保護**
```bash
# タイムアウト設定を増加
git config --global core.longpaths true
git config --global http.lowSpeedLimit 1000
git config --global http.lowSpeedTime 30
```

### 2. **並行操作の制御**
- VS Code との自動フェッチ機能を一時的に無効化
- 大規模な操作（歴史的なフェッチ）は手動実行

### 3. **定期的なリポジトリ検証**
```bash
# 定期的に実行
git fsck --full --strict

# 破損が軽微の場合のみ実行
git gc --aggressive
```

### 4. **GitHub のデフォルトブランチ設定**
- **重要**: GitHub Settings で Default branch を明示的に設定
- `refs/remotes/origin/HEAD` との整合性を確認

### 5. **バックアップ戦略**
```
- ローカルリポジトリは定期的にバックアップ
- GitHub のブランチ保護ルール設定
- 重要なコミットは複数リモートに push
```

### 6. **git config の設定**
```ini
[core]
    # 自動 GC を有効化（オブジェクト整理）
    autoGC = true

[fetch]
    # フェッチ時に自動的に破損チェック
    fsckObjects = true

[push]
    # プッシュ時に破損チェック
    checkReceiveObjects = true
```

---

## チェックリスト（再発防止）

- [ ] GitHub Settings で Default branch が明示的に設定されている
- [ ] `git remote set-head origin <branch>` で origin HEAD が正しい
- [ ] `git fsck --full --strict` で定期的に検証
- [ ] ネットワーク設定タイムアウトを確認
- [ ] VS Code の自動フェッチ設定を確認
- [ ] 重要な操作の前にはローカル backup を作成

---

## 参考資料

- [Git - git-fsck Documentation](https://git-scm.com/docs/git-fsck)
- [Git - git-gc Documentation](https://git-scm.com/docs/git-gc)
- [Git Internals - Git Objects](https://git-scm.com/book/en/v2/Git-Internals-Git-Objects)
- [GitHub - Refs and the reflog](https://git-scm.com/docs/git-reflog)

---

**分析日時**: 2026年1月5日
**リポジトリ**: maguro027/atamamozi_d
**ステータス**: ✅ 修復完了
