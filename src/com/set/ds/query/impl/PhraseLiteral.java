package com.set.ds.query.impl;

import com.set.constants.query.QueryConst;
import com.set.ds.query.inter.QLLiteral;

public class PhraseLiteral implements QLLiteral{
	private String phraseLiteral;

	public String getLiteral() {
		return phraseLiteral;
	}

	public void setPhraseLiteral(String phraseLiteral) {
		this.phraseLiteral = phraseLiteral;
	}

	@Override
	public int getType() {
		return QueryConst.PHRASE;
	}
}
