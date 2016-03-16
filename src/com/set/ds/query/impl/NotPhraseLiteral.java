package com.set.ds.query.impl;

import com.set.constants.query.QueryConst;
import com.set.ds.query.inter.QLLiteral;

public class NotPhraseLiteral implements QLLiteral{
	private String queryLiteral;

	public String getLiteral() {
		return queryLiteral;
	}

	public void setNotPhraseLiteral(String queryLiteral) {
		this.queryLiteral = queryLiteral;
	}

	@Override
	public int getType() {
		return QueryConst.NOTPHRASE;
	}
}
