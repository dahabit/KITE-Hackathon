/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package com.cosmo.kite.usrmgmt;

import javax.json.JsonObject;

/**
 * The type Rc account.
 */
public class Account extends Entity {
  
  /**
   * The Account type.
   */
  protected AccountType accountType;
  /**
   * The Credential.
   */
  protected String credential;
  /**
   * The Email.
   */
  protected String email;
  /**
   * The Ext.
   */
  protected String ext = null;
  /**
   * The Password.
   */
  protected String password;
  /**
   * The Role.
   */
  protected AccountRole role;
  /**
   * The Username.
   */
  protected String username;
  
  /**
   * Instantiates a new Rc account.
   */
  public Account() {
  
  }
  
  /**
   * Instantiates a new Rc account.
   *
   * @param jsonObject the json object
   */
  public Account(JsonObject jsonObject) {
    this.username = jsonObject.getString("username");
    this.credential = jsonObject.getString("credential");
    this.password = jsonObject.getString("password");
    this.email = jsonObject.getString("email");
    this.ext = jsonObject.getString("ext", this.ext);
  }
  
  /**
   * Constructor for a Ring Central user, account accountType is free user by default
   *
   * @param username   account's username, displayed to other users
   * @param credential account's credential, can be an email or a phone number
   * @param password   account's password
   * @param email      account's email
   */
  public Account(String username, String credential, String password, String email) {
    this.username = username;
    this.credential = credential;
    this.password = password;
    this.email = email;
    this.ext = null;
    this.accountType = AccountType.FREE_ACCOUNT;
    this.role = AccountRole.CALLEE;
  }
  
  @Override
  public boolean equals(Object obj) {
    Account otherAcc = (Account) obj;
    return (this.username.equals(otherAcc.getUserName()) &&
      this.email.equals(otherAcc.getEmail()));
  }
  
  /**
   * Returns type of the account.
   *
   * @return type account type
   */
  public AccountType getAccountType() {
    return accountType;
  }
  
  /**
   * Sets account's type
   *
   * @param accountType the account type
   */
  public void setAccountType(AccountType accountType) {
    this.accountType = accountType;
  }
  
  /**
   * Returns account credential.
   *
   * @return account credential String
   */
  public String getCredential() {
    return credential;
  }
  
  /**
   * Returns account email.
   *
   * @return account email String
   */
  public String getEmail() {
    return email;
  }
  
  /**
   * Sets account's email
   *
   * @param email the email
   */
  public void setEmail(String email) {
    this.email = email;
  }
  
  /**
   * Returns account ext.
   *
   * @return account ext String
   */
  public String getExt() {
    return ext;
  }
  
  /**
   * Sets account's ext value, used to login if the credential is a phone number
   *
   * @param ext account's ext value
   */
  public void setExt(String ext) {
    this.ext = ext;
  }
  
  /**
   * Returns account password.
   *
   * @return account password String
   */
  public String getPassword() {
    return password;
  }
  
  /**
   * Returns role of the tester.
   *
   * @return role role
   */
  public AccountRole getRole() {
    return role;
  }
  
  /**
   * Sets account's role
   *
   * @param role the role
   */
  public void setRole(AccountRole role) {
    this.role = role;
  }
  
  /**
   * Returns username.
   *
   * @return user id String
   */
  public String getUserName() {
    return username;
  }
  
  /**
   * Sets account's username
   *
   * @param username the username
   */
  public void setUsername(String username) {
    this.username = username;
  }
}
