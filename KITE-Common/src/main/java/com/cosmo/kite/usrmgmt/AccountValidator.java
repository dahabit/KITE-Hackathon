/*
 * Copyright (C) CoSMo Software Consulting Pte. Ltd. - All Rights Reserved
 */

package com.cosmo.kite.usrmgmt;

import com.cosmo.kite.pool.Pool.Validator;

/**
 * The type Rc account validator.
 */
public class AccountValidator implements Validator<Account> {
  
  @Override
  public void invalidate(Account account) {
    // Do Nothing
  }
  
  @Override
  public boolean isValid(Account account) {
    return true;
  }
  
}
