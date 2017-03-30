package com.six.compactdb.dialect;

public abstract interface Dialect
{
  public abstract String getName();

  public abstract String getPrefixEsc();

  public abstract String getSuffixEsc();

  public abstract String getLimitQueryScript(String paramString, int paramInt1, int paramInt2);

  public abstract String dateValue(String paramString);
}
