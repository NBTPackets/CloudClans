package github.com.nbtpackets.database;

public record DbCreds(String host, int port, String database, String user, String pass) {
}