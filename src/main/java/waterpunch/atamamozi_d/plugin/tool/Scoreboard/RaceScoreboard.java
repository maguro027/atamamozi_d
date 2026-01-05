package waterpunch.atamamozi_d.plugin.tool.Scoreboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import waterpunch.atamamozi_d.plugin.race.domain.Race;
import waterpunch.atamamozi_d.plugin.race.domain.RacePlayer;

/**
 * レースの進行状況をスコアボードに表示します。
 *
 * <p>
 * 責務:
 * <ul>
 * <li>RacePlayer の進行状況から表示内容を構築（buildLines）</li>
 * <li>Bukkitスコアボードオブジェクトの生成（buildBoardFromLines）</li>
 * <li>現在のゲーム状態に応じた表示切り替え</li>
 * </ul>
 * </p>
 *
 * <p>
 * このクラスは RacePlayer と Race オブジェクトの状態から、
 * プレイヤー向けのスコアボード行を動的に生成します。
 * </p>
 */
public class RaceScoreboard {

    /** Bukkitスコアボードオブジェクト */
    private Scoreboard board;
    /** スコアボードのObjective */
    private Objective objective;

    /**
     * 行リストから実際のScoreboardオブジェクトを構築します。
     *
     * <p>
     * 事前に buildLines() で内容を作成し、内容が変わっていない場合の
     * 再構築を避けることをお勧めします。
     * </p>
     *
     * @param lines スコアボードに表示する行のリスト
     * @return 構築されたScoreboardオブジェクト
     */
    public Scoreboard buildBoardFromLines(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return null;
        }

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) {
            return null;
        }

        board = manager.getNewScoreboard();

        objective = board.registerNewObjective("Stats", "dummy", "a");

        objective.setDisplayName("Atamamozi_" + ChatColor.RED + "D");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // 行を逆順にして、スコアを 0 から割り当てる
        // （Bukkit のスコアボードは高いスコアが上に表示される）
        List<String> tmp = new ArrayList<>(lines);
        Collections.reverse(tmp);

        ArrayList<Score> scores = new ArrayList<>();
        for (String s : tmp) {
            scores.add(objective.getScore(s));
        }

        for (int i = 0; i < scores.size(); i++) {
            scores.get(i).setScore(i);
        }

        return board;
    }

    /**
     * RacePlayer の状態からスコアボード表示行を構築します。
     *
     * <p>
     * プレイヤーの状態（JOINED, READY, RUNNING, FINISHED, SPECTATING）に応じて
     * 異なる情報を表示します。
     * </p>
     *
     * @param racePlayer RacePlayer オブジェクト
     * @param race       Race オブジェクト
     * @return スコアボードに表示する行のリスト。表示不可な場合は null
     */
    public List<String> buildLines(RacePlayer racePlayer, Race race) {
        // 基本的なバリデーション
        if (racePlayer == null || race == null) {
            return null;
        }

        ArrayList<String> lines = new ArrayList<>();

        // ヘッダー
        lines.add("[" + ChatColor.AQUA + "RACE" + ChatColor.WHITE + "]");
        lines.add(race.getRaceName());

        // プレイヤーの状態に応じて表示内容を変更
        switch (racePlayer.getState()) {
            case JOINED:
                // 参加済み状態：カウントダウン表示
                lines.add(ChatColor.YELLOW + "WAITING");
                // TODO: カウントダウン値を RaceSession から取得
                lines.add("/atd " + ChatColor.AQUA + "start");
                lines.add("[" + ChatColor.AQUA + "ENTRY" + ChatColor.WHITE + "]");
                // TODO: セッション内の他のプレイヤー一覧を表示
                break;

            case READY:
                // レディ状態：カウントダウン表示
                lines.add(ChatColor.YELLOW + "READY");
                // TODO: カウントダウン値を表示
                break;

            case RUNNING:
                // レース実行中：タイム、ラップ、チェックポイント表示
                lines.add("Time : ");
                lines.add(racePlayer.getFormattedTime());

                lines.add("Lap  : ");
                lines.add(racePlayer.getCurrentLap() + " / " + race.getRap());

                lines.add("CheckPoint : ");
                lines.add(racePlayer.getCurrentCheckpoint() + " / " + race.getCheckPointLoc().size());

                // 速度情報（キャッシュされた値を使用）
                int speed = calculateSpeed(racePlayer);
                lines.add("SPEED : " + speed);
                break;

            case FINISHED:
                // ゴール済み：タイムとランキング情報を表示
                lines.add(ChatColor.GREEN + "FINISHED!");
                lines.add("Time : ");
                lines.add(racePlayer.getFormattedTime());
                // TODO: ランキング情報を表示
                break;

            case SPECTATING:
                // 観戦モード：レース情報を表示
                lines.add(ChatColor.GRAY + "SPECTATING");
                // TODO: 他のプレイヤーのランキング等を表示
                break;

            default:
                return null;
        }

        return lines;
    }

    /**
     * RacePlayer の速度を計算します。
     *
     * <p>
     * プレイヤーの速度ベクトルから3D速度（blocks/tick）を計算し、
     * blocks/second に変換して表示します。
     * </p>
     *
     * @param racePlayer RacePlayer オブジェクト
     * @return 速度（blocks/second、整数値）
     */
    private int calculateSpeed(RacePlayer racePlayer) {
        if (racePlayer == null || racePlayer.getPlayer() == null) {
            return 0;
        }

        // プレイヤーの速度ベクトルを取得
        org.bukkit.util.Vector velocity = racePlayer.getPlayer().getVelocity();
        if (velocity == null) {
            return 0;
        }
        
        // 3D速度の大きさを計算（blocks/tick）
        // length() は √(x² + y² + z²) を返す
        double speedPerTick = velocity.length();
        
        // blocks/tick から blocks/second に変換
        // Minecraftは1秒間に20ティック実行される
        double speedPerSecond = speedPerTick * 20.0;
        
        // 整数に丸める
        return (int) Math.round(speedPerSecond);
    }

    /**
     * 構築されたScoreboardを取得します。
     *
     * @return Scoreboardオブジェクト
     */
    public Scoreboard getBoard() {
        return board;
    }

    /**
     * 構築されたObjectiveを取得します。
     *
     * @return Objectiveオブジェクト
     */
    public Objective getObjective() {
        return objective;
    }
}
