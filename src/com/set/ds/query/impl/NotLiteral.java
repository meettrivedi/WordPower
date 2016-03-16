package com.set.ds.query.impl;

import com.set.constants.query.QueryConst;
import com.set.ds.query.inter.QLLiteral;

public class NotLiteral implements QLLiteral{
	private String queryLiteral;

	public String getLiteral() {
		return queryLiteral;
	}

	public void setNotLiteral(String literal) {
		this.queryLiteral = literal;
	}

	@Override
	public int getType() {
		return QueryConst.NOT;
	}
	
}
