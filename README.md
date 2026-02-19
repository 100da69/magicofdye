# Magic of Dye (Minecraft 1.21.1 / NeoForge)

このリポジトリは **NeoForge版** の Magic of Dye です。

## 実装済み（現段階）
- Hue 7色 + White の状態管理（プレイヤー単位）
- Toneポイント（0..20）および厳密ルール
  - 0..4 Light / 5..9 Base / 10 直前Tone維持（初期はNull）/ 11..15 Tinted / 16..20 Dark
- White時のTone Null化
- 全Hue=20到達時のKey（10秒）
  - Key中は被ダメージ無効
  - Key終了時に吸収ダメージの80%を `indirect_magic` で返還
  - Key中はTone自然減衰停止
- 3秒ごとの自然減衰（Hue/Tone）
- 食品4カテゴリ（Side/Carbs/Drink/Main）
- 確定仕様の染料IDテーブル（Dye depot + Vanilla）のHue/Tone連携
- コマンド
  - `/magicofdye status`
  - `/magicofdye drain`
  - `/magicofdye apply <category> <dye_id>` （デバッグ用）

## 未実装（次段階）
- 左クリック媒介魔法（剣/棒/エメラルド/ボウル）
- 色相相性（補色/近似/反対/同系）による最終ダメージ補正
- 食品クラフト結果へ染料IDを埋め込む処理
- 永続化（再ログイン・再起動後の状態保持）

## IntelliJ IDEA での動作確認
可能です。Java 21 を設定し、Gradle同期後に `runClient` を使って確認してください。

> 注意: この実行環境では外部Maven到達制限のため、依存解決を伴う最終ビルド確認は行えない可能性があります。

## runClient で `com/towadaroku/magicofdye/...` がコンパイルされる場合
提示されたエラーは、このリポジトリに存在しない旧API向けソース
（`net.neoforged.modloader.api.*` / `net.minecraftforge.network.*` など）を
同じプロジェクトへ混在させたときに発生します。

対処:
1. `src/main/java/com/towadaroku/magicofdye/` 配下の旧コードを削除または別プロジェクトへ退避
2. 本リポジトリの `com.magicofdye` のみをビルド対象にする
3. Gradleリフレッシュ後に `runClient` を再実行

このリポジトリでは安全策として `build.gradle` で
`com/towadaroku/magicofdye/**` をビルド除外しています。


## `Task 'runClient' not found` が出る場合
`net.neoforged.moddev` では、実行タスクは `neoForge.runs` 定義から生成されます。
このリポジトリでは `client/server/data` を明示定義したため、
Gradle同期後に `runClient` / `runServer` / `runData` が表示される想定です。

確認手順:
1. IntelliJ で Gradle リロード
2. ターミナルで `./gradlew tasks --all`（Windowsは `gradlew tasks --all`）
3. `runClient` が表示されることを確認

表示されない場合:
- 別フォルダを開いていないか（`settings.gradle` があるルートを開く）
- `File > Settings > Build Tools > Gradle` の Gradle JVM が Java 21 か
- オフラインモードが有効になっていないか
