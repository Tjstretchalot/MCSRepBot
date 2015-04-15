package me.timothy.mcsrepbot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import me.timothy.bots.Database;
import me.timothy.mcsrepbot.models.MCSUser;
import me.timothy.mcsrepbot.models.Response;

/**
 * Handles the connection with the MCSRep Database. All 
 * interaction through the database should occur here.
 * 
 * NOT guaranteed to be thread-safe
 * 
 * @author Timothy
 */
public class MCSRepDatabase extends Database {
	private Connection connection;
	
	/**
	 * Connects to the database using the specified information
	 * 
	 * @param url      - the url to the database
	 * @param username - the username to the database
	 * @param password - the password to the database
	 */
	public MCSRepDatabase(String url, String username, String password) {
		try {
			connection = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	// ===========================================================
	// |                                                         |
	// |                       FULLNAMES                         |
	// |                                                         |
	// ===========================================================
	
	/*
	 * fullnames
	 *   id         - int primary key
	 *   fullname   - varchar(50) not null
	 *   
	 *   created_at - datetime not null
	 *   updated_at - datetime not null
	 */

	@Override
	public void addFullname(String fullname) {
		try {
			PreparedStatement statement = connection.prepareStatement("INSERT INTO fullnames (fullname, created_at, updated_at) VALUES(?, ?, ?)");
			statement.setString(1, fullname);
			long now = System.currentTimeMillis();
			statement.setTimestamp(2, new Timestamp(now));
			statement.setTimestamp(3, new Timestamp(now));
			
			statement.executeUpdate();
			statement.close();
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean containsFullname(String fullname) {
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM fullnames WHERE fullname=?");
			statement.setString(1, fullname);
			
			ResultSet results = statement.executeQuery();
			boolean hasOne = results.first();
			results.close();
			statement.close();
			
			return hasOne;
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	// ===========================================================
	// |                                                         |
	// |                       RESPONSES                         |
	// |                                                         |
	// ===========================================================
	
	/*
	 * responses
	 *   id         - int primary key
	 *   title      - varchar(50) unique not null
	 *   body       - text not null
	 *   
	 *   created_at - datetime not null
	 *   updated_at - datetime not null
	 */
	
	/**
	 * Get the response object by searching for a specific
	 * title.
	 * 
	 * @param title the title to search for
	 * @return the response object or null
	 */
	public Response getResponseByTitle(String title) {
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM responses WHERE title=?");
			statement.setString(1, title);
			
			ResultSet results = statement.executeQuery();
			Response resp = results.first() ? getResponseFromSet(results) : null;
			results.close();
			statement.close();
			return resp;
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Gets the response object from the currently selected
	 * location in the result set
	 * @param results the result set
	 * @return the response object
	 */
	private Response getResponseFromSet(ResultSet results) throws SQLException {
		return new Response(results.getInt("id"), results.getString("title"), results.getString("text"),
							results.getTimestamp("created_at"), results.getTimestamp("updated_at"));
	}
	
	
	// ===========================================================
	// |                                                         |
	// |                          USERS                          |
	// |                                                         |
	// ===========================================================
	
	/*
	 * users
	 *   id         - int primary key
	 *   username   - varchar(255) unique
	 *   rep_url    - text
	 *   
	 *   created_at - datetime not null
	 *   updated_at - datetime not null
	 */
	
	/**
	 * Finds the user in the database with the username exactly
	 * matching the specified username. If it does not exist, a
	 * new one is created.
	 * 
	 * @param username the username to search for
	 * @return the user if it exists, otherwise a new one that was created and saved
	 */
	public MCSUser getUserByUsername(String username) {
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
			statement.setString(1, username);
			
			ResultSet results = statement.executeQuery();
			MCSUser result = (results.first() ? getUserFromSet(results) : null);
			results.close();
			statement.close();
			
			if(result == null) {
				long time = System.currentTimeMillis();
				result = new MCSUser(-1, username, null, new Timestamp(time), new Timestamp(time));
				updateOrSaveMCSUser(result);
			}
			return result;
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * <p>If the user has an strictly greater than 0, then that user
	 * is updated. If a user with that id doesn't exist, then no
	 * change is effected.</p>
	 * 
	 * <p>If the users id is less than 0, the user is inserted into 
	 * the database, and the id is updated to match the newly generated id.</p>
	 * 
	 * <p>If the id is 0, an error is thrown to ensure you are getting the expected
	 * behavior</p>
	 * 
	 * @param user the user to update or save
	 */
	public void updateOrSaveMCSUser(MCSUser user) {
		if(!user.isValid()) {
			throw new IllegalArgumentException("Only valid users are saved (using user.isValid)");
		}
		
		try {
			PreparedStatement statement;
			if(user.id < 0) {
				statement = connection.prepareStatement("INSERT INTO users (username, rep_url, created_at, updated_at) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else {
				statement = connection.prepareStatement("UPDATE users SET username=?, rep_url=?, created_at=?, updated_at=? WHERE id=?");
			}

			int counter = 1;
			statement.setString(counter++, user.username);
			statement.setString(counter++, user.repURL);
			statement.setTimestamp(counter++, user.createdAt);
			statement.setTimestamp(counter++, user.updatedAt);
			
			if(user.id > 0) {
				statement.setInt(counter++, user.id);
			}
			
			statement.executeUpdate();
			
			if(user.id < 0) {
				ResultSet keys = statement.getGeneratedKeys();
				if(keys.next()) {
					user.id = keys.getInt(1);
				}else {
					throw new IllegalStateException("No generated keys from user column - did you set id to autoincrementing?");
				}
				keys.close();
			}
			statement.close();
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Parses the current row from results into an MCSUser
	 * 
	 * @param results the results
	 * @return the parsed user
	 * @throws SQLException if a sql exception occurs, such as the set being closed or a missing column
	 */
	private MCSUser getUserFromSet(ResultSet results) throws SQLException {
		return new MCSUser(results.getInt("id"), results.getString("username"), results.getString("rep_url"),
				results.getTimestamp("created_at"), results.getTimestamp("updated_at"));
	}
}
