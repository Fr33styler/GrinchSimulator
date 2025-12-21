package ro.fr33styler.grinch.mysql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.scheduler.BukkitRunnable;

import ro.fr33styler.grinch.Main;
import ro.fr33styler.grinch.cache.PlayerStats;
import ro.fr33styler.grinch.cache.PlayerStatus;

public class MySQL extends BukkitRunnable {

	private int amountQueue;
	private Connection connection;
	private List<PlayerStatus> status;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

	public MySQL(Main main, String host, String database, String username, String password, int port, int amountQueue) {
		try {
			synchronized (main) {
				Class.forName("com.mysql.jdbc.Driver");
				connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database+"?autoReconnect=true", username, password);
			}
			this.amountQueue = amountQueue;
			status = new ArrayList<PlayerStatus>();
			Statement statement = connection.createStatement();
			runTaskTimerAsynchronously(main, 0, 20);
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS GrinchSimulator (id INTEGER NOT NULL AUTO_INCREMENT, UUID VARCHAR(36) UNIQUE, NAME VARCHAR(16), SCORE INTEGER, WINS INTEGER, PLAYED INTEGER, PRIMARY KEY (id))");
			statement.close();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public List<PlayerStatus> getCache() {
		return status;
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public void closeConnection() {
        executor.shutdownNow();
		try {
			Statement statement = connection.createStatement();
			for (PlayerStatus s : this.status) {
				statement.executeUpdate("INSERT INTO GrinchSimulator (UUID, NAME, SCORE, WINS, PLAYED) VALUES ('" + s.getUUID() + "', '" + s.getName() + "', " + s.getScore() + ", " + s.hasWon() + ", 1) ON DUPLICATE KEY UPDATE NAME='" + s.getName() + "', SCORE=SCORE+" + s.getScore() + ", WINS=WINS+" + s.hasWon() + ", PLAYED=PLAYED+1;");
			}
			status.clear();
			statement.close();
			connection.close();
			cancel();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

    public void synchronise(UUID uuid, PlayerStats data) {
        executor.submit(() -> {
            try (PreparedStatement statement =
                         connection.prepareStatement("SELECT WINS, SCORE FROM GrinchSimulator WHERE uuid = ? LIMIT 1;")) {
                statement.setString(1, uuid.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        data.setWins(resultSet.getInt(1));
                        data.setGiftsStolen(resultSet.getInt(2));
                    }
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

	@Override
	public void run() {
		if (status.size() >= amountQueue) {
			try {
				Statement statement = connection.createStatement();
				for (PlayerStatus s : this.status) {
					statement.executeUpdate("INSERT INTO GrinchSimulator (UUID, NAME, SCORE, WINS, PLAYED) VALUES ('" + s.getUUID() + "', '" + s.getName() + "', " + s.getScore() + ", " + s.hasWon() + ", 1) ON DUPLICATE KEY UPDATE NAME='" + s.getName() + "', SCORE=SCORE+" + s.getScore() + ", WINS=WINS+" + s.hasWon() + ", PLAYED=PLAYED+1;");
				}
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			status.clear();
		}
	}
}
