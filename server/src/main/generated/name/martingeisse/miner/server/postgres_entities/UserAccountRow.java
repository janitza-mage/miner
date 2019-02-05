/*
 * This file was generated from the database schema.
 */
package name.martingeisse.miner.server.postgres_entities;

import com.querydsl.sql.dml.SQLInsertClause;
import name.martingeisse.miner.server.util.database.postgres.PostgresConnection;

import java.io.Serializable;

/**
 * This class represents rows from table 'UserAccount'.
 */
public class UserAccountRow implements Serializable {

    /**
     * Constructor.
     */
    public UserAccountRow() {
    }

    /**
     * the deleted
     */
    private Boolean deleted;

    /**
     * the id
     */
    private Long id;

    /**
     * the passwordHash
     */
    private String passwordHash;

    /**
     * the username
     */
    private String username;

    /**
     * Getter method for the deleted.
     * 
     * @return the deleted
     */
    public Boolean getDeleted() {
        return deleted;
    }

    /**
     * Setter method for the deleted.
     * 
     * @param deleted the deleted to set
     */
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * Getter method for the id.
     * 
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Setter method for the id.
     * 
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter method for the passwordHash.
     * 
     * @return the passwordHash
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Setter method for the passwordHash.
     * 
     * @param passwordHash the passwordHash to set
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Getter method for the username.
     * 
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter method for the username.
     * 
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Loads the instance with the specified ID.
     * 
     * @param connection the database connection
     * @param id the ID of the instance to load
     * @return the loaded instance
     */
    public static UserAccountRow loadById(PostgresConnection connection, Long id) {
        QUserAccountRow q = QUserAccountRow.UserAccount;
        return connection.query().select(q).from(q).where(q.id.eq(id)).fetchFirst();
    }

    /**
     * Inserts this instance into the database. This object must not have an ID yet.
     */
    public void insert(PostgresConnection connection) {
        if (id != null) {
        	throw new IllegalStateException("this object already has an id: " + id);
        }
        QUserAccountRow q = QUserAccountRow.UserAccount;
        SQLInsertClause insert = connection.insert(q);
        insert.set(q.deleted, deleted);
        insert.set(q.passwordHash, passwordHash);
        insert.set(q.username, username);
        id = insert.executeWithKey(Long.class);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "{UserAccountRow. deleted = " + deleted + ", id = " + id + ", passwordHash = " + passwordHash + ", username = " + username + "}";
    }

}

