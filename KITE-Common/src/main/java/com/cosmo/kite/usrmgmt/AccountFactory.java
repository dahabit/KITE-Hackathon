/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package com.cosmo.kite.usrmgmt;

import com.cosmo.kite.pool.ObjectFactory;

import java.util.List;

/**
 * The type Rc account factory.
 */
public class AccountFactory implements ObjectFactory<Account> {
  
  private List<Account> accountList;
  private int index;
  
  /**
   * Instantiates a new Rc account factory.
   *
   * @param accountList the account list
   */
  public AccountFactory(List<Account> accountList) {
    this.accountList = accountList;
  }
  
  @Override
  public Account createNew() {
    return this.accountList.get(this.index++);
  }
  
}
