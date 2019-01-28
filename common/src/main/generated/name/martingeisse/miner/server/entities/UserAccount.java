/*
 * This file was generated from the database schema.
 */
package name.martingeisse.miner.server.entities;

import java.io.Serializable;

/**
 * This class represents rows from table 'UserAccount'.
 */
public class UserAccount implements Serializable {

    /**
     * Constructor.
     */
    public UserAccount() {
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "{UserAccount. deleted = " + deleted + ", id = " + id + ", passwordHash = " + passwordHash + ", username = " + username + "}";
    }

}

